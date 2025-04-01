package com.indosoft.medibridge.Activities;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.OrderRegisterAdapter;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.OrderRegisterViewModel;
import com.indosoft.medibridge.databinding.ActivityOrderRegisterBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderRegisterActivity extends AppCompatActivity {
ActivityOrderRegisterBinding binding;
    OrderRegisterViewModel viewModel;
    ArrayList<OrderRegisterResponse> list = new ArrayList<>();
    OrderRegisterAdapter adapter;
    private String startDateSelected = null;
    private String lastDateSelected = null;
    private boolean isReceiverRegistered = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    binding = ActivityOrderRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(OrderRegisterViewModel.class);
        viewModel.init(this);
        initClicks();
        onAttachObservers();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        adapter = new OrderRegisterAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String realerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        viewModel.orderRegisterList(realerId);
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.swipeRefreshLayout.setRefreshing(false);

    }
    private void initClicks() {
       binding.autoMonth.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
           }
           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               filterByProductName(s.toString());

           }
           @Override
           public void afterTextChanged(Editable s) {

           }
       });
       binding.btnPrintOrder.setOnClickListener(v -> {
           generateInvoicePdf();
       });
    }
    private void filterByProductName(String product) {
        if (product.isEmpty()){
            adapter.updateList(list);
            return;
        }
        ArrayList<OrderRegisterResponse> filterList = new ArrayList<>();
        for (OrderRegisterResponse response : list){
            if (response.getProductName() !=null && response.getProductName().toLowerCase().contains(product.toLowerCase())){
                filterList.add(response);
            }
        }
        adapter.updateList(filterList);
    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(this, orderDetailsResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (orderDetailsResponses != null) {
                list.clear();
                for (OrderRegisterResponse response : orderDetailsResponses) {
                    if (isWithinLast24Hours(response.getAddtime())) {
                        list.add(response);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    private boolean isWithinLast24Hours(String addtime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date orderDate = sdf.parse(addtime);
            if (orderDate == null) return false;
            long currentTime = System.currentTimeMillis();
            long twentyFourHoursAgo = currentTime - (24 * 60 * 60 * 1000);
            return orderDate.getTime() >= twentyFourHoursAgo;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
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
    private void generateInvoicePdf() {
        if (list == null || list.isEmpty()) {
            Toast.makeText(this, "No order details available", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        Paint borderPaint = new Paint();

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(1);

        int pageWidth = 600;
        int pageHeight = 800;
        int marginTop = 50;
        int rowHeight = 40;
        int availableHeight = pageHeight - 150;

        int col1 = 140, col2 = 100, col3 = 100, col4 = 100, col5 = 80;
        int itemIndex = 0;

        while (itemIndex < list.size()) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            // **Title: "Purchase Order"**
            titlePaint.setTextSize(20);
            titlePaint.setColor(Color.BLACK);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("Purchase Order", 220, marginTop, titlePaint);

            int y = marginTop + 50;
            paint.setTextSize(14);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            int startX = 50;
            int endX = startX + col1 + col2 + col3 + col4 + col5;
            int headerBottom = y + rowHeight;

            // **Header Row**
            canvas.drawText("Product Name", startX + 10, y + 25, paint);
            canvas.drawText("Unit", startX + col1 + 10, y + 25, paint);
            canvas.drawText("Delivery Day", startX + col1 + col2 + 10, y + 25, paint);
            canvas.drawText("Status", startX + col1 + col2 + col3 + 10, y + 25, paint);
            canvas.drawText("Stockist", startX + col1 + col2 + col3 + col4 + 10, y + 25, paint);

            canvas.drawRect(startX, y, endX, headerBottom, borderPaint);
            y += rowHeight;
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

            // **Loop through list items**
            while (itemIndex < list.size() && y + rowHeight < availableHeight) {
                OrderRegisterResponse item = list.get(itemIndex);

                String productName = (item.getProductName() != null) ? item.getProductName() : "";
                String unitName = (item.getUnitName() != null) ? item.getUnitName() : "";
                String deliveryDay = (item.getDeliveryDay() != null) ? item.getDeliveryDay() : "";
                String orderStatus = (item.getOrderStatus() != null) ? item.getOrderStatus() : "";
                String stockistName = (item.getDealerName() != null) ? item.getDealerName() : "Unknown";
                String unlistedMedicines = (item.getUnlistedMedicines() != null) ? (String) item.getUnlistedMedicines() : "";

                int textY = y + 20;
                int rowHeightAdjusted = 40; // Default row height

                // **Handle UNLISTED MEDICINES case**
                if ("UNLISTED MEDICINES".equals(productName)) {
                    List<String> wrappedUnlistedMedicines = wrapText(unlistedMedicines, paint, col1 - 20);

                    // **Draw "UNLISTED MEDICINES" in bold**
                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    canvas.drawText(productName, startX + 10, textY, paint);
                    textY += 20;

                    paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                    for (String line : wrappedUnlistedMedicines) {
                        canvas.drawText(line, startX + 10, textY, paint);
                        textY += 20;
                    }

                    rowHeightAdjusted = wrappedUnlistedMedicines.size() * 20 + 20;
                } else {
                    // **Wrap Product Name Properly**
                    List<String> wrappedProductName = wrapText(productName, paint, col1 - 20);
                    for (String line : wrappedProductName) {
                        canvas.drawText(line, startX + 10, textY, paint);
                        textY += 20;
                    }

                    // **Unit Column (Separate)**
                    canvas.drawText(!unitName.isEmpty() ? unitName : "-", startX + col1 + 10, y + 25, paint);

                    // **Calculate row height**
                    rowHeightAdjusted = wrappedProductName.size() * 20;
                }

                // **Wrap Stockist Name (2-line support)**
                String[] stockistWords = stockistName.split(" ", 2);
                String stockistFirstLine = stockistWords[0];
                String stockistSecondLine = (stockistWords.length > 1) ? stockistWords[1] : "";

                canvas.drawText(stockistFirstLine, startX + col1 + col2 + col3 + col4 + 10, y + 20, paint);
                if (!stockistSecondLine.isEmpty()) {
                    canvas.drawText(stockistSecondLine, startX + col1 + col2 + col3 + col4 + 10, y + 40, paint);
                }

                rowHeightAdjusted = Math.max(rowHeightAdjusted, (!stockistSecondLine.isEmpty()) ? 60 : 40);

                // **Other Columns**
                canvas.drawText(!deliveryDay.isEmpty() ? deliveryDay : "-", startX + col1 + col2 + 10, y + 25, paint);
                canvas.drawText(!orderStatus.isEmpty() ? orderStatus : "-", startX + col1 + col2 + col3 + 10, y + 25, paint);

                // **Draw Border**
                canvas.drawRect(startX, y, endX, y + rowHeightAdjusted, borderPaint);
                y += rowHeightAdjusted;
                itemIndex++;
            }

            pdfDocument.finishPage(page);
        }

        // **Save PDF**
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Purchase_Invoice.pdf");
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