package com.indosoft.medibridge.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.Body.UnlistedItemBody;
import com.indosoft.medibridge.Model.CityDealerResponse;
import com.indosoft.medibridge.Model.UnitResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.AddtoCartViewModel;
import com.indosoft.medibridge.ViewModel.CityDealerViewModel;
import com.indosoft.medibridge.ViewModel.LastStockitsViewModel;
import com.indosoft.medibridge.ViewModel.MedicineViewModel;
import com.indosoft.medibridge.ViewModel.UnitViewModel;
import com.indosoft.medibridge.ViewModel.UnlistedItemViewModel;
import com.indosoft.medibridge.databinding.ActivityUnlistedItemsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnlistedItemsActivity extends AppCompatActivity {
    ActivityUnlistedItemsBinding binding;
    UnitViewModel unitViewModel;
    CityDealerViewModel cityDealerViewModel;
    ArrayList<UnitResponse> unitList = new ArrayList<>();
    private HashMap<String, String> dealerMap = new HashMap<>();
    private HashMap<String, String> unitNameToIdMap = new HashMap<>();
    String selectDealerId;
    String selectUnitId;
    UnlistedItemViewModel unlistedItemViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityUnlistedItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        selectDealerId = null;
        selectUnitId = null;
        AppSession.getInstance(this).setValue(Constants.DEALER_ID, null);

        unitViewModel = new ViewModelProvider(this).get(UnitViewModel.class);
        unitViewModel.init(this);
        unlistedItemViewModel = new ViewModelProvider(this).get(UnlistedItemViewModel.class);
        unlistedItemViewModel.init(this);

//        cityDealerViewModel = new ViewModelProvider(this).get(CityDealerViewModel.class);
//        cityDealerViewModel.init(this);


        unitViewModel.getUnits();
        initClicks();
        onAttachObservers();

    }

    private void initClicks() {

        binding.btnUnListaddCart.setOnClickListener(v -> {

            hideKeyboard();
            String productName = binding.etEnterMedicine.getText().toString().trim();
            String unitName = binding.txtUnitName.getText().toString().trim();

            // 🔴 Validation
            if (productName.isEmpty()) {
                Toast.makeText(this, "Enter product name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (unitName.isEmpty()) {
                Toast.makeText(this, "Select unit", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 Body create karo
            UnlistedItemBody body = new UnlistedItemBody();
            body.setProductName(productName);
            body.setUnit(unitName);
            body.setCompanyName("");
            body.setProductModel("");
            body.setCreatedBy(1);

            unlistedItemViewModel.unlistedItemAdd(body);

        });
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.layoutUnitBox.setOnClickListener(v -> {
            binding.txtUnitName.setVisibility(View.GONE);
            binding.spinnerUnit.setVisibility(View.VISIBLE);
            binding.spinnerUnit.performClick();
        });

    }
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    private void onAttachObservers() {
        String savedUnitId =
                AppSession.getInstance(this).getValue(Constants.UNIT_ID);

        String savedUnitName =
                AppSession.getInstance(this).getValue(Constants.UNIT_NAME);
        unitViewModel.getLiveData().observe(this, unitResponses -> {

            if (unitResponses == null || unitResponses.isEmpty()) return;

            unitList.clear();
            unitList.addAll(unitResponses);

            List<String> unitNames = new ArrayList<>();
            unitNameToIdMap.clear();

            for (UnitResponse unit : unitList) {
                unitNames.add(unit.getUnitName());
                unitNameToIdMap.put(unit.getUnitName(), unit.getUnitId());
            }

            ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    unitNames
            );
            unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            binding.spinnerUnit.setAdapter(unitAdapter);

            // 🔥 ALWAYS SET (not inside if)
            binding.spinnerUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    selectUnitId = unitList.get(position).getUnitId();
                    binding.txtUnitName.setText(unitList.get(position).getUnitName());

                    binding.spinnerUnit.setVisibility(View.GONE);
                    binding.txtUnitName.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            // ✅ Restore session if exists
            if (savedUnitId != null) {
                for (int i = 0; i < unitList.size(); i++) {
                    if (unitList.get(i).getUnitId().equals(savedUnitId)) {
                        binding.spinnerUnit.setSelection(i);
                        binding.txtUnitName.setText(savedUnitName);
                        selectUnitId = savedUnitId;
                        break;
                    }
                }
            }
        });

        unlistedItemViewModel.getLiveData().observe(this, response -> {
            if (response != null) {

                Toast toast = Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT);

                toast.setGravity(android.view.Gravity.BOTTOM, 0, 200);

                toast.show();

                if (response.getSuccess()) {

                    binding.etEnterMedicine.setText("");
                    binding.txtUnitName.setText("");

                    selectUnitId = null;

                    binding.spinnerUnit.setSelection(0);

                    // 🔥 Optional auto open
                    binding.txtUnitName.setVisibility(View.GONE);
                    binding.spinnerUnit.setVisibility(View.VISIBLE);
                }
            }
        });


    }

}