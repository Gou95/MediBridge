package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.Body.ExpiryRegisterBody;
import com.indosoft.medibridge.Body.RegisterExpiryBody;
import com.indosoft.medibridge.Model.ProductItem;
import com.indosoft.medibridge.Model.StockistListResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.StockistListViewModel;
import com.indosoft.medibridge.databinding.ActivityExpireBinding;

import java.util.ArrayList;
import java.util.List;

public class ExpireActivity extends AppCompatActivity {

    ActivityExpireBinding binding;
    SignUpViewModel viewModel;
    private String selectedProductId = null;
    private String selectedDealerId = null;
    private String oderitemsId = null;
    ArrayList<StockistListResponse> list = new ArrayList<>();
    StockistListViewModel stockistListViewModel;
    boolean isReceiverRegistered = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpireBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.init(this);
        stockistListViewModel = new ViewModelProvider(this).get(StockistListViewModel.class);
        stockistListViewModel.init(this);

        String cityId = AppSession.getInstance(this).getValue(Constants.CITY_ID);
        stockistListViewModel.stockitsList(cityId);
        String productName = getIntent().getStringExtra("productName");
        String qty = getIntent().getStringExtra("qty");
        String stockist = getIntent().getStringExtra("stockist");
        String batch = getIntent().getStringExtra("batch");
        selectedProductId = getIntent().getStringExtra("productId");   // <-- FIXED
        selectedDealerId = getIntent().getStringExtra("stockistId");
        oderitemsId = getIntent().getStringExtra("orderItemsId");
       String expiry = getIntent().getStringExtra("expiry");

        binding.txtProductName.setText(productName);
        binding.edtStockQty.setText(qty);
        binding.autoStockist.setText(stockist);
        binding.edtExpiryMonth.setText(expiry);
        binding.edtBatchNo.setText(batch);
        onAttachObservers();
        initClicks();
        startNetworkService();
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
//                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//        );

    }
    private void initClicks() {
        binding.edtExpiryMonth.setOnClickListener(v -> {
            showCalendarDialog();
        });
        binding.btnSubmit.setOnClickListener(v -> {
            String expiryMonth = binding.edtExpiryMonth.getText().toString().trim();
            String stock = binding.edtStockQty.getText().toString().trim();
            String batchNo = binding.edtBatchNo.getText().toString().trim();
            String dealerName = binding.autoStockist.getText().toString().trim();

            if (selectedProductId == null) {
                Toast.makeText(this, "Select a product", Toast.LENGTH_SHORT).show();
            } else if (expiryMonth.isEmpty()) {
                Toast.makeText(this, "Enter expiry month", Toast.LENGTH_SHORT).show();
            } else if (dealerName.isEmpty() || selectedDealerId == null || selectedDealerId.trim().isEmpty()) {
                Toast.makeText(this, "Select a dealer name", Toast.LENGTH_SHORT).show();
            } else {
                String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

                RegisterExpiryBody registerExpiryBody = new RegisterExpiryBody();
                registerExpiryBody.setExpiryMonth(expiryMonth);
                registerExpiryBody.setBatchNo(batchNo);
                registerExpiryBody.setStock(stock);
                registerExpiryBody.setDealerId(selectedDealerId);

                // ✅ API call
                viewModel.registerexpiry(retailerId, selectedProductId, oderitemsId, registerExpiryBody);
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
        viewModel.getLiveData().observe(this, signUpResponse -> {
            if (signUpResponse != null) {
                binding.txtProductName.setText("");
                binding.edtExpiryMonth.setText("");
                binding.edtStockQty.setText("");
                binding.autoStockist.setText("");
                binding.edtBatchNo.setText("");

                String expiryMonth = binding.edtExpiryMonth.getText().toString().trim();
                String batch = binding.edtBatchNo.getText().toString().trim();
                String qty = binding.edtStockQty.getText().toString().trim();

                Intent resultIntent = new Intent();
                resultIntent.putExtra("productId", selectedProductId);
                resultIntent.putExtra("expiryMonth", expiryMonth);
                resultIntent.putExtra("batch", batch);
                resultIntent.putExtra("qty", qty);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        stockistListViewModel.getLiveData().observe(this, responses -> {
            if (responses != null) {
                list.clear();
                list.addAll(responses);

                List<String> dealerNames = new ArrayList<>();

                for (StockistListResponse dealer : list) {
                    if (dealer.getDealerName() != null) {
                        dealerNames.add(dealer.getDealerName());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        dealerNames
                );

                binding.autoStockist.setAdapter(adapter);
                binding.autoStockist.setThreshold(1);
                binding.autoStockist.setDropDownHeight(200);
                binding.autoStockist.setDropDownVerticalOffset(-200);
                binding.autoStockist.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedName = parent.getItemAtPosition(position).toString();
                    for (StockistListResponse dealer : list) {
                        if (dealer.getDealerName().equals(selectedName)) {
                            selectedDealerId = dealer.getDealerId(); // ID set
                            break;
                        }
                    }
                });
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
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
            }
        }
    };

    private void reloadData() {
        new Handler().postDelayed(() -> {
            // You can refresh some data here if needed
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
}