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
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.ProductRecivedAdapter;
import com.indosoft.medibridge.Body.ExpiryRegisterBody;
import com.indosoft.medibridge.Model.CityDealerResponse;
import com.indosoft.medibridge.Model.MedicineListResponse;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.Model.RecievedOrderResponse;
import com.indosoft.medibridge.Model.UnitResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.CityDealerViewModel;
import com.indosoft.medibridge.ViewModel.MedicineViewModel;
import com.indosoft.medibridge.ViewModel.RecievedProductViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.UnitViewModel;
import com.indosoft.medibridge.databinding.ActivityUnlistedMedicineBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ExpiryRegisterActivity extends AppCompatActivity {
    ActivityUnlistedMedicineBinding binding;
    ArrayList<RecievedOrderResponse> list = new ArrayList<>();
    RecievedProductViewModel recievedProductViewModel;
    ProductRecivedAdapter adapter;
    private String startDateSelected = null;
    private String lastDateSelected = null;
    boolean isReceiverRegistered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUnlistedMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recievedProductViewModel = new ViewModelProvider(this).get(RecievedProductViewModel.class);
        recievedProductViewModel.init(this);
        String retailer_id = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        recievedProductViewModel.getRecievedOrder(retailer_id);

        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);

        adapter = new ProductRecivedAdapter(this, list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        onAttachObservers();
        initClicks();
        setDefaultDates();
        startNetworkService();

    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.autoSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterByProductName(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.txtStartDate.setOnClickListener(v -> {
            openCalendarDialog("start");
        });

        binding.txtLastDate.setOnClickListener(v -> {
            openCalendarDialog("last");
        });

        TextView title = binding.txtRecieved;
        SpannableString spannable = new SpannableString("Product Received");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 8, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);

    }

    private void filterByProductName(String product) {
        if (product.isEmpty()) {
            adapter.updateList(list);
            return;
        }
        ArrayList<RecievedOrderResponse> filterList = new ArrayList<>();
        for (RecievedOrderResponse response : list) {
            if (response.getProductName() != null && response.getProductName().toLowerCase().contains(product.toLowerCase())) {
                filterList.add(response);
            }
        }
        adapter.updateList(filterList);
    }

    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        recievedProductViewModel.getLiveData().observe(this, responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null) {
                list.clear();
                for (RecievedOrderResponse response : responses) {
                    if ("Received".equals(response.getOrderStatus())) {
                        list.add(response);
                    }
                }
                filterListByDateRange();
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            String formattedDate = sdf.format(cal.getTime());

            if ("start".equals(dateType)) {
                startDateSelected = formattedDate;
                binding.txtStartDate.setText(formattedDate);
            } else if ("last".equals(dateType)) {
                lastDateSelected = formattedDate;
                binding.txtLastDate.setText(formattedDate);
            }

            dialog.dismiss();
            filterListByDateRange();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }


    private void filterListByDateRange() {
        if (startDateSelected == null || lastDateSelected == null) {
            adapter.updateList(list); // Show all if no date selected
            return;
        }

        ArrayList<RecievedOrderResponse> filteredList = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            Date start = dateFormat.parse(startDateSelected);
            Date end = dateFormat.parse(lastDateSelected);

            Calendar calEnd = Calendar.getInstance();
            calEnd.setTime(end);
            calEnd.set(Calendar.HOUR_OF_DAY, 23);
            calEnd.set(Calendar.MINUTE, 59);
            calEnd.set(Calendar.SECOND, 59);
            end = calEnd.getTime();

            for (RecievedOrderResponse response : list) {
                Date orderDate = dateFormat.parse(response.getAddtime());
                if (orderDate != null && !orderDate.before(start) && !orderDate.after(end)) {
                    filteredList.add(response);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapter.updateList(filteredList);
    }


    private void setDefaultDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        lastDateSelected = sdf.format(calendar.getTime());
        binding.txtLastDate.setText(lastDateSelected);

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        startDateSelected = sdf.format(calendar.getTime());
        binding.txtStartDate.setText(startDateSelected);
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