package com.indosoft.medibridge.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.PosAllListAdapter;
import com.indosoft.medibridge.Model.GetPosProductResponse;
import com.indosoft.medibridge.Model.PosAllListResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.PosAllListViewModel;
import com.indosoft.medibridge.databinding.ActivityPosAllListBinding;

import java.util.ArrayList;
import java.util.Locale;

public class PosAllListActivity extends AppCompatActivity {
    ActivityPosAllListBinding binding;
    ArrayList<PosAllListResponse> list = new ArrayList<>();
    PosAllListAdapter adapter;
    PosAllListViewModel viewModel;
    private boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosAllListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(PosAllListViewModel.class);
        viewModel.init(this);
        String retailerid = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        String addTime = getIntent().getStringExtra("addtime");
        binding.txtPosDate.setText(addTime);
        viewModel.allPosList(retailerid,addTime);

        adapter = new PosAllListAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        onAttachObservers();
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }



    private void onAttachObservers() {
        viewModel.getLiveData().observe(this,responses -> {
            if (responses !=null){
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

        for (PosAllListResponse item : list) {
            try {
                if (item.getAmount() != null && !item.getAmount().isEmpty()) {
                    totalAmount += Double.parseDouble(item.getAmount());
                }
                totalItems++;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Show total amount
        binding.txtTotalAmount.setText("₹ " + String.format(Locale.getDefault(), "%.2f", totalAmount));

        // Show total number of sold medicine items
        binding.txtSoldMedicine.setText("" + totalItems);
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