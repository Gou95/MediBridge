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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.indosoft.medibridge.Adapter.ViewStockistAdapter;
import com.indosoft.medibridge.Model.RecentStockitsResponse;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.RecentStockitsViewModel;
import com.indosoft.medibridge.databinding.ActivityViewAllStockistBinding;

import java.util.ArrayList;

public class ViewAllStockistActivity extends AppCompatActivity {
    ActivityViewAllStockistBinding binding;
    RecentStockitsViewModel viewModel;
    ArrayList<RecentStockitsResponse> list = new ArrayList<>();
    ViewStockistAdapter adapter;
    private boolean isReceiverRegistered = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityViewAllStockistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(RecentStockitsViewModel.class);
        viewModel.init(this);
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        viewModel.recentStockits(retailerId);
        adapter = new ViewStockistAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Use 2 columns

        onAttachObservers();
        startNetworkService();

        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);

        initClicks();
    }
    private void initClicks() {

        binding.autoStockistName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterByStockistName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(this,recentStockitsResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (recentStockitsResponses != null){
                list.clear();
                list.addAll(recentStockitsResponses);
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void filterByStockistName(String stockistName) {
        if (stockistName.isEmpty()) {
            adapter.updateList(list);
            return;
        }
        ArrayList<RecentStockitsResponse> filterList = new ArrayList<>();
        for (RecentStockitsResponse response : list) {
            if (response.getDealerName() != null && response.getDealerName().toLowerCase().contains(stockistName.toLowerCase())) {
                filterList.add(response);
            }
        }
        adapter.updateList(filterList);
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
        }, 5000);
    }
}