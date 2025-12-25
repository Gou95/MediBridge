package com.indosoft.medibridge.Fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import com.indosoft.medibridge.Adapter.CashMemoDetailsAdapter;
import com.indosoft.medibridge.Model.CashMemodetailsResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.CashMemoDetailsViewModel;
import com.indosoft.medibridge.databinding.FragmentCashMemoDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CashMemoDetailsFragment extends Fragment {
    FragmentCashMemoDetailsBinding binding;

    ArrayList<CashMemodetailsResponse> list = new ArrayList<>();
    ArrayList<CashMemodetailsResponse> fullList = new ArrayList<>();
    CashMemoDetailsAdapter adapter;
    CashMemoDetailsViewModel viewModel;
    String retailerId;
    private String startDateSelected = null;
    private String lastDateSelected = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentCashMemoDetailsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(CashMemoDetailsViewModel.class);
        viewModel.init(getContext());

        retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        viewModel.getCashMemoDetails(retailerId);

        onAttachObservers();
        initClicks();
        setDefaultDates();

        adapter = new CashMemoDetailsAdapter(requireContext(), list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);

        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);

        return binding.getRoot();
    }
    private void initClicks() {
        binding.txtStartDate.setOnClickListener(v -> openCalendarDialog("start"));
        binding.txtLastDate.setOnClickListener(v -> openCalendarDialog("last"));

    }

    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(getViewLifecycleOwner(), responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            list.clear();
            fullList.clear();

            if (responses != null && !responses.isEmpty()) {
                // ✅ Filter totalitems > 0
                for (CashMemodetailsResponse item : responses) {
                    try {
                        int totalItems = Integer.parseInt(item.getTotalitems());
                        if (totalItems > 0) {
                            fullList.add(item);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            filterListByDateRange(); // Apply filter & update adapter
        });
    }


    private void openCalendarDialog(String dateType) {
        View calendarView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_calendar, null);
        CalendarView calendar = calendarView.findViewById(R.id.calendarView);
        Button btnClose = calendarView.findViewById(R.id.btnClose);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(calendarView)
                .create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String apiDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (month + 1), dayOfMonth);
            if ("start".equals(dateType)) {
                startDateSelected = apiDate;
                binding.txtStartDate.setText(apiDate);
            } else {
                lastDateSelected = apiDate;
                binding.txtLastDate.setText(apiDate);
            }
            dialog.dismiss();
            filterListByDateRange();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());
    }

    private void filterListByDateRange() {
        if (startDateSelected == null || lastDateSelected == null) {
            adapter.updateList(fullList);
            return;
        }

        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ArrayList<CashMemodetailsResponse> filteredList = new ArrayList<>();

        try {
            Date start = apiFormat.parse(startDateSelected);
            Date end = apiFormat.parse(lastDateSelected);

            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            end = cal.getTime();

            for (CashMemodetailsResponse item : fullList) {
                Date itemDate = apiFormat.parse(item.getBillDate());
                if (itemDate != null && !itemDate.before(start) && !itemDate.after(end)) {
                    filteredList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.updateList(filteredList);
    }

    private void setDefaultDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());

        startDateSelected = todayDate;
        lastDateSelected = todayDate;

        binding.txtStartDate.setText(todayDate);
        binding.txtLastDate.setText(todayDate);
    }


    @Override
    public void onResume() {
        super.onResume();
        viewModel.getCashMemoDetails(retailerId);
    }
}