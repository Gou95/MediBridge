package com.indosoft.medibridge.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.Body.ExpiryRegisterBody;
import com.indosoft.medibridge.Model.CityDealerResponse;
import com.indosoft.medibridge.Model.MedicineListResponse;
import com.indosoft.medibridge.Model.UnitResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.CityDealerViewModel;
import com.indosoft.medibridge.ViewModel.MedicineViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.UnitViewModel;
import com.indosoft.medibridge.databinding.ActivityUnlistedMedicineBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ExpiryRegisterActivity extends AppCompatActivity {
    ActivityUnlistedMedicineBinding binding;
    ArrayList<MedicineListResponse> itemList = new ArrayList<>();
    MedicineViewModel medicineViewModel;
    CityDealerViewModel cityDealerViewModel;
    ArrayList<String> productNameList = new ArrayList<>();
    SignUpViewModel viewModel;
    UnitViewModel unitViewModel;
    ArrayList<UnitResponse>unitList = new ArrayList<>();
    private HashMap<String, String> dealerMap = new HashMap<>();
    private HashMap<String, String> productMap = new HashMap<>();
    private String selectedProductId = null;
    private String selectedDealerId = null;
    private String selectUnitId ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUnlistedMedicineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        medicineViewModel.init(this);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.init(this);
        cityDealerViewModel = new ViewModelProvider(this).get(CityDealerViewModel.class);
        cityDealerViewModel.init(this);
        unitViewModel = new ViewModelProvider(this).get(UnitViewModel.class);
        unitViewModel.init(this);
        medicineViewModel.getMedicineData();
        unitViewModel.getUnits();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);

        cityDealerViewModel.cityDealerData(AppSession.getInstance(this).getValue(Constants.CITY_ID));


        onAttachObservers();
        initClicks();


    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.autoSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.nestedScrollView.postDelayed(() -> {
                    binding.nestedScrollView.smoothScrollTo(0, binding.autoSearch.getTop());
                }, 200);
            }
        });

        binding.edtExpiryMonth.setOnClickListener(v -> {
            showCalendarDialog();
        });
        binding.btnSubmit.setOnClickListener(v -> {
            String batchNo = binding.edtBatchNo.getText().toString();
            String expiryMonth = binding.edtExpiryMonth.getText().toString();
            String stock = binding.edtStockQty.getText().toString();

            if (selectedProductId == null) {
                Toast.makeText(this, "Select a product", Toast.LENGTH_SHORT).show();
            } else if (batchNo.isEmpty()) {
                Toast.makeText(this, "Enter batch number", Toast.LENGTH_SHORT).show();
            } else if (expiryMonth.isEmpty()) {
                Toast.makeText(this, "Enter expiry month", Toast.LENGTH_SHORT).show();
            } else if (stock.isEmpty()) {
                Toast.makeText(this, "Enter stock quantity", Toast.LENGTH_SHORT).show();
            } else if (selectedDealerId == null) {
                Toast.makeText(this, "Select a dealer", Toast.LENGTH_SHORT).show();
            } else {
                ExpiryRegisterBody body = new ExpiryRegisterBody();
                body.setRetailerId(AppSession.getInstance(this).getValue(Constants.RELAILER_ID));
                body.setProductId(selectedProductId);
                body.setBatchNo(batchNo);
                body.setExpiryMonth(expiryMonth);
                body.setStock(stock);
                body.setDealerId(selectedDealerId);
                viewModel.expiryRegister(body);
            }
        });

    }


    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);

        // Medicine List Fetch
        medicineViewModel.getLiveData().observe(this, medicineListResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (medicineListResponses != null) {
                itemList.clear();
                itemList.addAll(medicineListResponses);
                productMap.clear();
                productNameList.clear();

                for (MedicineListResponse response : medicineListResponses) {
                    if (response != null && response.getProductId() != null) {
                        productNameList.add(response.getProductName());
                        productMap.put(response.getProductName(), response.getProductId());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_items, productNameList);
                binding.autoSearch.setAdapter(adapter);

                binding.autoSearch.setOnItemClickListener((adapterView, view, position, id) -> {
                    String selectedProductName = adapterView.getItemAtPosition(position).toString();
                    selectedProductId = productMap.get(selectedProductName); // Get Product ID
                    binding.txtProductName.setText(selectedProductName);
                    binding.txtProductName.setBackgroundColor(Color.YELLOW);
                    binding.autoSearch.setText("");
                });

            }
        });
        cityDealerViewModel.getLiveData().observe(this, cityDealerResponses -> {
            if (cityDealerResponses != null) {
                List<String> stockitsList = new ArrayList<>();
                dealerMap.clear();
                for (CityDealerResponse response : cityDealerResponses) {
                    if (response != null && response.getDealerId() != null) {
                        stockitsList.add(response.getDealerName());
                        dealerMap.put(response.getDealerName(), response.getDealerId());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dealer_items, stockitsList);
                binding.autoStockist.setAdapter(adapter);

                binding.autoStockist.setOnClickListener(v -> {
                    binding.autoStockist.setText("");
                    if (binding.autoStockist.getText().toString().isEmpty()) {
                        selectedDealerId = null;
                        //  AppSession.getInstance(this).setValue(Constants.DEALER_ID, null);
                    }
                });

                binding.autoStockist.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedDealerName = parent.getItemAtPosition(position).toString();
                    selectedDealerId = dealerMap.get(selectedDealerName);
                    // AppSession.getInstance(this).setValue(Constants.DEALER_ID, selectDealerId);
                    binding.autoStockist.setText(selectedDealerName);
                });
            }
        });
        viewModel.getLiveData().observe(this, signUpResponse -> {
            if (signUpResponse != null) {
                Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        unitViewModel.getLiveData().observe(this, unitResponses -> {
            if (unitResponses != null && !unitResponses.isEmpty()) {
                unitList.clear();
                unitList.addAll(unitResponses);
                List<String> unitNames = new ArrayList<>();

                for (UnitResponse unit : unitList) {
                    unitNames.add(unit.getUnitName());

                }
                ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitNames);
                unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinner.setAdapter(unitAdapter);



                binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectUnitId = unitList.get(position).getUnitId();

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

            }
        });

    }

    private void showCalendarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.calendar_view,null); // Replace with your XML filename
       builder.setView(dialogView);

        CalendarView calendarView = dialogView.findViewById(R.id.calendarView);
        TextView btnCancel = dialogView.findViewById(R.id.txt_cancel);
        TextView btnOk = dialogView.findViewById(R.id.txt_ok);

        final Calendar selectedDate = Calendar.getInstance();
       AlertDialog dialog = builder.create();
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnOk.setOnClickListener(v -> {
            int selectedMonth = selectedDate.get(Calendar.MONTH) + 1; // 0-based index
            int selectedYear = selectedDate.get(Calendar.YEAR);
            binding.edtExpiryMonth.setText(selectedMonth + "/" + selectedYear);
            dialog.dismiss();
        });

        dialog.show();
    }


}