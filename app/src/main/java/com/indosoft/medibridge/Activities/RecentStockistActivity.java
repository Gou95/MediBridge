package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
    private String orderDate = null;
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

        orderDate = list.get(0).getAddtime(); // ✅ Get order date from first item
        dealer = dealer.replaceAll("[^a-zA-Z0-9\\-_ ]", "").replaceAll(" +", "_");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
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
        int rowHeight = 40;

        int col1 = 140, col2 = 100, col3 = 100, col4 = 80, col5 = 100;
        int itemIndex = 0;
        int pageNumber = 1;

        while (itemIndex < list.size()) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // 1. Draw QR Code (Top-Right)
            // 1. Draw QR Code on Top-Right
            Bitmap qrBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.qr_code);
            if (qrBitmap != null) {
                Rect src = new Rect(0, 0, qrBitmap.getWidth(), qrBitmap.getHeight());
                Rect dest = new Rect(pageWidth - 130, 30, pageWidth - 30, 130); // QR top-right corner
                canvas.drawBitmap(qrBitmap, src, dest, null);

                // 2. Draw text below QR
                Paint qrTextPaint = new Paint();
                qrTextPaint.setColor(Color.BLACK);
                qrTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                qrTextPaint.setTextSize(12);

// First line: "Scan & download"
                String line1 = "Scan & download";
                float textWidth1 = qrTextPaint.measureText(line1);
                float textX1 = pageWidth - 130 + (100 - textWidth1) / 2;
                float textY1 = 145;
                canvas.drawText(line1, textX1, textY1, qrTextPaint);

// Second line: "Medibro" (bigger + bold)
                Paint qrTextBoldPaint = new Paint();
                qrTextBoldPaint.setColor(Color.BLACK);
                qrTextBoldPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                qrTextBoldPaint.setTextSize(14);

                String line2 = "Medibro App";
                float textWidth2 = qrTextBoldPaint.measureText(line2);
                float textX2 = pageWidth - 130 + (100 - textWidth2) / 2;
                float textY2 = textY1 + 15; // a little below first line
                canvas.drawText(line2, textX2, textY2, qrTextBoldPaint);
            }

            String retailerName = "Shop Name: " + AppSession.getInstance(this).getValue(Constants.RELAILER_NAME);
            String retailerAddress = "Address: " + AppSession.getInstance(this).getValue(Constants.PERMANENTADDRESS);
            String DL = "DL: " + AppSession.getInstance(this).getValue(Constants.RETAILER_DL);
            String GST = "GST: " + AppSession.getInstance(this).getValue(Constants.RETAILER_GST);

            Paint linePaint = new Paint();
            linePaint.setTextSize(18);
            linePaint.setColor(Color.BLACK);
            linePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            Paint namePaint = new Paint();
            namePaint.setTextSize(22);
            namePaint.setColor(Color.BLACK);
            namePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            float startX1 = 30;
            float startY = 40;
            float lineSpacing = 25;

            canvas.drawText(retailerName, startX1, startY, namePaint);                           // Line 1: Shop Name (bold)
            canvas.drawText(retailerAddress, startX1, startY + lineSpacing, linePaint);         // Line 2: Address
            canvas.drawText(DL, startX1, startY + 2 * lineSpacing, linePaint);                  // Line 3: DL
            canvas.drawText(GST, startX1, startY + 3 * lineSpacing, linePaint);                 // Line 4: GST

            titlePaint.setTextSize(20);
            titlePaint.setColor(Color.BLACK);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            String titleText = "Purchase List";
            float titleWidth = titlePaint.measureText(titleText);
            canvas.drawText(titleText, (pageWidth - titleWidth) / 2, 160, titlePaint);

            // 3. Draw Expiry Date Centered Below Title
            dealerPaint.setTextSize(18);
            dealerPaint.setColor(Color.BLACK);
            dealerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            String dateText = "Order Date: " + orderDate;
            float dateTextWidth = dealerPaint.measureText(dateText);
            canvas.drawText(dateText, (pageWidth - dateTextWidth) / 2, 180, dealerPaint);

            dealerPaint.setTextSize(18);
            dealerPaint.setColor(Color.BLACK);
            dealerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            String dealerName = "" + dealer;
            float dealerTextWidth = dealerPaint.measureText(dealerName);
            canvas.drawText(dealerName, (pageWidth - dealerTextWidth) / 2, 200, dealerPaint);

            int y = 210;
            paint.setTextSize(14);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            int startX = 50;
            int endX = startX + col1 + col2 + col3 + col4 + col5;
            int headerBottom = y + rowHeight;

            canvas.drawText("Product Name", startX + 10, y + 25, paint);
            canvas.drawText("Quantity", startX + col1 + 10, y + 25, paint);
            canvas.drawText("Unit", startX + col1 + col2 + 10, y + 25, paint);
            canvas.drawText("Delivery", startX + col1 + col2 + col3 + 10, y + 25, paint);
            canvas.drawText("Remarks", startX + col1 + col2 + col3 + col4 + 10, y + 25, paint);

            canvas.drawRect(startX, y, endX, headerBottom, borderPaint);
            y += rowHeight;
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            while (itemIndex < list.size() && y + rowHeight < pageHeight - 50) {
                StockitsResponse item = list.get(itemIndex);

                String productName = safe(item.getProductName());
                String quantity = safe(item.getOrderQty());
                String unitName = safe(item.getUnitName());
                String deliveryDay = safe(item.getDeliveryDay(), "-");
                String remarks = "";
                String unlistedMedicines = safe((String) item.getUnlistedMedicines());

                int rowHeightAdjusted = rowHeight;
                int textY = y + 20;

                if ("UNLISTED MEDICINES".equals(productName)) {
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText(productName, startX + 10, textY, paint);
                    textY += 20;
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    List<String> wrapped = wrapText(unlistedMedicines, paint, col1 - 20);
                    for (String line : wrapped) {
                        canvas.drawText(line, startX + 10, textY, paint);
                        textY += 20;
                    }
                    rowHeightAdjusted = wrapped.size() * 20 + 20;
                } else {
                    List<String> wrappedName = wrapText(productName, paint, col1 - 20);
                    for (String line : wrappedName) {
                        canvas.drawText(line, startX + 10, textY, paint);
                        textY += 20;
                    }

                    canvas.drawText(!quantity.isEmpty() ? quantity : "-", startX + col1 + 10, y + 25, paint);
                    canvas.drawText(!unitName.isEmpty() ? unitName : "-", startX + col1 + col2 + 10, y + 25, paint);
                    canvas.drawText(deliveryDay, startX + col1 + col2 + col3 + 10, y + 25, paint);
                    rowHeightAdjusted = wrappedName.size() * 20;
                }

                // Stockist name (wrapped)
                String[] stockistLines = remarks.split(" ", 2);
                canvas.drawText(stockistLines[0], startX + col1 + col2 + col3 + col4 + 10, y + 20, paint);
                if (stockistLines.length > 1) {
                    canvas.drawText(stockistLines[1], startX + col1 + col2 + col3 + col4 + 10, y + 40, paint);
                    rowHeightAdjusted = Math.max(rowHeightAdjusted, 60);
                } else {
                    rowHeightAdjusted = Math.max(rowHeightAdjusted, 40);
                }

                canvas.drawRect(startX, y, endX, y + rowHeightAdjusted, borderPaint);
                y += rowHeightAdjusted;
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

    private String safe(String val) {
        return val != null ? val : "";
    }
    private String safe(String val, String defaultVal) {
        return val != null ? val : defaultVal;
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
}