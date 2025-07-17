package com.indosoft.medibridge.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.indosoft.medibridge.Activities.DashBoardActivity;
import com.indosoft.medibridge.Adapter.OrderListAdapter;
import com.indosoft.medibridge.Model.OrderListResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.OrderListViewModel;
import com.indosoft.medibridge.databinding.FragmentOrderBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class OrderFragment extends Fragment {

FragmentOrderBinding binding;
    OrderListViewModel viewModel;
    OrderListAdapter adapter;
    ArrayList<OrderListResponse> list = new ArrayList<>();
    ArrayList<OrderListResponse> fullList = new ArrayList<>();

    private String startDateSelected = null;
    private String lastDateSelected = null;
    private boolean isReceiverRegistered = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= FragmentOrderBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(OrderListViewModel.class);
        viewModel.init(requireContext());

        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);

        viewModel.orderList(retailerId);

        startNetworkCheckService();
        handleBackPress();
        setDefaultDates();
        refreshOrderList();
        initClicks();
        adapter = new OrderListAdapter(requireContext(), list);
        binding.orderRecyclerview.setAdapter(adapter);
        binding.orderRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshOrderList);
        binding.swipeRefreshLayout.setRefreshing(false);

        return binding.getRoot();
    }

    private void initClicks() {

        binding.imgBack.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment()) // Replace with your HomeFragment
                        .commit();
            }
        });
        binding.txtStartDate.setOnClickListener(v -> openCalendarDialog("start"));
        binding.txtLastDate.setOnClickListener(v -> openCalendarDialog("last"));

        TextView title = binding.txtActive;
        SpannableString spannable = new SpannableString("Active Orders");


        int blue = ContextCompat.getColor(getContext(), R.color.blue_light);
        int red = ContextCompat.getColor(getContext(), R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 7, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);
    }

    private void refreshOrderList() {
        binding.swipeRefreshLayout.setRefreshing(true);
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        viewModel.orderList(retailerId);

        viewModel.getLiveData().observe(getViewLifecycleOwner(), responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            fullList.clear();   // clear old data
            list.clear();       // clear old data
            if (responses != null) {
                fullList.addAll(responses); // save full list from API
                filterListByDateRange();    // do initial filtering using selected dates
            } else {
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void openCalendarDialog(String dateType) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View calendarView = inflater.inflate(R.layout.custom_calendar, null);

        CalendarView calendar = calendarView.findViewById(R.id.calendarView);
        Button btnClose = calendarView.findViewById(R.id.btnClose);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(calendarView)
                .create();
        dialog.show();

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, (month + 1), year);

            if ("start".equals(dateType)) {
                startDateSelected = selectedDate;
                binding.txtStartDate.setText(selectedDate);
            } else if ("last".equals(dateType)) {
                lastDateSelected = selectedDate;
                binding.txtLastDate.setText(selectedDate);
            }
            dialog.dismiss();
            filterListByDateRange();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }
    private void filterListByDateRange() {
        if (startDateSelected == null || lastDateSelected == null) {
            adapter.updateList(fullList); // fallback to full list
            return;
        }

        ArrayList<OrderListResponse> filteredList = new ArrayList<>();
        SimpleDateFormat apiFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat filterFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            Date start = filterFormat.parse(startDateSelected);
            Date end = filterFormat.parse(lastDateSelected);

            // Ensure end date covers entire day
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            end = cal.getTime();

            // 🔴 Filter from fullList instead of already filtered 'list'
            for (OrderListResponse response : fullList) {
                Date orderDate = apiFormat.parse(response.getAddtime());
                if (orderDate != null && !orderDate.before(start) && !orderDate.after(end)) {
                    filteredList.add(response);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        list.clear();
        list.addAll(filteredList);  // update current list
        adapter.updateList(list);
    }



    private void startNetworkCheckService() {
        Intent serviceIntent = new Intent(getContext(), NetworkCheckService.class);
        requireActivity().startService(serviceIntent);

    }


    private void handleBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                if (isAdded()) {
                    FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new HomeFragment(), "HomeFragment");

                    transaction.commit();
                }
                else {

                    System.out.println("Fragment is not attached to an activity.");
                }
            }
        });
    }
    private void setDefaultDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        lastDateSelected = sdf.format(calendar.getTime());
        binding.txtLastDate.setText(lastDateSelected);
        calendar.add(Calendar.DAY_OF_MONTH, -3);
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
}