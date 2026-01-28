package com.indosoft.medibridge.Activities;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.SubsCriptionAdapter;
import com.indosoft.medibridge.Body.UpdateStatusBody;
import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.Model.PlansResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.GetSignUpUserViewModel;
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
    GetSignUpUserViewModel signUpViewModel;
    SignUpViewModel sign;
    SubsCriptionAdapter adapter;
    ArrayList<PlansResponse> list = new ArrayList<>();
    ArrayList<GetSignUpUserResponse> getAllUserList = new ArrayList<>();
    private String planId;
    boolean isReceiverRegistered = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubscribeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(PlansViewModel.class);
        viewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);
        signUpViewModel = new ViewModelProvider(this).get(GetSignUpUserViewModel.class);
        signUpViewModel.init(this);
        signUpViewModel.getAllSignUPData();
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
        startNetworkService();


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
        signUpViewModel.getLiveData().observe(this, responses -> {
            if (responses != null) {
                getAllUserList.clear();
                getAllUserList.addAll(responses);
            } else {
                Log.e("LoginActivity", "Sign-up response is null");
            }
        });
    }
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {

        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show();

        int amount = getIntent().getIntExtra("amount", 0);
        String planId = getIntent().getStringExtra("planId");

        String retailerId = AppSession.getInstance(this)
                .getValue(Constants.RELAILER_ID);

        // 1️⃣ Generate invoice
        generateInvoice(razorpayPaymentID, String.valueOf(amount));

        // 2️⃣ Update subscription (session + server)
        updateSubscription(getPlanName(planId), getPlanDuration(planId));

        // 3️⃣ 🔒 BLOCK FREE PLAN POPUP FOREVER
        AppSession.getInstance(this)
                .setBoolean("FREE_PLAN_POPUP_SHOWN_" + retailerId, true);

        AppSession.getInstance(this)
                .setBoolean(Constants.FREE_PLAN_USED + "_" + retailerId, true);

        // 4️⃣ Go directly to Dashboard
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
        checkout.setKeyID("rzp_live_hkuBztPfIkJjjs");

        try {
            JSONObject options = new JSONObject();
            options.put("name", "MediBro Pvt Ltd");
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
    public void onPaymentError(int code, String response) {

        Toast.makeText(this,
                "Payment failed. Please try again.",
                Toast.LENGTH_LONG).show();

        if (isRetailerActive()) {
            // ✅ Status Active → Dashboard
            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else {
            // ❌ Status not Active → Login
            AppSession.getInstance(this).clear();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }


    private boolean isRetailerActive() {

        String retailerId = AppSession.getInstance(this)
                .getValue(Constants.RELAILER_ID);

        for (GetSignUpUserResponse user : getAllUserList) {
            if (retailerId.equals(user.getRetailerId())) {

                String status = user.getStatus(); // "Active"
                return "Active".equalsIgnoreCase(status);
            }
        }
        return false; // default inactive
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
        }, 1000);
    }
}