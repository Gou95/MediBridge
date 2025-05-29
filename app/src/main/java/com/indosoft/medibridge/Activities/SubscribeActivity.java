package com.indosoft.medibridge.Activities;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.SubsCriptionAdapter;
import com.indosoft.medibridge.Body.UpdateStatusBody;
import com.indosoft.medibridge.Model.PlansResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.PlansViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.databinding.ActivitySubscribeBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class SubscribeActivity extends AppCompatActivity implements PaymentResultListener {
    ActivitySubscribeBinding binding;
    PlansViewModel viewModel;
    SignUpViewModel sign;
    SubsCriptionAdapter adapter;
    ArrayList<PlansResponse> list = new ArrayList<>();
    private String planId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscribeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(PlansViewModel.class);
        viewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);
        viewModel.plansList();
        onAttachObservers();
        adapter = new SubsCriptionAdapter(this,list);
        binding.recyclerViewPlan.setAdapter(adapter);
        binding.recyclerViewPlan.setLayoutManager(new LinearLayoutManager(this
        ));
      //  binding.recyclerViewPlan.setLayoutManager(new GridLayoutManager(this,2));

        if (getIntent().hasExtra("amount") && getIntent().hasExtra("planId")) {
            int amount = getIntent().getIntExtra("amount", 0);
            String planId = getIntent().getStringExtra("planId");
            if (amount > 0) {
                startPayment(amount, planId);
            }
        }

        handlePlanVisibility();


    }
    private void handlePlanVisibility() {
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        boolean hasSelectedPlan = AppSession.getInstance(this).getBoolean(Constants.PLAN_SELECTED + "_" + retailerId, false);

        if (hasSelectedPlan) {
            list.removeIf(plan -> "5".equals(plan.getId()));
            adapter.notifyDataSetChanged();
        }
    }
    private void onAttachObservers() {
        viewModel.getLiveData().observe(this,plansResponses -> {
            if (plansResponses!=null){
                list.clear();
                list.addAll(plansResponses);
                adapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();
        int amount = getIntent().getIntExtra("amount", 0);
        String planId = getIntent().getStringExtra("planId");
        generateInvoice(razorpayPaymentID, String.valueOf(amount));
        updateSubscription(getPlanName(planId), getPlanDuration(planId));
        startActivity(new Intent(this, DashBoardActivity.class));
        finish();
    }

    private int getPlanDuration(String planId) {
        switch (planId) {
            case "2":
                return 365;  // Annual plan
            case "3":
                return 180;  // 6 months plan
            case "4":
                return 90;   // 3 months plan
            default:
                return 0;
        }
    }
    private String getPlanName(String planId) {
        switch (planId) {
            case "2":
                return "Annual";
            case "3":
                return "6 months";
            case "4":
                return "3 months";
            default:
                return "Unknown";
        }
    }
    private void generateInvoice(String razorpayPaymentID, String selectedPlanAmount) {
        try {
            File pdfFile = new File(getExternalFilesDir(null), "Invoice_" + razorpayPaymentID + ".pdf");

            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Medibro Pvt Ltd").setBold().setFontSize(16));
            document.add(new Paragraph("GST: 12ABCDE3456FZ7 | CST: 45XYZ6789AB").setFontSize(12));
            document.add(new Paragraph("------------------------------------------------").setFontSize(10));
            document.add(new Paragraph("Order ID: " + razorpayPaymentID).setFontSize(12));
            document.add(new Paragraph("Amount Paid: ₹" + selectedPlanAmount).setFontSize(12));
            document.add(new Paragraph("Date: " + new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).setFontSize(12));
            document.add(new Paragraph("------------------------------------------------").setFontSize(10));

            document.close();

            AppSession.getInstance(this).setValue(Constants.INVOICE_PATH, pdfFile.getAbsolutePath());

           // Toast.makeText(this, "Invoice saved successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating invoice", Toast.LENGTH_SHORT).show();
        }
    }
    private void startPayment(int amount, String planId) {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_i8Yg1uX2gMasnU");

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Medibro Pvt Ltd");
            options.put("description", "Subscription Payment");
            options.put("currency", "INR");
            options.put("amount", amount * 100);  // Amount in paise


            checkout.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed! Please try again.", Toast.LENGTH_LONG).show();
    }

    private void updateSubscription(String planName, int planDuration) {
        Calendar calendar = Calendar.getInstance();
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        long currentExpiry = AppSession.getInstance(this).getLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, 0);
        if (currentExpiry > System.currentTimeMillis()) {
            calendar.setTimeInMillis(currentExpiry);
        }

        // Add plan duration (in days)
        calendar.add(Calendar.DAY_OF_YEAR, planDuration);

        // Format the new expiry date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String expiryDate = sdf.format(calendar.getTime());

        // Save plan details in session
        AppSession.getInstance(this).setBoolean(Constants.PLAN_SELECTED + "_" + retailerId, true);
        AppSession.getInstance(this).setValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId, planName);
        AppSession.getInstance(this).putLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, calendar.getTimeInMillis());

        if (!"Trail Period (30 days)".equals(planName)) {
            AppSession.getInstance(this).setBoolean(Constants.FREE_PLAN_USED + "_" + retailerId, true);
        }

        Log.d("Subscription", "Saved Plan Name: " + planName);
        Log.d("Subscription", "Saved Expiry Date: " + expiryDate);

        // ✅ Prepare correct API body
        UpdateStatusBody body = new UpdateStatusBody();
        body.setSubscriptionPlan(planName);
        body.setSubsExpiryDate(expiryDate);

        // Use selected planId (passed from intent)
        String selectedPlanId = getIntent().getStringExtra("planId");
        if (selectedPlanId != null) {
            body.setSubscriptionId(selectedPlanId);
        } else {
            body.setSubscriptionId("0"); // fallback
        }

        // Call API
        sign.updateStatus(retailerId, body);

        startActivity(new Intent(this, DashBoardActivity.class));
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale > 1.0f) {
            newConfig.fontScale = 1.0f;
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
        super.onConfigurationChanged(newConfig);
    }
}