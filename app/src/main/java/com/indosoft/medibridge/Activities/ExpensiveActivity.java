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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.databinding.ActivityExpensiveBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpensiveActivity extends AppCompatActivity {

    ActivityExpensiveBinding binding;
    boolean isReceiverRegistered = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityExpensiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
      initClicks();
      startNetworkService();
    }

    private void initClicks() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(cal.getTime());
        binding.edtDate.setText(formattedDate);

        binding.edtDate.setOnClickListener(v -> {
            openCalendarDialog();
        });
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void openCalendarDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View calendarView = inflater.inflate(R.layout.custom_calendar, null);

        CalendarView calendar = calendarView.findViewById(R.id.calendarView);
        Button btnClose = calendarView.findViewById(R.id.btnClose);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(calendarView)
                .create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = sdf.format(cal.getTime());
            binding.edtDate.setText(formattedDate);


            dialog.dismiss();

        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
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