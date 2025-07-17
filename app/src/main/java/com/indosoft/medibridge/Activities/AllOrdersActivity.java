package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.AllOrdersListAdapter;
import com.indosoft.medibridge.Model.OrderDetailsResponse;
import com.indosoft.medibridge.Model.OrderListResponse;
import com.indosoft.medibridge.Model.OrderRegisterResponse;
import com.indosoft.medibridge.Model.StockitsResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.OrderDetailsViewModel;
import com.indosoft.medibridge.ViewModel.OrderRegisterViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.databinding.ActivityAllOrdersBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AllOrdersActivity extends AppCompatActivity {
    ActivityAllOrdersBinding binding;
    ArrayList<OrderDetailsResponse> list = new ArrayList<>();
    AllOrdersListAdapter adapter;
    OrderDetailsViewModel viewModel;
    SignUpViewModel sign;
    private String startDateSelected = null;
    private String lastDateSelected = null;
    ActivityResultLauncher<Intent> expiryLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityAllOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);
        viewModel.init(this);

        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);
        String retailer_id = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        viewModel.getOrderDetailsData(retailer_id);

        expiryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String productId = result.getData().getStringExtra("productId");
                        String expiryMonth = result.getData().getStringExtra("expiryMonth");

                        for (OrderDetailsResponse item : list) {
                            if (item.getProductId().equals(productId)) {
                                item.setExpiryMonth(expiryMonth);
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
        );

        adapter = new AllOrdersListAdapter(this, list, sign, expiryLauncher);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        onAttachObservers();
        initClicks();
        setDefaultDates();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);




    }
    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.txtStartDate.setOnClickListener(v -> {
            openCalendarDialog("start");
        });

        binding.txtLastDate.setOnClickListener(v -> {
            openCalendarDialog("last");
        });
        TextView title = binding.txtOrder;
        SpannableString spannable = new SpannableString("All Orders");
        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 4, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);
    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(this, orderDetailsResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            list.clear();
            if (orderDetailsResponses != null) {
                list.addAll(orderDetailsResponses);
                filterListByDateRange();
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show();
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
            adapter.updateList(list); // fallback to full list
            return;
        }
        ArrayList<OrderDetailsResponse> filteredList = new ArrayList<>();
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

            for (OrderDetailsResponse response : list) {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String productId = data.getStringExtra("productId");
            String expiryMonth = data.getStringExtra("expiryMonth");

            for (OrderDetailsResponse item : list) {
                if (item.getProductId().equals(productId)) {
                    item.setExpiryMonth(expiryMonth);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String retailer_id = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        viewModel.getOrderDetailsData(retailer_id);
    }
}