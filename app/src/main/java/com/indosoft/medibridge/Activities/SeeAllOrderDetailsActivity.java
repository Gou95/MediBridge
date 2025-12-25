package com.indosoft.medibridge.Activities;

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
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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

import com.indosoft.medibridge.Adapter.AllOrderDetailAdapter;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.OrderDetailsViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.databinding.ActivitySeeAllOrderDetailsBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SeeAllOrderDetailsActivity extends AppCompatActivity {
    ActivitySeeAllOrderDetailsBinding binding;
    OrderDetailsViewModel viewModel;
    SignUpViewModel sign;
    ArrayList<OrderDetailsResponse> list = new ArrayList<>();
    AllOrderDetailAdapter adapter;
    private boolean isReceiverRegistered = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivitySeeAllOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);
        viewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);

        onAttachObserver();
        initClicks();
        startNetworkService();

        String retailerId = getIntent().getStringExtra("retailerId");
        String dealerId = getIntent().getStringExtra("dealerId");
        String orderNo = getIntent().getStringExtra("orderNo");
        String dateTime = getIntent().getStringExtra("dot");
        String orderStatus = getIntent().getStringExtra("orderStatus");
        String dealer = getIntent().getStringExtra("name");


        binding.txtOrderNo.setText("#"+orderNo);
        binding.txtDateOftime.setText(dateTime);
        binding.txtStockist.setText(dealer);

        viewModel.stockistDetailsData(retailerId,orderNo,dealerId,orderStatus);
        adapter = new AllOrderDetailAdapter(this,list,sign);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObserver);
        binding.swipeRefreshLayout.setRefreshing(false);



    }
    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        TextView title = binding.txtDetails;
        SpannableString spannable = new SpannableString("Order Details");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 6, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);

    }

    private void onAttachObserver() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(this, orderDetailsResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (orderDetailsResponses != null) {
                list.clear();
                list.addAll(orderDetailsResponses);
                adapter.notifyDataSetChanged();

                // Enable or re-attach the click listener for generating PDF
                binding.btnPrintInvoice.setOnClickListener(v -> {
                    generateInvoicePdf();
                });
            }
        });
    }

    private void generateInvoicePdf() {
        String dealer = getIntent().getStringExtra("name");
        if (dealer == null) dealer = "";

        String orderDate = getIntent().getStringExtra("dot");
        if (orderDate == null) orderDate = "";

        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "No order details available", Toast.LENGTH_SHORT).show();
            return;
        }

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

        // ✅ Generate unique filename using timestamp
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "Purchase_Invoice_" + timestamp + ".pdf";
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF Saved Successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Save PDF", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale > 1.0f) {
            newConfig.fontScale = 1.0f;
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
        super.onConfigurationChanged(newConfig);
    }



//    private void printOrderInvoice() {
//
//        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
//        String dealerName = getIntent().getStringExtra("name");
//        String orderNo = getIntent().getStringExtra("orderNo");
//        String dateTime = getIntent().getStringExtra("dot");
//
//        PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {
//
//            @Override
//            public void onStart() {
//                super.onStart();
//            }
//
//            @Override
//            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
//                String printContent = generateInvoiceContent(dealerName,orderNo, dateTime);
//                String[] lines = printContent.split("\n");
//
//                int linesPerPage = 40; // Number of lines that fit per page
//                int totalPages = (int) Math.ceil((double) lines.length / linesPerPage);
//
//                callback.onLayoutFinished(new PrintDocumentInfo.Builder("Invoice")
//                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
//                        .setPageCount(totalPages)
//                        .build(), true);
//            }
//
//            @Override
//            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
//                try {
//                    PdfDocument document = new PdfDocument();
//                    String printContent = generateInvoiceContent(dealerName,orderNo, dateTime);
//                    String[] lines = printContent.split("\n");
//
//                    int linesPerPage = 40; // Lines per page
//                    int totalPages = (int) Math.ceil((double) lines.length / linesPerPage);
//
//                    for (int pageNum = 0; pageNum < totalPages; pageNum++) {
//                        if (cancellationSignal.isCanceled()) {
//                            callback.onWriteCancelled();
//                            document.close();
//                            return;
//                        }
//
//                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, pageNum + 1).create();
//                        PdfDocument.Page page = document.startPage(pageInfo);
//
//                        Canvas canvas = page.getCanvas();
//                        Paint paint = new Paint();
//                        paint.setColor(Color.BLACK);
//                        paint.setTextSize(16);
//
//                        float x = 10;
//                        float y = 20;
//
//                        // Write the lines for the current page
//                        int startLine = pageNum * linesPerPage;
//                        int endLine = Math.min(startLine + linesPerPage, lines.length);
//                        for (int i = startLine; i < endLine; i++) {
//                            canvas.drawText(lines[i], x, y, paint);
//                            y += 20; // Move down for the next line
//                        }
//
//                        document.finishPage(page);
//                    }
//
//                    // Write the document to the output stream
//                    FileOutputStream out = new FileOutputStream(destination.getFileDescriptor());
//                    document.writeTo(out);
//                    document.close();
//
//                    // Indicate that writing is complete
//                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
//
//                } catch (IOException e) {
//                    Log.e("PrintError", "Error writing document: " + e.getMessage());
//                    callback.onWriteFailed("Error writing document");
//                }
//            }
//        };
//
//        // Start the print job
//        printManager.print("Invoice_" + orderNo, printAdapter, new PrintAttributes.Builder().build());
//    }
//
//
//
//    private String generateInvoiceContent(String name,String orderNo, String dateTime) {
//        StringBuilder content = new StringBuilder();
//        content.append("Stockist : #").append(name).append("\n\n");
//        content.append("Order No: #").append(orderNo).append("\n\n");
//        content.append("Date: ").append(dateTime).append("\n\n");
//
//        content.append("------------------------------------------------\n");
//
//        content.append("Product Details:\n");
//
//        content.append("------------------------------------------------\n");
//
//        if (list != null && !list.isEmpty()) {
//            int serialNumber = 1;
//
//            for (OrderDetailsResponse response : list) {
//                content.append(serialNumber).append(". ").append(response.getProductName()).append("\n");
////                content.append("Dealer Name: ").append(response.getDealerName()).append("\n");
//                content.append("Unit: ").append(response.getUnitName()).append("\n");
//                content.append("Quantity: ").append(response.getOrderQty()).append("\n");
//                content.append("------------------------------------------------\n");
//
//                serialNumber++;
//            }
//        } else {
//            content.append("No products found in the order.\n");
//        }
//
//        return content.toString();
//    }

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