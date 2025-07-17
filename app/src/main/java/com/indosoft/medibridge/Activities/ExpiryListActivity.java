package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

public class
ExpiryListActivity extends AppCompatActivity {

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
                //filterByMonth(currentMonthYear);
           applyAllFilters();
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
                applyAllFilters();
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
                applyAllFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.autoMonth.setOnClickListener(v -> showCalendarDialog() );
        TextView title = binding.txtExpiryList;
        SpannableString spannable = new SpannableString("Expiry List");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 7, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);

    }
    private String getCurrentMonthYear() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        return month + "/" + year;
    }


    private void applyAllFilters() {
        String medicineFilter = binding.autoMedicine.getText().toString().toLowerCase().trim();
        String stockistFilter = binding.autoStockist.getText().toString().toLowerCase().trim();
        String monthFilter = binding.autoMonth.getText().toString().trim();

        ArrayList<ExpiryListResponse> filteredList = new ArrayList<>();
        for (ExpiryListResponse response : originalList) {
            boolean matchesMedicine = response.getProductName() != null &&
                    response.getProductName().toLowerCase().contains(medicineFilter);

            boolean matchesStockist = response.getDealerName() != null &&
                    response.getDealerName().toLowerCase().contains(stockistFilter);

            boolean matchesMonth = response.getExpiryMonth() != null &&
                    response.getExpiryMonth().trim().equalsIgnoreCase(monthFilter);

            if ((medicineFilter.isEmpty() || matchesMedicine) &&
                    (stockistFilter.isEmpty() || matchesStockist) &&
                    (monthFilter.isEmpty() || matchesMonth)) {
                filteredList.add(response);
            }
        }

        adapter.updateList(filteredList);
    }

    private void showCalendarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.calendar_view, null);
        builder.setView(dialogView);

        final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        // Hide the day spinner to allow only month/year selection
        int daySpinnerId = getResources().getIdentifier("android:id/day", null, null);
        if (daySpinnerId != 0) {
            View daySpinner = datePicker.findViewById(daySpinnerId);
            if(daySpinner != null){
                daySpinner.setVisibility(View.GONE);
            }
        }

        TextView btnCancel = dialogView.findViewById(R.id.txt_cancel);
        TextView btnOk = dialogView.findViewById(R.id.txt_ok);
        final AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            // Get month (0-indexed) and year from the DatePicker
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();

            String selectedMonthYear = month + "/" + year;
            binding.autoMonth.setText(selectedMonthYear);
//            filterByMonth(selectedMonthYear);
            applyAllFilters();
            dialog.dismiss();
        });

        dialog.show();
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