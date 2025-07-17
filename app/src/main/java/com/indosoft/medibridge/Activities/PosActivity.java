package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.indosoft.medibridge.Adapter.PosListAdapter;
import com.indosoft.medibridge.Body.AddtoCartBody;
import com.indosoft.medibridge.Body.PosAddBody;
import com.indosoft.medibridge.Model.CityDealerResponse;
import com.indosoft.medibridge.Model.GetPosProductResponse;
import com.indosoft.medibridge.Model.LastStockitsResponse;
import com.indosoft.medibridge.Model.MedicineListResponse;
import com.indosoft.medibridge.Model.UnitResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.CityDealerViewModel;
import com.indosoft.medibridge.ViewModel.LastStockitsViewModel;
import com.indosoft.medibridge.ViewModel.MedicineViewModel;
import com.indosoft.medibridge.ViewModel.PosProductViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.UnitViewModel;
import com.indosoft.medibridge.databinding.ActivityPosBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class PosActivity extends AppCompatActivity {
    ActivityPosBinding binding;
    MedicineViewModel medicineViewModel;
    UnitViewModel unitViewModel;
    SignUpViewModel sign;
    PosProductViewModel viewModel;
    PosListAdapter adapter;
    ArrayList<UnitResponse> unitList = new ArrayList<>();
    ArrayList<GetPosProductResponse> list = new ArrayList<>();
    ArrayList<MedicineListResponse> itemList = new ArrayList<>();
    private HashMap<String, String> productMap = new HashMap<>();
    private HashMap<String, String> unitNameToIdMap = new HashMap<>();
    String selectDealerId;
    String selectUnitId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        medicineViewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        medicineViewModel.init(this);
        viewModel = new ViewModelProvider(this).get(PosProductViewModel.class);
        viewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.init(this);
        medicineViewModel.getMedicineData();
        String retailerId=AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        viewModel.getPosList(retailerId);
        initClicks();
        onAttachObservers();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);

        adapter = new PosListAdapter(this,list,sign,()->{
            viewModel.getPosList(retailerId);

        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this

        ));
    }

    private void initClicks() {
        binding.autoMedi.setOnItemClickListener(this::onProductSelected);
        binding.autoMedi.addTextChangedListener(new TextWatcher() {
            private Handler handler = new Handler();
            private Runnable inputFinishChecker;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputFinishChecker != null) {
                    handler.removeCallbacks(inputFinishChecker);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String enteredText = s.toString().trim();

                inputFinishChecker = () -> {
                    if (!enteredText.isEmpty() && !productMap.containsKey(enteredText)) {
                        //  Toast.makeText(getContext(), "This product is not in the list. Please select unlisted medicine.", Toast.LENGTH_SHORT).show();
                    }
                };

                handler.postDelayed(inputFinishChecker, 2000); // 1 second delay
            }
        });
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.txtPosDate.setText("");
    }

    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        medicineViewModel.getLiveData().observe(this, medicineListResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (medicineListResponses != null && !medicineListResponses.isEmpty()) {
                itemList.clear();
                itemList.addAll(medicineListResponses);
                List<String> productNameList = new ArrayList<>();
                productMap.clear();
                for (MedicineListResponse response : medicineListResponses) {
                    if (response != null && response.getProductId() != null) {
                        productNameList.add(response.getProductName());
                        productMap.put(response.getProductName(), response.getProductId());
                        AppSession.getInstance(this).setValue(Constants.PRODUCT_ID, response.getProductId());

                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_items, productNameList);
                binding.autoMedi.setAdapter(adapter);

            } else {
                Log.e("HomeFragment", "Medicine data is null or empty");
            }
        });
        viewModel.getLiveData().observe(this, responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null) {
                list.clear();

                // ✅ Get today's date in yyyy-MM-dd format
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                for (GetPosProductResponse item : responses) {
                    if (item.getAddtime() != null && item.getAddtime().startsWith(today)) {
                        list.add(item); // ✅ Add only today's items
                    }
                }

                adapter.notifyDataSetChanged();
                calculateTotalAmount();

                // ✅ Set date on top
                binding.txtPosDate.setText("" + today);
            }
        });

    }

    private void onProductSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedProductName = parent.getItemAtPosition(position).toString();
        String selectedProductId = productMap.get(selectedProductName);

        if (selectedProductId != null) {

            AppSession.getInstance(this).setValue(Constants.PRODUCT_ID, selectedProductId);
            String supplierName = getSupplierNameForProduct(selectedProductId);
            showPopup(selectedProductName, supplierName);
            binding.autoMedi.setText("");
        }
    }
    private String getSupplierNameForProduct(String productId) {
        for (MedicineListResponse medicine : itemList) {
            if (medicine.getProductId().equals(productId)) {
                return medicine.getSupplierName();
            }
        }
        return "Supplier not available";
    }
    private String getUnitForProduct(String productName) {
        for (MedicineListResponse medicine : itemList) {
            if (medicine.getProductName().equalsIgnoreCase(productName)) {
                // Toast.makeText(getContext(), medicine.getProductId(), Toast.LENGTH_SHORT).show();
                return (String) medicine.getUnitName();
            }
        }
        return null;

    }
    private void calculateTotalAmount() {
        double totalAmount = 0.0;
        int totalItems = 0;

        for (GetPosProductResponse item : list) {
            try {
                if (item.getAmount() != null && !item.getAmount().isEmpty()) {
                    totalAmount += Double.parseDouble(item.getAmount());
                }
                totalItems++;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Show total amount
        binding.txtTotalAmount.setText("₹ " + String.format(Locale.getDefault(), "%.2f", totalAmount));

        // Show total number of sold medicine items
        binding.txtSoldMedicine.setText("" + totalItems);
    }


    private void showPopup(String selectedProductName, String supplierName) {
        unitViewModel = new ViewModelProvider(this).get(UnitViewModel.class);
        unitViewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);
        unitViewModel.getUnits();
        String retailerId=AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        View popupView = LayoutInflater.from(this).inflate(R.layout.pos_list_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        TextView txtItemDetails = popupView.findViewById(R.id.txt_posMediName);
        txtItemDetails.setText(selectedProductName);
        TextView companyNm = popupView.findViewById(R.id.txt_posCompanyName);
        companyNm.setText(supplierName);
        TextView txtNumber = popupView.findViewById(R.id.txt_number);
        TextView txtUnitName = popupView.findViewById(R.id.txt_posUnitName);
        ImageView imgSub = popupView.findViewById(R.id.img_sub);
        ImageView imgAdd = popupView.findViewById(R.id.img_add);
        ImageView imgCancel = popupView.findViewById(R.id.img_cancle);
        CardView addCart = popupView.findViewById(R.id.btn_addCart);
        Spinner unitSpn = popupView.findViewById(R.id.spin_unit);
        EditText amount = popupView.findViewById(R.id.edt_posAmount);
       // AutoCompleteTextView dealerName = popupView.findViewById(R.id.auto_dealerName);
        unitViewModel.getLiveData().observe(this, unitResponses -> {
            if (unitResponses != null && !unitResponses.isEmpty()) {
                unitList.clear();
                unitList.addAll(unitResponses);
                List<String> unitNames = new ArrayList<>();
                unitNameToIdMap.clear();
                for (UnitResponse unit : unitList) {
                    unitNames.add(unit.getUnitName());
                    unitNameToIdMap.put(unit.getUnitName(), unit.getUnitId());
                    AppSession.getInstance(this).setValue(Constants.UNIT_ID, unit.getUnitId());
                }
                ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitNames);
                unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                unitSpn.setAdapter(unitAdapter);
                String selectedUnitName = getUnitForProduct(selectedProductName);
                if (selectedUnitName != null) {
                    txtUnitName.setText(selectedUnitName);
                    selectUnitId = unitNameToIdMap.get(selectedUnitName);
                } else {
                    txtUnitName.setText("Unit not available");
                    Log.e("UnitError", "No unit name found for product: " + selectedProductName);
                }
                unitSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectUnitId = unitList.get(position).getUnitId();
                        txtUnitName.setText(selectedUnitName);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                txtUnitName.setOnClickListener(v -> {
                    txtUnitName.setVisibility(View.GONE);
                    unitSpn.setVisibility(View.VISIBLE);
                    unitSpn.performClick();
                });
            }
        });
        sign.getLiveData().observe(this,signUpResponse -> {
            if (signUpResponse !=null){
                viewModel.getPosList(retailerId);

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }            }
        });

        addCart.setOnClickListener(v -> {
            String product_id = AppSession.getInstance(this).getValue(Constants.PRODUCT_ID);
            String quantity = txtNumber.getText().toString();
            String amt = amount.getText().toString();
            String unitId = selectUnitId;

            if (product_id == null || product_id.isEmpty()) {
                Toast.makeText(this, "Please select a product", Toast.LENGTH_SHORT).show();
                return;
            }
            if (unitId == null || unitId.isEmpty()) {
                Toast.makeText(this, "Please select a unit", Toast.LENGTH_SHORT).show();
                return;
            }
            if (quantity == null || quantity.isEmpty() || Integer.parseInt(quantity) <= 0) {
                Toast.makeText(this, "Please select a valid quantity greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }


            PosAddBody body = new PosAddBody();
            body.setProductId(product_id);
            body.setUnitId(unitId);
            body.setQty(quantity);
            body.setAmount(amt);
          //  body.setAddtime(formattedDateTime);  // ✅ this must not be null or empty

            Log.d("POS_BODY", new Gson().toJson(body));  // ✅ check output

            sign.posAdd(retailerId, body);


            //  cartViewModel.getAddToCardData(body);

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        });

        AtomicInteger number = new AtomicInteger();
        try {
            number.set(Integer.parseInt(txtNumber.getText().toString()));
        } catch (NumberFormatException e) {
            txtNumber.setText("1");
        }
        imgSub.setOnClickListener(v -> {
            if (number.get() > 1) {
                number.getAndDecrement();
                txtNumber.setText(String.valueOf(number.get()));
            }
        });
        imgAdd.setOnClickListener(v -> {
            number.getAndIncrement();
            txtNumber.setText(String.valueOf(number.get()));
        });
        imgCancel.setOnClickListener(v -> dialog.dismiss());
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.TOP);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}