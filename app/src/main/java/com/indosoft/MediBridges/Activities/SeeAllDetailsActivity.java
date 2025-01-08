package com.indosoft.MediBridges.Activities;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.MediBridges.Adapter.AllOrderDetailAdapter;
import com.indosoft.MediBridges.Model.OrderDetailsResponse;
import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.ViewModel.OrderDetailsViewModel;
import com.indosoft.MediBridges.databinding.ActivitySeeAllDetailsBinding;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class SeeAllDetailsActivity extends AppCompatActivity {

    ActivitySeeAllDetailsBinding binding;
    OrderDetailsViewModel viewModel;
    ArrayList<OrderDetailsResponse> list = new ArrayList<>();
    AllOrderDetailAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding = ActivitySeeAllDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        viewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);
        viewModel.init(this);


        onAttachObserver();
        initClicks();

        String retailerId = getIntent().getStringExtra("retailerId");
        String orderNo = getIntent().getStringExtra("orderNo");
        String dateTime = getIntent().getStringExtra("dot");

        binding.txtOrderNo.setText("#"+orderNo);
        binding.txtDateOftime.setText(dateTime);

        viewModel.getOrderDetailsData(retailerId,orderNo);
        adapter = new AllOrderDetailAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnPrintInvoice.setOnClickListener(v -> {
printOrderInvoice();
        });
        binding.imgWhatsapp.setOnClickListener(v -> {

        });
    }

    private void onAttachObserver() {
        viewModel.getLiveData().observe(this,orderDetailsResponses -> {
            if (orderDetailsResponses !=null){
                list.clear();
                list.addAll(orderDetailsResponses);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void printOrderInvoice() {

        PrintManager printManager = (PrintManager) getSystemService(PRINT_SERVICE);

        String orderNo = getIntent().getStringExtra("orderNo");
        String dateTime = getIntent().getStringExtra("dot");

        // Create a custom PrintDocumentAdapter to define how to print
        PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {

            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                // Provide layout information (e.g., page size, layout)
                callback.onLayoutFinished(new PrintDocumentInfo.Builder("Invoice_" + orderNo)
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(1)
                        .build(), true);
            }

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                try {
                    PdfDocument document = new PdfDocument();

                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, 1).create();
                    PdfDocument.Page page = document.startPage(pageInfo);

                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(16);

                    float x = 10;
                    float y = 20;

                    String printContent = generateInvoiceContent(orderNo, dateTime);
                    String[] lines = printContent.split("\n");

                    for (String line : lines) {
                        canvas.drawText(line, x, y, paint);
                        y += 20;
                    }

                    document.finishPage(page);

                    FileOutputStream out = new FileOutputStream(destination.getFileDescriptor());
                    document.writeTo(out);

                    document.close();

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

        content.append("Order No: #").append(orderNo).append("\n\n");
        content.append("Date: ").append(dateTime).append("\n\n");

        content.append("------------------------------------------------\n");

        content.append("Product Details:\n");

        content.append("------------------------------------------------\n");

        if (list != null && !list.isEmpty()) {
            int serialNumber = 1;

            // Iterate over the products in the list
            for (OrderDetailsResponse response : list) {
                content.append("Serial No: ").append(serialNumber).append("\n");
                content.append("Product Name: ").append(response.getProductName()).append("\n");
                content.append("Dealer Name: ").append(response.getDealerName()).append("\n");
                content.append("Unit: ").append(response.getUnitName()).append("\n");
                content.append("Quantity: ").append(response.getOrderQty()).append("\n");
                content.append("------------------------------------------------\n");

                // Increment the serial number for the next product
                serialNumber++;
            }
        } else {
            content.append("No products found in the order.\n");
        }

        return content.toString();
    }


}



