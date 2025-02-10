package com.indosoft.medibridge.Activities;

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
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.AllOrderDetailAdapter;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.ViewModel.OrderDetailsViewModel;
import com.indosoft.medibridge.databinding.ActivitySeeAllOrderDetailsBinding;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SeeAllOrderDetailsActivity extends AppCompatActivity {

    ActivitySeeAllOrderDetailsBinding binding;
    OrderDetailsViewModel viewModel;
    ArrayList<OrderDetailsResponse> list = new ArrayList<>();
    AllOrderDetailAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivitySeeAllOrderDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);
        viewModel.init(this);


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

        viewModel.getOrderDetailsData(retailerId,orderNo,dealerId,orderStatus);
        adapter = new AllOrderDetailAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObserver);
        binding.swipeRefreshLayout.setRefreshing(false);



    }
    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnPrintInvoice.setOnClickListener(v -> {
            printOrderInvoice();
        });
        binding.imgWhatsapp.setOnClickListener(v -> {

            whatsappOpen();
        });
    }

    private void whatsappOpen() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.whatsapp");

        // Check if WhatsApp is installed
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // If WhatsApp is not installed, show a toast message
            Toast.makeText(this, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
        }
    }



    private void onAttachObserver() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(this,orderDetailsResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (orderDetailsResponses !=null){
                list.clear();
                list.addAll(orderDetailsResponses);
                adapter.notifyDataSetChanged();


            }
        });
    }

    private void printOrderInvoice() {

        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);
        String dealerName = getIntent().getStringExtra("name");
        String orderNo = getIntent().getStringExtra("orderNo");
        String dateTime = getIntent().getStringExtra("dot");

        PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                String printContent = generateInvoiceContent(dealerName,orderNo, dateTime);
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
                    String printContent = generateInvoiceContent(dealerName,orderNo, dateTime);
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



    private String generateInvoiceContent(String name,String orderNo, String dateTime) {
        StringBuilder content = new StringBuilder();
        content.append("Stockist : #").append(name).append("\n\n");
        content.append("Order No: #").append(orderNo).append("\n\n");
        content.append("Date: ").append(dateTime).append("\n\n");

        content.append("------------------------------------------------\n");

        content.append("Product Details:\n");

        content.append("------------------------------------------------\n");

        if (list != null && !list.isEmpty()) {
            int serialNumber = 1;

            for (OrderDetailsResponse response : list) {
                content.append(serialNumber).append(". ").append(response.getProductName()).append("\n");
//                content.append("Dealer Name: ").append(response.getDealerName()).append("\n");
                content.append("Unit: ").append(response.getUnitName()).append("\n");
                content.append("Quantity: ").append(response.getOrderQty()).append("\n");
                content.append("------------------------------------------------\n");

                serialNumber++;
            }
        } else {
            content.append("No products found in the order.\n");
        }

        return content.toString();
    }

    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
        Log.d("LoginActivity", "NetworkCheckService started");
    }

}