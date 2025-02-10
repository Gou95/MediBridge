package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.StockitsListAdapter;
import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.StockitsViewModel;
import com.indosoft.medibridge.databinding.ActivityRecentStockistBinding;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RecentStockistActivity extends AppCompatActivity {
    ActivityRecentStockistBinding binding;
    StockitsViewModel stockitsViewModel;
    StockitsListAdapter adapter;
    ArrayList<StockitsResponse> list = new ArrayList<>();
    private String startDateSelected = null;
    private String lastDateSelected = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityRecentStockistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        stockitsViewModel = new ViewModelProvider(this).get(StockitsViewModel.class);
        stockitsViewModel.init(this);

        onAttachObservers();
        initClicks();
        startNetworkService();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);

        String dealerName = getIntent().getStringExtra("dealerName");
        String dealerId = getIntent().getStringExtra("dealerId");
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        binding.txtDealername.setText(dealerName);
        stockitsViewModel.stockitsOrderList(retailerId,dealerId);

        adapter = new StockitsListAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

      //  Toast.makeText(this, retailerId, Toast.LENGTH_SHORT).show();

    }
    private void initClicks() {

        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnPrintInvoice.setOnClickListener(v -> {
            printOrderInvoice();
        });
        binding.txtStartDate.setOnClickListener(v -> {
            openCalendarDialog("start");
        });

        binding.txtLastDate.setOnClickListener(v -> {
            openCalendarDialog("last");
        });
        binding.imgWhatsApp.setOnClickListener(v -> {

        });
        binding.autoMedicineName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterListByProductName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        stockitsViewModel.getLiveData().observe(this, responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null) {
                list.clear();

                String dealerId = getIntent().getStringExtra("dealerId");
                for (StockitsResponse response : responses) {
                    if (response.getDealerId().equals(dealerId)) {
                        list.add(response);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void openCalendarDialog(String dateType) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View calendarView = inflater.inflate(R.layout.custom_calendar, null);

        CalendarView calendar = calendarView.findViewById(R.id.calendarView);
        Button btnClose = calendarView.findViewById(R.id.btnClose);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(calendarView)
                .create();
        dialog.show();

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            String formattedDate = dayOfMonth + "." + getMonthName(month) + "." + year;

            if ("start".equals(dateType)) {
                startDateSelected = selectedDate;
                binding.txtStartDate.setText(formattedDate);
            } else if ("last".equals(dateType)) {
                lastDateSelected = selectedDate;
                binding.txtLastDate.setText(formattedDate);
            }

            Toast.makeText(this, "Start: " + startDateSelected + " End: " + lastDateSelected, Toast.LENGTH_SHORT).show();
            filterListByDateRange();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }
    private String getMonthName(int month) {
        String[] monthNames = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        return monthNames[month]; // Month is 0-based, so this is correct
    }
    private void filterListByDateRange() {
        if (startDateSelected == null || lastDateSelected == null) {
            Toast.makeText(this, "Please select both start and end dates.", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<StockitsResponse> filteredList = new ArrayList<>();
        for (StockitsResponse response : list) {
            String addTime = response.getAddtime();

            if (isDateInRange(addTime, startDateSelected, lastDateSelected)) {
                filteredList.add(response);
            }
        }

        adapter.updateList(filteredList);
    }
    private boolean isDateInRange(String date, String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date targetDate = sdf.parse(date);
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return (targetDate.equals(start) || targetDate.after(start)) &&
                    (targetDate.equals(end) || targetDate.before(end));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    private void printOrderInvoice() {

        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

        String orderNo = getIntent().getStringExtra("dealerId");
        String dateTime = getIntent().getStringExtra("dealerName");

        PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                String printContent = generateInvoiceContent(orderNo, dateTime);
                String[] lines = printContent.split("\n");

                int linesPerPage = 40; // Number of lines that fit per page
                int totalPages = (int) Math.ceil((double) lines.length / linesPerPage);

                callback.onLayoutFinished(new PrintDocumentInfo.Builder("Invoice")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(totalPages)
                        .build(), true);
            }

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                try {
                    PdfDocument document = new PdfDocument();
                    String printContent = generateInvoiceContent(orderNo, dateTime);
                    String[] lines = printContent.split("\n");

                    int linesPerPage = 40; // Lines per page
                    int totalPages = (int) Math.ceil((double) lines.length / linesPerPage);

                    for (int pageNum = 0; pageNum < totalPages; pageNum++) {
                        if (cancellationSignal.isCanceled()) {
                            callback.onWriteCancelled();
                            document.close();
                            return;
                        }

                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, pageNum + 1).create();
                        PdfDocument.Page page = document.startPage(pageInfo);

                        Canvas canvas = page.getCanvas();
                        Paint paint = new Paint();
                        paint.setColor(Color.BLACK);
                        paint.setTextSize(16);

                        float x = 10;
                        float y = 20;

                        // Write the lines for the current page
                        int startLine = pageNum * linesPerPage;
                        int endLine = Math.min(startLine + linesPerPage, lines.length);
                        for (int i = startLine; i < endLine; i++) {
                            canvas.drawText(lines[i], x, y, paint);
                            y += 20; // Move down for the next line
                        }

                        document.finishPage(page);
                    }

                    // Write the document to the output stream
                    FileOutputStream out = new FileOutputStream(destination.getFileDescriptor());
                    document.writeTo(out);
                    document.close();

                    // Indicate that writing is complete
                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (IOException e) {
                    Log.e("PrintError", "Error writing document: " + e.getMessage());
                    callback.onWriteFailed("Error writing document");
                }
            }
        };

        // Start the print job
        printManager.print("Invoice_" + orderNo, printAdapter, new PrintAttributes.Builder().build());
    }
    private String generateInvoiceContent(String orderNo, String dateTime) {
        StringBuilder content = new StringBuilder();

        // content.append("Order Id: #").append(orderNo).append("\n\n");
        content.append("dealerName: ").append(dateTime).append("\n\n");

        content.append("------------------------------------------------\n");

        content.append("Product Details:\n");

        content.append("------------------------------------------------\n");

        if (list != null && !list.isEmpty()) {
            int serialNumber = 1;

            for (StockitsResponse response : list) {
                content.append(serialNumber).append(". ").append(response.getProductName()).append("\n");
                content.append("Unit: ").append(response.getUnitName()).append("\n");
                content.append("Quantity: ").append(response.getOrderQty()).append("\n");
                content.append("Delivery Day: ").append(response.getDeliveryDay()).append("\n");
                content.append("------------------------------------------------\n");

                serialNumber++;
            }
        } else {
            content.append("No products found in the order.\n");
        }

        return content.toString();
    }
    private void filterListByProductName(String productName) {
        if (productName.isEmpty()) {
            adapter.updateList(list); // Show the full list if input is empty
            return;
        }

        ArrayList<StockitsResponse> filteredList = new ArrayList<>();
        for (StockitsResponse response : list) {
            if (response.getProductName() != null && response.getProductName().toLowerCase().contains(productName.toLowerCase())) {
                filteredList.add(response);
            }
        }

        adapter.updateList(filteredList);
    }
    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
        Log.d("LoginActivity", "NetworkCheckService started");
    }

}