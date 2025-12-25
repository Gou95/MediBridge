package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.ExpiryListAdapter;
import com.indosoft.medibridge.Model.ExpiryListResponse;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.Model.RecentStockitsResponse;
import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.ExpiryListViewModel;
import com.indosoft.medibridge.ViewModel.OrderDetailsViewModel;
import com.indosoft.medibridge.databinding.ActivityExpiryListBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class
ExpiryListActivity extends AppCompatActivity {

    ActivityExpiryListBinding binding;
    ArrayList<OrderDetailsResponse> list = new ArrayList<>();
    ArrayList<OrderDetailsResponse> originalList = new ArrayList<>();
    ExpiryListAdapter adapter;
    OrderDetailsViewModel viewModel;
    String orderDate = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityExpiryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);
        viewModel.init(this);
        viewModel.getOrderDetailsData(AppSession.getInstance(this).getValue(Constants.RELAILER_ID));
        initCliks();
        onAttachObservers();

        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);
        adapter = new ExpiryListAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(this, responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null) {
                originalList.clear();
                originalList.addAll(responses);

                list.clear();
                String currentMonthYear = getCurrentMonthYear();
                binding.autoMonth.setText(currentMonthYear);

           applyAllFilters();
            } else {
                Log.e("DEBUG", "API Response is NULL");
            }
        });
    }

    private void initCliks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.autoMedicine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyAllFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.autoStockist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyAllFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.autoMonth.setOnClickListener(v -> showCalendarDialog() );
        TextView title = binding.txtExpiryList;
        SpannableString spannable = new SpannableString("Expiry List");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 7, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);
        binding.btnPrintOrder.setOnClickListener(v -> generateInvoicePdf());

    }
    private String getCurrentMonthYear() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        return month + "/" + year;
    }


    private void applyAllFilters() {
        String medicineFilter = binding.autoMedicine.getText().toString().toLowerCase().trim();
        String stockistFilter = binding.autoStockist.getText().toString().toLowerCase().trim();
        String monthFilter = binding.autoMonth.getText().toString().trim();

        ArrayList<OrderDetailsResponse> filteredList = new ArrayList<>();
        for (OrderDetailsResponse response : originalList) {
            boolean matchesMedicine = response.getProductName() != null &&
                    response.getProductName().toLowerCase().contains(medicineFilter);

            boolean matchesStockist = response.getDealerName() != null &&
                    response.getDealerName().toLowerCase().contains(stockistFilter);

            boolean matchesMonth = response.getExpiryMonth() != null &&
                    response.getExpiryMonth().trim().equalsIgnoreCase(monthFilter);

            if ((medicineFilter.isEmpty() || matchesMedicine) &&
                    (stockistFilter.isEmpty() || matchesStockist) &&
                    (monthFilter.isEmpty() || matchesMonth)) {
                filteredList.add(response);
            }
        }

        // 👇 Add this to sync PDF list
        list.clear();
        list.addAll(filteredList);

        adapter.updateList(filteredList);
    }


    private void showCalendarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.calendar_view, null);
        builder.setView(dialogView);

        final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        // Hide the day spinner to allow only month/year selection
        int daySpinnerId = getResources().getIdentifier("android:id/day", null, null);
        if (daySpinnerId != 0) {
            View daySpinner = datePicker.findViewById(daySpinnerId);
            if(daySpinner != null){
                daySpinner.setVisibility(View.GONE);
            }
        }

        TextView btnCancel = dialogView.findViewById(R.id.txt_cancel);
        TextView btnOk = dialogView.findViewById(R.id.txt_ok);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            // Get month (0-indexed) and year from the DatePicker
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();

            String selectedMonthYear = month + "/" + year;
            binding.autoMonth.setText(selectedMonthYear);
//            filterByMonth(selectedMonthYear);
            applyAllFilters();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale > 1.0f) {
            newConfig.fontScale = 1.0f;
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
        super.onConfigurationChanged(newConfig);
    }

    private void generateInvoicePdf() {
        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "No order details available", Toast.LENGTH_SHORT).show();
            return;
        }

        orderDate = list.get(0).getExpiryMonth();
        String timestamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Expiry_list" + timestamp + ".pdf");

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
            String titleText = "Expiry List";
            float titleWidth = titlePaint.measureText(titleText);
            canvas.drawText(titleText, (pageWidth - titleWidth) / 2, 160, titlePaint);

            // 3. Draw Expiry Date Centered Below Title
            dealerPaint.setTextSize(18);
            dealerPaint.setColor(Color.BLACK);
            dealerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            String dateText = "Exp Date: " + orderDate;
            float dateTextWidth = dealerPaint.measureText(dateText);
            canvas.drawText(dateText, (pageWidth - dateTextWidth) / 2, 190, dealerPaint);

            // 4. Start Table below expiry info
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
            canvas.drawText("Stockist", startX + col1 + col2 + col3 + col4 + 10, y + 25, paint);
            canvas.drawRect(startX, y, endX, headerBottom, borderPaint);
            y += rowHeight;
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            while (itemIndex < list.size() && y + rowHeight < pageHeight - 50) {
                OrderDetailsResponse item = list.get(itemIndex);

                String productName = safe(item.getProductName());
                String quantity = safe(item.getOrderQty());
                String unitName = safe(item.getUnitName());
                String deliveryDay = safe(item.getDeliveryDay(), "-");
                String stockistName = safe(item.getDealerName(), "Unknown");
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
                String[] stockistLines = stockistName.split(" ", 2);
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

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF Saved Successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Save PDF", Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }

        openPdf(file);
    }

    private String safe(String val) {
        return val != null ? val : "";
    }
    private String safe(String val, String defaultVal) {
        return val != null ? val : defaultVal;
    }
    private List<String> wrapText(String text, Paint paint, int width) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }

        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            if (paint.measureText(line + word) <= width) {
                line.append(word).append(" ");
            } else {
                lines.add(line.toString().trim());
                line = new StringBuilder(word + " ");
            }
        }
        if (line.length() > 0) {
            lines.add(line.toString().trim());
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