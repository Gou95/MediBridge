package com.indosoft.medibridge.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

public class SubscribeActivity extends AppCompatActivity implements PaymentResultListener {
    ActivitySubscribeBinding binding;
    PlansViewModel viewModel;
    SignUpViewModel sign;


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
        initClicks();
        handlePlanVisibility();




    }
    private void handlePlanVisibility() {
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        boolean hasSelectedPlan = AppSession.getInstance(this).getBoolean(Constants.PLAN_SELECTED + "_" + retailerId, false);

        if (hasSelectedPlan) {
            binding.radioFree.setVisibility(View.GONE);
            binding.txtFree.setVisibility(View.GONE);
        } else {
            binding.radioFree.setVisibility(View.VISIBLE);
            binding.txtFree.setVisibility(View.VISIBLE);
        }
    }

    private void initClicks() {
        binding.btnSubmit.setOnClickListener(v -> {
            int selectedId = binding.planGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Please select a plan", Toast.LENGTH_SHORT).show();
                return;
            }

            String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
            boolean hasSelectedPlan = AppSession.getInstance(this).getBoolean(Constants.PLAN_SELECTED + "_" + retailerId, false);

            if (selectedId == R.id.radioFree && hasSelectedPlan) {
                Toast.makeText(this, "Free plan is no longer available!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedId == R.id.radioFree) {
                activateFreePlan();
            } else {
                startPayment();
            }
        });
    }



    private void onAttachObservers() {
        viewModel.getLiveData().observe(this, plansResponses -> {
            if (plansResponses != null) {
                String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
                boolean hasSelectedPlan = AppSession.getInstance(this).getBoolean(Constants.PLAN_SELECTED + "_" + retailerId, false);

                for (PlansResponse response : plansResponses) {
                    switch (response.getId()) {
                        case "2":  // Yearly Plan
                            binding.txtYearly.setText("₹" + response.getSubscriptionCharges());
                            binding.radioYearly.setTag(response.getSubscriptionCharges());
                            break;
                        case "3":  // 6 Months Plan
                            binding.txtSix.setText("₹" + response.getSubscriptionCharges());
                            binding.radioSixMonths.setTag(response.getSubscriptionCharges());
                            break;
                        case "4":  // 3 Months Plan
                            binding.txtThree.setText("₹" + response.getSubscriptionCharges());
                            binding.radioThreeMonths.setTag(response.getSubscriptionCharges());
                            break;
                        case "5":  // Free Plan
                            if (hasSelectedPlan) {
                                binding.radioFree.setVisibility(View.GONE);
                                binding.txtFree.setVisibility(View.GONE);
                            } else {
                                binding.txtFree.setText("Free");
                                binding.radioFree.setVisibility(View.VISIBLE);
                                binding.txtFree.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                }
            }
        });
    }


    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        int selectedId = binding.planGroup.getCheckedRadioButtonId();
        String selectedPlanAmount = "0"; // Default value

        if (selectedId == R.id.radioYearly) {
            selectedPlanAmount = binding.radioYearly.getTag().toString();
        } else if (selectedId == R.id.radioSixMonths) {
            selectedPlanAmount = binding.radioSixMonths.getTag().toString();
        } else if (selectedId == R.id.radioThreeMonths) {
            selectedPlanAmount = binding.radioThreeMonths.getTag().toString();
        }

        // ✅ Pass the correct amount before generating the invoice
        generateInvoice(razorpayPaymentID, selectedPlanAmount);

        int planDuration = 0;
        String planName = "";

        if (selectedId == R.id.radioYearly) {
            planDuration = 365;
            planName = "Annual Subscription";
        } else if (selectedId == R.id.radioSixMonths) {
            planDuration = 180;
            planName = "6 Months Subscription";
        } else if (selectedId == R.id.radioThreeMonths) {
            planDuration = 90;
            planName = "3 Months Subscription";
        }

        updateSubscription(planName, planDuration);
    }
    private void generateInvoice(String razorpayPaymentID, String selectedPlanAmount) {
        try {
            File pdfFile = new File(getExternalFilesDir(null), "Invoice_" + razorpayPaymentID + ".pdf");

            // ✅ Corrected PdfWriter usage
            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(writer); // ✅ Fix: Pass writer to PdfDocument
            Document document = new Document(pdfDocument); // ✅ Fix: Pass pdfDocument to Document

            // ✅ Add invoice details
            document.add(new Paragraph("Medibro Pvt Ltd").setBold().setFontSize(16));
            document.add(new Paragraph("GST: 12ABCDE3456FZ7 | CST: 45XYZ6789AB").setFontSize(12));
            document.add(new Paragraph("------------------------------------------------").setFontSize(10));
            document.add(new Paragraph("Order ID: " + razorpayPaymentID).setFontSize(12));
            document.add(new Paragraph("Amount Paid: ₹" + selectedPlanAmount).setFontSize(12));
            document.add(new Paragraph("Date: " + new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date())).setFontSize(12));
            document.add(new Paragraph("------------------------------------------------").setFontSize(10));

            // ✅ Close document properly
            document.close();

            // ✅ Save invoice path
            AppSession.getInstance(this).setValue(Constants.INVOICE_PATH, pdfFile.getAbsolutePath());

            Toast.makeText(this, "Invoice saved successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating invoice", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed! Please try again.", Toast.LENGTH_LONG).show();
    }
    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_i8Yg1uX2gMasnU"); // Replace with your actual Razorpay Key ID

        int amount = 0;
        String planName = "";

        int selectedId = binding.planGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.radioYearly) {
            amount = Integer.parseInt(binding.radioYearly.getTag().toString()) * 100;
            planName = "Annual Subscription";
        } else if (selectedId == R.id.radioSixMonths) {
            amount = Integer.parseInt(binding.radioSixMonths.getTag().toString()) * 100;
            planName = "6 Months Subscription";
        } else if (selectedId == R.id.radioThreeMonths) {
            amount = Integer.parseInt(binding.radioThreeMonths.getTag().toString()) * 100;
            planName = "3 Months Subscription";
        }

        try {
            JSONObject options = new JSONObject();
            options.put("name", "MediBro");
            options.put("description", planName);
            options.put("currency", "INR");
            options.put("amount", amount);
            options.put("prefill.email", "user@example.com");
            options.put("prefill.contact", "9999999999");

            checkout.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Payment Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void activateFreePlan() {
        int freeDays = 30;
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        for (PlansResponse response : viewModel.getLiveData().getValue()) {
            if (response.getId().equals("5")) {
                freeDays = Integer.parseInt(response.getSubscriptionCharges());
                break;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, freeDays);

        // ✅ Save free plan details with retailer ID
        AppSession.getInstance(this).setBoolean(Constants.PLAN_SELECTED + "_" + retailerId, true);
        AppSession.getInstance(this).putLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, calendar.getTimeInMillis());
        AppSession.getInstance(this).setValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId, "Free Plan");

        // ✅ Debugging logs
        Log.d("Subscription", "Free Plan Activated for " + freeDays + " days");
        Log.d("Subscription", "Saved Expiry Date: " + calendar.getTimeInMillis());

        Toast.makeText(this, "Free Plan Activated for " + freeDays + " days!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DashBoardActivity.class));
        finish();
    }
    private void updateSubscription(String planName, int planDuration) {
        Calendar calendar = Calendar.getInstance();
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        long currentExpiry = AppSession.getInstance(this).getLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, 0);
        if (currentExpiry > System.currentTimeMillis()) {
            calendar.setTimeInMillis(currentExpiry);
        }
        calendar.add(Calendar.DAY_OF_YEAR, planDuration);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String expiryDate = sdf.format(calendar.getTime());

        // ✅ Save plan details with retailer-specific keys
        AppSession.getInstance(this).setBoolean(Constants.PLAN_SELECTED + "_" + retailerId, true);
        AppSession.getInstance(this).putLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, calendar.getTimeInMillis());
        AppSession.getInstance(this).setValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId, planName);

        // ✅ Debugging logs
        Log.d("Subscription", "Saved Plan Name: " + planName);
        Log.d("Subscription", "Saved Expiry Date: " + expiryDate);

        // ✅ Send plan update to backend
        UpdateStatusBody body = new UpdateStatusBody();
        body.setSubscriptionPlan(planName);
        body.setSubsExpiryDate(expiryDate);
        sign.updateStatus(retailerId, body);

        Toast.makeText(this, "Plan Activated: " + planName, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, DashBoardActivity.class));
        finish();
    }








}