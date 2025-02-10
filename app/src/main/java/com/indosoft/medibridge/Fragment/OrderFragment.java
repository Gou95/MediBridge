package com.indosoft.medibridge.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

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
    private String startDateSelected = null;
    private String lastDateSelected = null;
    private boolean isReceiverRegistered = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentOrderBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(OrderListViewModel.class);
        viewModel.init(requireContext());

        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);

        viewModel.orderList(retailerId);


        startNetworkCheckService();
        handleBackPress();
        setDefaultDates();

        binding.swipeRefreshLayout.setOnRefreshListener(this::refreshOrderList);

        refreshOrderList();


        adapter = new OrderListAdapter(requireContext(), list);
        binding.orderRecyclerview.setAdapter(adapter);
        binding.orderRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        initClicks();
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
        binding.txtStartDate.setOnClickListener(v -> {
            openCalendarDialog("start"); // Pass a flag to indicate which date is being selected
        });

        binding.txtLastDate.setOnClickListener(v -> {
            openCalendarDialog("last"); // Pass a flag to indicate which date is being selected
        });

    }

    private void refreshOrderList() {
        binding.swipeRefreshLayout.setRefreshing(true);
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        viewModel.orderList(retailerId);

        viewModel.getLiveData().observe(getViewLifecycleOwner(), responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null) {
                list.clear();
                SimpleDateFormat apiFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                SimpleDateFormat filterFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                try {
                    Date start = filterFormat.parse(startDateSelected);
                    Date end = filterFormat.parse(lastDateSelected);

                    // 🛠 FIX: Ensure end date includes full day (23:59:59)
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(end);
                    cal.set(Calendar.HOUR_OF_DAY, 23);
                    cal.set(Calendar.MINUTE, 59);
                    cal.set(Calendar.SECOND, 59);
                    end = cal.getTime();

                    for (OrderListResponse response : responses) {
                        Date orderDate = apiFormat.parse(response.getAddtime());

                        // ✅ FIX: Check if orderDate is within range INCLUDING the last date
                        if (orderDate != null && !orderDate.before(start) && !orderDate.after(end)) {
                            list.add(response);
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                adapter.updateList(list);
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
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (month + 1), dayOfMonth);
            String formattedDate = dayOfMonth + "." + getMonthName(month) + "." + year;

            if ("start".equals(dateType)) {
                startDateSelected = selectedDate;
                binding.txtStartDate.setText(formattedDate);
            } else if ("last".equals(dateType)) {
                lastDateSelected = selectedDate;
                binding.txtLastDate.setText(formattedDate);
            }
            dialog.dismiss();
            filterListByDateRange();

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
    private void filterListByDateRange() {
        if (startDateSelected == null || lastDateSelected == null) {
            refreshOrderList();
            return;
        }

        ArrayList<OrderListResponse> filteredList = new ArrayList<>();
        SimpleDateFormat apiFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        SimpleDateFormat filterFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date start = filterFormat.parse(startDateSelected);
            Date end = filterFormat.parse(lastDateSelected);

            // 🛠 FIX: Ensure end date includes the full day (23:59:59)
            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            end = cal.getTime();

            for (OrderListResponse response : list) {
                Date targetDate = apiFormat.parse(response.getAddtime());

                // ✅ Fix: Check if targetDate is within range INCLUDING last date
                if (targetDate != null && !targetDate.before(start) && !targetDate.after(end)) {
                    filteredList.add(response);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapter.updateList(filteredList);
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
                } else {

                    System.out.println("Fragment is not attached to an activity.");
                }
            }
        });
    }
    private void setDefaultDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        lastDateSelected = sdf.format(calendar.getTime());
        binding.txtLastDate.setText(lastDateSelected);

        // Start Date = Current Date - 7 Days
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        startDateSelected = sdf.format(calendar.getTime());
        binding.txtStartDate.setText(startDateSelected);
    }

}