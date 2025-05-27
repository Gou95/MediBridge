package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.StockitsListAdapter;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.StockitsViewModel;
import com.indosoft.medibridge.databinding.ActivityRecentStockistBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecentStockistActivity extends AppCompatActivity {
    ActivityRecentStockistBinding binding;
    StockitsViewModel stockitsViewModel;
    StockitsListAdapter adapter;
    ArrayList<StockitsResponse> list = new ArrayList<>();
    ArrayList<StockitsResponse> allResponses = new ArrayList<>();
    private String startDateSelected = null;
    private String lastDateSelected = null;
    private boolean isReceiverRegistered = false;
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
        setDefaultDates();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);

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

        binding.txtStartDate.setOnClickListener(v -> {
            openCalendarDialog("start");
        });

        binding.txtLastDate.setOnClickListener(v -> {
            openCalendarDialog("last");
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
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale > 1.0f) {
            newConfig.fontScale = 1.0f;
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
        super.onConfigurationChanged(newConfig);
    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        stockitsViewModel.getLiveData().observe(this, responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null) {
                allResponses.clear();   // ✅ Save master list
                allResponses.addAll(responses);

                filterListByDateRange(); // ✅ Filter based on selected dates
                adapter.notifyDataSetChanged();

                binding.btnPrintInvoice.setOnClickListener(v -> generateInvoicePdf());
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
            String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, (month + 1), year);

            if ("start".equals(dateType)) {
                startDateSelected = selectedDate;
                binding.txtStartDate.setText(selectedDate);
            } else if ("last".equals(dateType)) {
                lastDateSelected = selectedDate;
                binding.txtLastDate.setText(selectedDate);
            }
            dialog.dismiss();
            filterListByDateRange();

        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void filterListByDateRange() {
        if (startDateSelected == null || lastDateSelected == null) {
            // Show full list if dates not selected
            list.clear();
            list.addAll(allResponses);
            adapter.updateList(list);
            return;
        }

        ArrayList<StockitsResponse> filteredList = new ArrayList<>();
        SimpleDateFormat apiFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat filterFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            Date start = filterFormat.parse(startDateSelected);
            Date end = filterFormat.parse(lastDateSelected);

            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            end = cal.getTime();

            for (StockitsResponse response : allResponses) {
                Date targetDate = apiFormat.parse(response.getAddtime());
                if (targetDate != null && !targetDate.before(start) && !targetDate.after(end)) {
                    filteredList.add(response);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        list.clear();
        list.addAll(filteredList);
        adapter.updateList(list);
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
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {

                return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
            }
        }
        return false;
    }
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {

                reloadData();
            } else {

            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (!isReceiverRegistered) {
            registerReceiver(networkReceiver, filter);
            isReceiverRegistered = true;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(networkReceiver);
            isReceiverRegistered = false;
        }
    }
    private void reloadData() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 5000);
    }
    private void setDefaultDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        lastDateSelected = sdf.format(calendar.getTime());
        binding.txtLastDate.setText(lastDateSelected);

        // Start Date = Current Date - 7 Days
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        startDateSelected = sdf.format(calendar.getTime());
        binding.txtStartDate.setText(startDateSelected);
    }
    private void generateInvoicePdf() {
        String dealer = getIntent().getStringExtra("dealerName");
        if (dealer == null) dealer = "Unknown_Stockist";

        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "No order details available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clean the dealer name to make it filename-safe
        dealer = dealer.replaceAll("[^a-zA-Z0-9\\-_ ]", "").replaceAll(" +", "_");

        // Create timestamp for filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // Final filename
        String fileName = dealer + "_Invoice_" + timeStamp + ".pdf";

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        Paint dealerPaint = new Paint();
        Paint borderPaint = new Paint();

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(1);

        int pageWidth = 600;
        int pageHeight = 800;
        int marginTop = 50;
        int rowHeight = 40;
        int availableHeight = pageHeight - 150;

        int col1 = 150, col2 = 100, col3 = 100, col4 = 100, col5 = 80;
        int itemIndex = 0;

        while (itemIndex < list.size()) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // Title
            titlePaint.setTextSize(20);
            titlePaint.setColor(Color.BLACK);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Purchase Order", 250, marginTop, titlePaint);

            // Dealer Info
            dealerPaint.setTextSize(16);
            dealerPaint.setColor(Color.DKGRAY);
            dealerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Stockist: " + dealer, 50, marginTop + 30, dealerPaint);

            int y = marginTop + 70;
            paint.setTextSize(14);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            int startX = 50;
            int endX = startX + col1 + col2 + col3 + col4 + col5;

            // Table headers
            canvas.drawText("Product Name", startX + 10, y + 25, paint);
            canvas.drawText("Quantity", startX + col1 + 10, y + 25, paint);
            canvas.drawText("Unit", startX + col1 + col2 + 10, y + 25, paint);
            canvas.drawText("Delivery Day", startX + col1 + col2 + col3 + 10, y + 25, paint);
            canvas.drawText("Remarks", startX + col1 + col2 + col3 + col4 + 10, y + 25, paint);

            canvas.drawRect(startX, y, endX, y + rowHeight, borderPaint);
            y += rowHeight;
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            while (itemIndex < list.size() && y + rowHeight < availableHeight) {
                StockitsResponse item = list.get(itemIndex);

                String productName = item.getProductName() != null ? item.getProductName() : "";
                String quantity = item.getOrderQty() != null ? item.getOrderQty() : "-";
                String unit = item.getUnitName() != null ? item.getUnitName() : "-";
                String deliveryDay = item.getDeliveryDay() != null ? item.getDeliveryDay() : "-";
                String remarks = "";

                int textY = y;
                int rowLines = 1;

                if (productName.equalsIgnoreCase("UNLISTED MEDICINES")) {
                    String unlisted = item.getUnlistedMedicines() != null ? (String) item.getUnlistedMedicines() : "";
                    String[] lines = unlisted.split(",");

                    canvas.drawText("UNLISTED MEDICINES", startX + 10, textY + 20, paint);
                    textY += 20;
                    rowLines++;

                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
                    paint.setTextSize(12);

                    for (String line : lines) {
                        canvas.drawText(line.trim(), startX + 10, textY + 20, paint);
                        textY += 20;
                        rowLines++;
                    }

                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    paint.setTextSize(14);
                } else {
                    List<String> wrapped = wrapText(productName, paint, col1 - 20);
                    for (String line : wrapped) {
                        canvas.drawText(line, startX + 10, textY + 20, paint);
                        textY += 20;
                        rowLines++;
                    }
                }

                int actualRowHeight = Math.max(rowHeight, rowLines * 20 + 10);

                canvas.drawText(quantity, startX + col1 + 10, y + 25, paint);
                canvas.drawText(unit, startX + col1 + col2 + 10, y + 25, paint);
                canvas.drawText(deliveryDay, startX + col1 + col2 + col3 + 10, y + 25, paint);
                canvas.drawText(remarks, startX + col1 + col2 + col3 + col4 + 10, y + 25, paint);

                canvas.drawRect(startX, y, endX, y + actualRowHeight, borderPaint);

                y += actualRowHeight;
                itemIndex++;
            }

            pdfDocument.finishPage(page);
        }

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF saved as " + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
        openPdf(file);
    }




    private List<String> wrapText(String text, Paint paint, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            float textWidth = paint.measureText(testLine);

            if (textWidth < maxWidth) {
                currentLine.append(word).append(" ");
            } else {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder(word).append(" ");
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }
    private void openPdf(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF Viewer Installed", Toast.LENGTH_SHORT).show();
        }
    }

}