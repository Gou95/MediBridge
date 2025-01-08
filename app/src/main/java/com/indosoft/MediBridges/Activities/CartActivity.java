package com.indosoft.MediBridges.Activities;

import android.app.AlertDialog;
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
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.MediBridges.Adapter.CardListAdapter;
import com.indosoft.MediBridges.Adapter.StockitsListAdapter;
import com.indosoft.MediBridges.Fragment.HomeFragment;
import com.indosoft.MediBridges.Fragment.OrderFragment;
import com.indosoft.MediBridges.Model.OrderDetailsResponse;
import com.indosoft.MediBridges.Model.ShowCartResponse;
import com.indosoft.MediBridges.Model.StockitsResponse;
import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.ViewModel.DeleteCartViewModel;
import com.indosoft.MediBridges.ViewModel.ProceedOrderViewModel;
import com.indosoft.MediBridges.ViewModel.QuantityChangeViewModel;
import com.indosoft.MediBridges.ViewModel.ShowCartViewModel;
import com.indosoft.MediBridges.ViewModel.StockitsViewModel;
import com.indosoft.MediBridges.ViewModel.UrgentCartViewModel;
import com.indosoft.MediBridges.databinding.ActivityCartBinding;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    StockitsViewModel stockitsViewModel;
    StockitsListAdapter adapter;
    ArrayList<StockitsResponse> list = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
 stockitsViewModel = new ViewModelProvider(this).get(StockitsViewModel.class);
 stockitsViewModel.init(this);

        onAttachObservers();
        initClicks();

        String dealerName = getIntent().getStringExtra("dealerName");
        String dealerId = getIntent().getStringExtra("dealerId");
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        binding.txtDealername.setText(dealerName);
        stockitsViewModel.stockitsOrderList(retailerId,dealerId);

        adapter = new StockitsListAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toast.makeText(this, retailerId, Toast.LENGTH_SHORT).show();



    }

    private void initClicks() {

        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnPrintInvoice.setOnClickListener(v -> {
            printOrderInvoice();
        });
    }

    private void onAttachObservers() {
        stockitsViewModel.getLiveData().observe(this, responses -> {
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




    private void navigateToOrderFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.addToBackStack(null); // Add to back stack if you want the user to navigate back to the cart
        transaction.commit();
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
                // Calculate the total number of pages based on the content
                String printContent = generateInvoiceContent("StockistName", "DateTime");  // Replace with actual values
                String[] lines = printContent.split("\n");

                int linesPerPage = 40; // Set this based on your page layout
                int totalPages = (int) Math.ceil((double) lines.length / linesPerPage);

                callback.onLayoutFinished(new PrintDocumentInfo.Builder("Invoice").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(totalPages).build(), true);
            }

            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                try {
                    PdfDocument document = new PdfDocument();
                    Paint paint = new Paint();
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(16);

                    float x = 10; // X position for drawing text
                    float y = 20; // Y position for drawing text
                    float pageHeight = 800; // Height of the page (for A4 size, for example)
                    float lineHeight = 20; // Height of a single line of text
                    int linesPerPage = 30; // Set this based on your page layout (this will affect page size)

                    // Get dealerId (from intent or session)
                    String dealerId = getIntent().getStringExtra("dealerId");

                    // Initialize stockistName and dateTime
                    String stockistName = "Unknown Stockist";  // Default name in case the dealerId is not found
                    String dateTime = "Unknown Date";  // Default date-time if not found

                    // Find the stockistName and dateTime by searching for the dealerId in the list
                    for (StockitsResponse response : list) {
                        if (response.getDealerId().equals(dealerId)) {
                            stockistName = response.getDealerName(); // Get the dealer name from the matched response
                            dateTime = response.getAddtime(); // Assuming 'Addtime' is the date-time field in the response
                            break;
                        }
                    }

                    // Generate the invoice content using the stockistName and dateTime
                    String printContent = generateInvoiceContent(stockistName, dateTime);  // Pass both stockistName and dateTime
                    String[] lines = printContent.split("\n");

                    int lineNumber = 0;
                    int currentPage = 1;

                    // Loop through the lines and print them to pages
                    while (lineNumber < lines.length) {
                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 800, currentPage).create();
                        PdfDocument.Page page = document.startPage(pageInfo);
                        Canvas canvas = page.getCanvas();
                        y = 20;  // Reset Y position to the top of the page

                        // Print lines for the current page
                        int linesPrinted = 0;
                        while (lineNumber < lines.length && linesPrinted < linesPerPage) {
                            canvas.drawText(lines[lineNumber], x, y, paint);
                            y += lineHeight;
                            lineNumber++;
                            linesPrinted++;
                        }

                        document.finishPage(page);
                        currentPage++;
                    }

                    // Write the document to the output destination
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


    private String generateInvoiceContent(String stockistName, String dateTime) {
        StringBuilder content = new StringBuilder();

        // Replacing orderNo with stockistName in the header
        content.append("Stockist: ").append(stockistName).append("\n\n");
        content.append("Date: ").append(dateTime).append("\n\n");

        content.append("------------------------------------------------\n");

        // Add the section for product details
        content.append("Product Details:\n");

        content.append("------------------------------------------------\n");

        // Check if the list is not empty
        if (list != null && !list.isEmpty()) {
            int serialNumber = 1;

            for (StockitsResponse response : list) {

                content.append("").append(serialNumber).append("\n");
                content.append("Product Name: ").append(response.getProductName()).append("\n");
                content.append("Order No: ").append(response.getOrderNo()).append("\n"); // Replace dealerName with orderNo
                content.append("Unit: ").append(response.getUnitName()).append("\n");
                content.append("Quantity: ").append(response.getOrderQty()).append("\n");
                content.append("Delivery Day: ").append(response.getDeliveryDay()).append("\n");
                content.append("Added Time: ").append(response.getAddtime()).append("\n");
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