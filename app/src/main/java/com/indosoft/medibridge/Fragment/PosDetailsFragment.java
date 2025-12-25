package com.indosoft.medibridge.Fragment;

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

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import com.indosoft.medibridge.Adapter.PosDetailesAdapter;
import com.indosoft.medibridge.Model.PosDetailsResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.PosDetailsViewModel;
import com.indosoft.medibridge.databinding.FragmentPosDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PosDetailsFragment extends Fragment {

    FragmentPosDetailsBinding binding;
    ArrayList<PosDetailsResponse> list = new ArrayList<>();
    ArrayList<PosDetailsResponse> fullList = new ArrayList<>();
    PosDetailsViewModel viewModel;
    PosDetailesAdapter adapter;

    private String startDateSelected = null;
    private String lastDateSelected = null;

    private boolean isReceiverRegistered = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPosDetailsBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(PosDetailsViewModel.class);
        viewModel.init(requireContext());

        String retailerId = AppSession.getInstance(requireContext()).getValue(Constants.RELAILER_ID);
        viewModel.posList(retailerId);

        adapter = new PosDetailesAdapter(requireContext(), list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            reloadData();
        });

        initClicks();
        onAttachObservers();
        setDefaultDates();

        return binding.getRoot();
    }

    private void initClicks() {
        binding.txtStartDate.setOnClickListener(v -> openCalendarDialog("start"));
        binding.txtLastDate.setOnClickListener(v -> openCalendarDialog("last"));

    }

    private void onAttachObservers() {
        viewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            fullList.clear();

            if (response != null) {
                fullList.addAll(response);
            }

            filterListByDateRange();
        });
    }

    private void openCalendarDialog(String dateType) {
        View calendarView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_calendar, null);
        CalendarView calendar = calendarView.findViewById(R.id.calendarView);
        Button btnClose = calendarView.findViewById(R.id.btnClose);

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setView(calendarView)
                .create();

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            String apiDate = String.format(Locale.getDefault(),
                    "%04d-%02d-%02d", year, (month + 1), dayOfMonth);

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
        ArrayList<PosDetailsResponse> filteredList = new ArrayList<>();

        try {
            Date start = apiFormat.parse(startDateSelected);
            Date end = apiFormat.parse(lastDateSelected);

            Calendar cal = Calendar.getInstance();
            cal.setTime(end);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            end = cal.getTime();

            for (PosDetailsResponse item : fullList) {
                Date itemDate = apiFormat.parse(item.getAddDate());
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



    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
                reloadData();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if (!isReceiverRegistered) {
            requireContext().registerReceiver(networkReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            isReceiverRegistered = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isReceiverRegistered) {
            requireContext().unregisterReceiver(networkReceiver);
            isReceiverRegistered = false;
        }
    }

    private void reloadData() {
        binding.swipeRefreshLayout.setRefreshing(true);

        new Handler().postDelayed(() -> {
            String retailerId = AppSession.getInstance(requireContext()).getValue(Constants.RELAILER_ID);
            viewModel.posList(retailerId);
        }, 1000);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                NetworkCapabilities cap = cm.getNetworkCapabilities(network);
                return cap != null && cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                return cm.getActiveNetworkInfo() != null &&
                        cm.getActiveNetworkInfo().isConnectedOrConnecting();
            }
        }
        return false;
    }
}
