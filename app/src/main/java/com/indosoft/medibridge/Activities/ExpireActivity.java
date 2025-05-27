package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.Body.ExpiryRegisterBody;
import com.indosoft.medibridge.Body.RegisterExpiryBody;
import com.indosoft.medibridge.Model.ProductItem;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.databinding.ActivityExpireBinding;

import java.util.List;

public class ExpireActivity extends AppCompatActivity {

    ActivityExpireBinding binding;
    List<ProductItem> productList;
    SignUpViewModel viewModel;
    private String selectedProductId = null;
    private String selectedDealerId = null;
    private String oderitemsId = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding = ActivityExpireBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.init(this);

        String productName = getIntent().getStringExtra("productName");
        String qty = getIntent().getStringExtra("qty");
        String stockist = getIntent().getStringExtra("stockist");
        selectedProductId = getIntent().getStringExtra("productId");   // <-- FIXED
        selectedDealerId = getIntent().getStringExtra("stockistId");
        oderitemsId = getIntent().getStringExtra("orderItemsId");

        binding.txtProductName.setText(productName);
        binding.edtStockQty.setText(qty);
        binding.autoStockist.setText(stockist);
        onAttachObservers();
        initClicks();


    }

    private void initClicks() {
        binding.edtExpiryMonth.setOnClickListener(v -> {
            showCalendarDialog();
        });
        binding.btnSubmit.setOnClickListener(v -> {
            String expiryMonth = binding.edtExpiryMonth.getText().toString();
            String stock = binding.edtStockQty.getText().toString();

            if (selectedProductId == null) {
                Toast.makeText(this, "Select a product", Toast.LENGTH_SHORT).show();
            }  else if (expiryMonth.isEmpty()) {
                Toast.makeText(this, "Enter expiry month", Toast.LENGTH_SHORT).show();
            }  else if (selectedDealerId == null) {
                Toast.makeText(this, "Enter dealer", Toast.LENGTH_SHORT).show();
            } else {
                ExpiryRegisterBody body = new ExpiryRegisterBody();
                body.setRetailerId(AppSession.getInstance(this).getValue(Constants.RELAILER_ID));
                body.setProductId(selectedProductId);
                body.setExpiryMonth(expiryMonth);
                body.setStock(stock);
                body.setDealerId(selectedDealerId);
                viewModel.expiryRegister(body);

                RegisterExpiryBody registerExpiryBody = new RegisterExpiryBody();
                registerExpiryBody.setExpiryMonth(expiryMonth);
                registerExpiryBody.setRetailerId(AppSession.getInstance(this).getValue(Constants.RELAILER_ID));
                registerExpiryBody.setOrderItemsId(oderitemsId);
                registerExpiryBody.setProductId(selectedProductId);
                viewModel.registerexpiry(registerExpiryBody);
            }
        });
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        TextView title = binding.txtExpiry;
        SpannableString spannable = new SpannableString("Expiry Products");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 7, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);
    }

    private void onAttachObservers() {
        viewModel.getLiveData().observe(this,signUpResponse -> {
            if (signUpResponse!=null){
                binding.txtProductName.setText("");
                binding.edtExpiryMonth.setText("");
                binding.edtStockQty.setText("");
                binding.autoStockist.setText("");

              //  String expiryMonth = binding.edtExpiryMonth.getText().toString();

//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("productId", selectedProductId);
//                resultIntent.putExtra("expiryMonth", expiryMonth);
//                setResult(RESULT_OK, resultIntent);
//                finish();
              //  Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCalendarDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            // Get month (0-indexed) and year from the DatePicker
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();

            String selectedMonthYear = month + "/" + year;
            binding.edtExpiryMonth.setText(selectedMonthYear);
            //  filterByMonth(selectedMonthYear);
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
}