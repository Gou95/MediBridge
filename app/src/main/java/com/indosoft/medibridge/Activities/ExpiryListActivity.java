package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.ExpiryListAdapter;
import com.indosoft.medibridge.Model.ExpiryListResponse;
import com.indosoft.medibridge.Model.RecentStockitsResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.ExpiryListViewModel;
import com.indosoft.medibridge.databinding.ActivityExpiryListBinding;

import java.util.ArrayList;
import java.util.Calendar;

public class ExpiryListActivity extends AppCompatActivity {

    ActivityExpiryListBinding binding;
    ArrayList<ExpiryListResponse> list = new ArrayList<>();
    ArrayList<ExpiryListResponse> originalList = new ArrayList<>();

    ExpiryListAdapter adapter;
    ExpiryListViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityExpiryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(ExpiryListViewModel.class);
        viewModel.init(this);
        viewModel.getList();
        initCliks();
        onAttachObservers();

        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);
        adapter = new ExpiryListAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(this, responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null) {
                originalList.clear();
                originalList.addAll(responses);

                list.clear();
                String currentMonthYear = getCurrentMonthYear();
                binding.autoMonth.setText(currentMonthYear);

                filterByRetailerId();  // ✅ Retailer filtering first
                filterByMonth(currentMonthYear);  // ✅ Then apply month filter
            } else {
                Log.e("DEBUG", "API Response is NULL");
            }
        });
    }


    private void filterByRetailerId() {
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        ArrayList<ExpiryListResponse> filteredList = new ArrayList<>();

        for (ExpiryListResponse response : originalList) {
            if (retailerId.equals(response.getRetailerId())) { // Only show products of the same retailer
                filteredList.add(response);
            }
        }

        originalList.clear();
        originalList.addAll(filteredList);
    }



    private void initCliks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.autoMedicine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterByMedicine( s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.autoStockist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterByStockist(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.autoMonth.setOnClickListener(v -> showCalendarDialog() );

    }
    private String getCurrentMonthYear() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        return month + "/" + year;
    }


    private void filterByStockist(String stockist) {
        if (stockist.isEmpty()){
            adapter.updateList(list);
            return;
        }
        ArrayList<ExpiryListResponse> filterList = new ArrayList<>();
        for (ExpiryListResponse response : list){
            if (response.getDealerName() !=null && response.getDealerName().toLowerCase().contains(stockist.toLowerCase())){
                filterList.add(response);
            }
        }
        adapter.updateList(filterList);
    }


    private void filterByMedicine(String product) {
        if (product.isEmpty()){
            adapter.updateList(list);
            return;
        }
        ArrayList<ExpiryListResponse> filterList = new ArrayList<>();
        for (ExpiryListResponse response : list){
            if (response.getProductName() !=null && response.getProductName().toLowerCase().contains(product.toLowerCase())){
                filterList.add(response);
            }
        }
        adapter.updateList(filterList);
    }
    private void showCalendarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.calendar_view, null);
        builder.setView(dialogView);

        CalendarView calendarView = dialogView.findViewById(R.id.calendarView);
        TextView btnCancel = dialogView.findViewById(R.id.txt_cancel);
        TextView btnOk = dialogView.findViewById(R.id.txt_ok);

        final Calendar selectedDate = Calendar.getInstance();

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
        });

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            int selectedMonth = selectedDate.get(Calendar.MONTH) + 1;
            int selectedYear = selectedDate.get(Calendar.YEAR);

            String selectedMonthYear = selectedMonth + "/" + selectedYear;
            binding.autoMonth.setText(selectedMonthYear);

            filterByMonth(selectedMonthYear);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void filterByMonth(String monthYear) {
        if (monthYear.isEmpty()) {
            adapter.updateList(originalList);
            return;
        }

        ArrayList<ExpiryListResponse> filteredList = new ArrayList<>();
        for (ExpiryListResponse response : originalList) {
            if (response.getExpiryMonth() != null && response.getExpiryMonth().trim().equalsIgnoreCase(monthYear)) {
                filteredList.add(response);
            }
        }

        adapter.updateList(filteredList);
    }


}