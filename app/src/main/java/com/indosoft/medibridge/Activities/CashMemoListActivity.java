package com.indosoft.medibridge.Activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.CashMemoItemsAdapter;
import com.indosoft.medibridge.Model.CashMemoItemDetailsResponse;
import com.indosoft.medibridge.Model.CashMemoPdfResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.CashMemoItemDetailsViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.databinding.ActivityCashMemoListBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CashMemoListActivity extends AppCompatActivity {

    ActivityCashMemoListBinding binding;
    ArrayList<CashMemoItemDetailsResponse> list = new ArrayList<>();
    CashMemoItemsAdapter adapter;
    CashMemoItemDetailsViewModel viewModel;
    SignUpViewModel sign;
    String retailerId;
    String billNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCashMemoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(CashMemoItemDetailsViewModel.class);
        viewModel.init(this);

        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);

        // ✅ Get retailer_id from session
        retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        // ✅ Get intent data
        billNo = getIntent().getStringExtra("bill_no");
        String billDate = getIntent().getStringExtra("date");
        binding.txtCashMemoDate.setText(billDate);

        // ✅ Setup RecyclerView and Adapter
        adapter = new CashMemoItemsAdapter(this, list, sign, () -> {
            // Callback after update — recalculate total and refresh
            calculateTotalAmount();
            refreshList();
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // ✅ Pull-to-refresh
        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshList);

        // ✅ Initial load
        refreshList();

        initClicks();
    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.imgPrint.setOnClickListener(v -> {
//            viewModel.getItemsList(retailerId, billNo);
            generateMedicalBillPdf(this);
        });
    }

    private void refreshList() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getItemsList(retailerId, billNo);
        observeData();
    }

    private void observeData() {
        viewModel.getLiveData().observe(this, responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null && !responses.isEmpty()) {
                list.clear();
                list.addAll(responses);
                adapter.notifyDataSetChanged();
                calculateTotalAmount();

            }
        });
    }

    private void calculateTotalAmount() {
        double totalAmount = 0.0;
        int totalItems = 0;

        for (CashMemoItemDetailsResponse item : list) {
            try {
                if (item.getAmount() != null && !item.getAmount().isEmpty()) {
                    totalAmount += Double.parseDouble(item.getAmount());
                }
                totalItems++;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // ✅ Show total amount
        binding.txtTotalAmount.setText("₹ " + String.format(Locale.getDefault(), "%.2f", totalAmount));

        // ✅ Show total number of sold medicine items
        binding.txtSoldMedicine.setText(String.valueOf(totalItems));
    }
    private void generateMedicalBillPdf(Context context) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(600, 850, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // ==== Outer Border ====
        int margin = 20;
        int boxLeft = margin;
        int boxTop = margin;
        int boxRight = pageInfo.getPageWidth() - margin;
        int boxBottom = pageInfo.getPageHeight() - margin;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(boxLeft, boxTop, boxRight, boxBottom, paint);
        paint.setStyle(Paint.Style.FILL);

        // ==== Header Section ====
        CashMemoItemDetailsResponse firstItem = list.get(0);
        String billNo = firstItem.getBillNo();
        String billDate = firstItem.getBillDate();
        String patientName = firstItem.getPatientName();
        String doctorName = firstItem.getDoctorName();

        int headerY = boxTop + 30;

// Cash Memo (Center)
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTextSize(22);
        titlePaint.setFakeBoldText(true);
        canvas.drawText("Cash Memo", pageInfo.getPageWidth() / 2, headerY, titlePaint);

// DL Number (Left)
        Paint headerLeftPaint = new Paint();
        headerLeftPaint.setTextSize(14);
        headerLeftPaint.setFakeBoldText(false);
        headerLeftPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("DL No: " + AppSession.getInstance(this).getValue(Constants.RETAILER_DL),
                boxLeft + 10, headerY, headerLeftPaint);

// GST Number (Right)
        Paint headerRightPaint = new Paint();
        headerRightPaint.setTextSize(14);
        headerRightPaint.setFakeBoldText(false);
        headerRightPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("GST No: " + AppSession.getInstance(this).getValue(Constants.RETAILER_GST),
                boxRight - 10, headerY, headerRightPaint);

// Retailer Name (Next Line - Center)
        titlePaint.setTextSize(18);
        titlePaint.setFakeBoldText(true);
        canvas.drawText(AppSession.getInstance(this).getValue(Constants.RELAILER_NAME),
                pageInfo.getPageWidth() / 2, boxTop + 55, titlePaint);

        paint.setTextSize(12);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(AppSession.getInstance(this).getValue(Constants.PERMANENTADDRESS),
                pageInfo.getPageWidth() / 2, boxTop + 75, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("No: " + billNo, boxLeft + 10, boxTop + 95, paint);
        canvas.drawText("Date: " + billDate, boxRight - 160, boxTop + 95, paint);
        canvas.drawText("Patient Name: " + patientName, boxLeft + 10, boxTop + 115, paint);
        canvas.drawText("Doctor Name: " + doctorName, boxRight - 160, boxTop + 115, paint);

        // ==== Table Header ====
        int tableLeft = boxLeft + 10;
        int tableTop = boxTop + 150;

        int[] colWidths = {40, 120, 80, 80, 60, 80, 80};
        String[] headers = {"S.No.", "Particulars", "Batch No", "Expiry", "Qty", "Rate", "Amount"};

        int tableRight = tableLeft;
        for (int width : colWidths) tableRight += width;

        int headerHeight = 30;
        int y = tableTop;

        // Header Row Box
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(tableLeft, y, tableRight, y + headerHeight, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(12);
        paint.setFakeBoldText(true);

        int x = tableLeft;
        for (int i = 0; i < headers.length; i++) {
            canvas.drawText(headers[i], x + 5, y + 20, paint);
            x += colWidths[i];
            canvas.drawLine(x, y, x, y + headerHeight, paint); // vertical lines
        }
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1);
        borderPaint.setColor(Color.BLACK);

        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(12);
        textPaint.setFakeBoldText(false);
        // ==== Table Rows ====
        paint.setFakeBoldText(false);
        y += headerHeight;
        double grandTotal = 0.0;

        for (int i = 0; i < list.size(); i++) {
            CashMemoItemDetailsResponse item = list.get(i);

            String sno = String.valueOf(i + 1);
            String name = item.getProductName();
            String batch = item.getBatchNo();
            String expiry = item.getExpiryDate();
            String qty = item.getQty();
            String rate = item.getRate() == null ? "0" : item.getRate().toString();
            String amt = item.getAmount() == null ? "0" : item.getAmount();

            try {
                grandTotal += Double.parseDouble(amt);
            } catch (Exception ignored) {}


            TextPaint tp = new TextPaint();
            tp.setTextSize(12);
            tp.setColor(Color.BLACK);

            int particularsColWidth = colWidths[1] - 10; // width of "Particulars" column
            StaticLayout staticLayout = new StaticLayout(
                    name,
                    tp,
                    particularsColWidth,
                    Layout.Alignment.ALIGN_NORMAL,
                    1.0f,
                    0.0f,
                    false
            );

            int nameHeight = staticLayout.getHeight();
            int rowHeight = Math.max(25, nameHeight + 10); // dynamic row height

            // Draw row border
            canvas.drawRect(tableLeft, y, tableRight, y + rowHeight, borderPaint);

            int colX = tableLeft;
            canvas.drawText(sno, colX + 5, y + 18, textPaint);
            colX += colWidths[0];

            // Draw product name multiline
            canvas.save();
            canvas.translate(colX + 5, y + 5);
            staticLayout.draw(canvas);
            canvas.restore();
            colX += colWidths[1];

            // Other columns
            canvas.drawText(batch, colX + 5, y + 18, textPaint);
            colX += colWidths[2];

            canvas.drawText(expiry, colX + 5, y + 18, textPaint);
            colX += colWidths[3];

            canvas.drawText(qty, colX + 5, y + 18, textPaint);
            colX += colWidths[4];

            canvas.drawText(rate, colX + 5, y + 18, textPaint); // <-- NEW
            colX += colWidths[5];

            canvas.drawText(amt, colX + 5, y + 18, textPaint);

            // Update Y for next row
            y += rowHeight;
        }

        // ==== Total Row ====
        int totalRowHeight = 30;
        canvas.drawRect(tableLeft, y, tableRight, y + totalRowHeight, borderPaint);

        textPaint.setFakeBoldText(true);
        canvas.drawText("Total:", tableRight - 120, y + 20, textPaint);
        canvas.drawText("₹" + String.format("%.2f", grandTotal), tableRight - 60, y + 20, textPaint);

        y += totalRowHeight + 40;

        paint.setFakeBoldText(false);
        paint.setTextSize(12);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Terms & Conditions", tableLeft, y, paint);

        // ==== Signature ====
        paint.setFakeBoldText(false);
        canvas.drawText("Signature", tableRight - 100, y, paint);

        // ==== End Page ====
        pdfDocument.finishPage(page);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "CashMemo_" + timeStamp + ".pdf";
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName);

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            openPdf(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        pdfDocument.close();
    }
    private void openPdf(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_LONG).show();
        }
    }
}
