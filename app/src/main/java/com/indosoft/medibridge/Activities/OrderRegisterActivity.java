package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.OrderRegisterAdapter;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.OrderRegisterViewModel;
import com.indosoft.medibridge.databinding.ActivityOrderRegisterBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class OrderRegisterActivity extends AppCompatActivity {
ActivityOrderRegisterBinding binding;
    OrderRegisterViewModel viewModel;
    ArrayList<OrderRegisterResponse> list = new ArrayList<>();
    OrderRegisterAdapter adapter;
    private String startDateSelected = null;
    private String lastDateSelected = null;
    private boolean isReceiverRegistered = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    binding = ActivityOrderRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(OrderRegisterViewModel.class);
        viewModel.init(this);
        initClicks();
        onAttachObservers();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        adapter = new OrderRegisterAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String realerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        viewModel.orderRegisterList(realerId);
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.swipeRefreshLayout.setRefreshing(false);

    }

    private void initClicks() {
        binding.txtStartDate.setOnClickListener(v -> {
            openCalendarDialog("start");
        });

        binding.txtLastDate.setOnClickListener(v -> {
            openCalendarDialog("last");
        });
    }

    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(this, orderDetailsResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (orderDetailsResponses != null) {
                list.clear();
                for (OrderRegisterResponse response : orderDetailsResponses) {
                    if ((startDateSelected != null && lastDateSelected != null) &&
                            isDateInRange(response.getAddtime(), startDateSelected, lastDateSelected)) {
                        list.add(response); // Show filtered data
                    } else if (startDateSelected == null && lastDateSelected == null &&
                            isWithinLast24Hours(response.getAddtime())) {
                        list.add(response); // Show only last 24-hour data
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void openCalendarDialog(String dateType) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View calendarView = inflater.inflate(R.layout.custom_calendar, null);

        CalendarView calendar = calendarView.findViewById(R.id.calendarView);
        Button btnClose = calendarView.findViewById(R.id.btnClose);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(calendarView)
                .create();
        dialog.show();

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            String formattedDate = dayOfMonth + "." + getMonthName(month) + "." + year;

            if ("start".equals(dateType)) {
                startDateSelected = selectedDate;
                binding.txtStartDate.setText(formattedDate);
            } else if ("last".equals(dateType)) {
                lastDateSelected = selectedDate;
                binding.txtLastDate.setText(formattedDate);
            }

            Toast.makeText(this, "Start: " + startDateSelected + " End: " + lastDateSelected, Toast.LENGTH_SHORT).show();
           // filterListByDateRange();
            onAttachObservers();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }
    private String getMonthName(int month) {
        String[] monthNames = {
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };
        return monthNames[month]; // Month is 0-based, so this is correct
    }

    private boolean isDateInRange(String date, String startDate, String endDate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date targetDate = sdf.parse(date.split(" ")[0]);
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            return (targetDate.equals(start) || targetDate.after(start)) &&
                    (targetDate.equals(end) || targetDate.before(end));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean isWithinLast24Hours(String addtime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date orderDate = sdf.parse(addtime);

            if (orderDate == null) return false;

            long currentTime = System.currentTimeMillis();
            long twentyFourHoursAgo = currentTime - (24 * 60 * 60 * 1000);

            return orderDate.getTime() >= twentyFourHoursAgo;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
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