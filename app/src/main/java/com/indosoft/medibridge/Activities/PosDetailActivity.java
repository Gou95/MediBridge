package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.webkit.internal.ApiFeature;

import com.google.android.material.tabs.TabLayoutMediator;
import com.indosoft.medibridge.Adapter.PosDetailesAdapter;
import com.indosoft.medibridge.Adapter.PosTabAdapter;
import com.indosoft.medibridge.Model.PosDetailsResponse;
import com.indosoft.medibridge.Model.TotalSalesResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.PosDetailsViewModel;
import com.indosoft.medibridge.ViewModel.TotalSalesViewModel;
import com.indosoft.medibridge.databinding.ActivityPosDetailBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PosDetailActivity extends AppCompatActivity {
    ActivityPosDetailBinding binding;
    private boolean isReceiverRegistered = false;
    TotalSalesViewModel viewModel;
    private String selectedDate; // user filter kare to yaha update hoga


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(TotalSalesViewModel.class);
        viewModel.init(this);
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        viewModel.getTotalSales(retailerId);
        onAttachObservers();

        PosTabAdapter adapter = new PosTabAdapter(this);
        binding.viewPager2.setAdapter(adapter);

        new TabLayoutMediator(binding.tab, binding.viewPager2,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("POS Details");
                    } else {
                        tab.setText("Case Memo Details");
                    }
                }).attach();
        binding.imgBack.setOnClickListener(v -> onBackPressed()
        );
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDate = sdf.format(new Date()); // ✅ Aaj ki date

    }

    private void onAttachObservers() {
        viewModel.getLiveData().observe(this, totalSalesResponses -> {
            if (totalSalesResponses != null && !totalSalesResponses.isEmpty()) {

                double totalSum = 0.0;
                boolean found = false;

                for (TotalSalesResponse response : totalSalesResponses) {

                    if (response.getDate().equals(selectedDate)) {
                        // ✅ Sirf selected/current date ka amount lo
                        try {
                            totalSum = Double.parseDouble(response.getTotalAmount());
                        } catch (Exception e) {
                            totalSum = 0.0;
                        }
                        found = true;
                        break;
                    }
                }

                if (found) {
                    binding.txtTotalSum.setText("₹ " + String.format(Locale.getDefault(), "%.2f", totalSum));
                } else {
                    // ✅ Agar us date ka data nahi mila
                    binding.txtTotalSum.setText("₹ 0.00");
                }
            } else {
                binding.txtTotalSum.setText("₹ 0.00");
            }
        });
    }


    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
                ///  hideNoConnectionView();
                reloadData();
            } else {
//                showNoConnectionView();
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
}
