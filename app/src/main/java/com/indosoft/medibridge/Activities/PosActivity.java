package com.indosoft.medibridge.Activities;

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
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.indosoft.medibridge.Adapter.PosListAdapter;
import com.indosoft.medibridge.Adapter.SearchAdapter;
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
    private HashMap<String, String> unitNameToIdMap = new HashMap<>();
    SearchAdapter searchAdapter;
    String selectUnitId;
    private String currentQuery = "";
    private boolean isReceiverRegistered = false;
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
        String retailerId=AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        viewModel.getPosList(retailerId);
        initClicks();
        onAttachObservers();
        medicineViewModel.searchMedicine("");
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);

        adapter = new PosListAdapter(this,list,sign,()->{
            viewModel.getPosList(retailerId);

        }
        );
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this

        ));
        searchAdapter = new SearchAdapter(item -> {

            binding.etSearch.setText("");
            binding.rvSearch.setVisibility(View.GONE);

            AppSession.getInstance(this)
                    .setValue(Constants.PRODUCT_ID, String.valueOf(item.getProductId()));

            AppSession.getInstance(this)
                    .setValue(Constants.UNIT_ID, String.valueOf(item.getUnitId()));

            AppSession.getInstance(this)
                    .setValue(Constants.UNIT_NAME, item.getUnit());

            showPopup(item.getProductName(), item.getCompanyName());
        });


        binding.rvSearch.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearch.setAdapter(searchAdapter);
    }

    private void initClicks() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {

            private final Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;

            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}

            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

                currentQuery = s.toString().trim();   // ✅ IMPORTANT

                if (currentQuery.isEmpty()) {
                    binding.rvSearch.setVisibility(View.GONE);
                    searchAdapter.submitList(new ArrayList<>());
                    return;
                }

                runnable = () -> medicineViewModel.searchMedicine(currentQuery);
                handler.postDelayed(runnable, 300);
            }
        });
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.txtPosDate.setText("");
        binding.imgVoice.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US"); // ya "hi-IN" agar Hindi chahiye
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak medicine name...");
            try {
                startActivityForResult(intent, 1001);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
//        binding.tx.setOnClickListener(v -> openCalendarDialog("start"));
//        binding.txtLastDate.setOnClickListener(v -> openCalendarDialog("last"));
    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        medicineViewModel.getLiveData().observe(this, list -> {

            if (currentQuery == null || currentQuery.isEmpty()) {
                binding.rvSearch.setVisibility(View.GONE);
                return;
            }

            if (list == null || list.isEmpty()) {
                binding.rvSearch.setVisibility(View.GONE);
                return;
            }

            searchAdapter.submitList(list);
            binding.rvSearch.setVisibility(View.VISIBLE);
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

        binding.txtTotalAmount.setText("₹ " + String.format(Locale.getDefault(), "%.2f", totalAmount));
        binding.txtSoldMedicine.setText("" + totalItems);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && result.size() > 0) {
                String spokenText = result.get(0); // user jo bola
                binding.etSearch.setText(spokenText);

            }
        }
    }

    private void showPopup(String selectedProductName, String supplierName) {
        unitViewModel = new ViewModelProvider(this).get(UnitViewModel.class);
        unitViewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);
        unitViewModel.getUnits();
        String savedUnitId =
                AppSession.getInstance(this).getValue(Constants.UNIT_ID);

        String savedUnitName =
                AppSession.getInstance(this).getValue(Constants.UNIT_NAME);
        String retailerId=AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        View popupView = LayoutInflater.from(this).inflate(R.layout.pos_list_popup, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
            unitAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);

            unitSpn.setAdapter(unitAdapter);

            // ✅ AUTO SELECT UNIT FROM SESSION
            if (savedUnitId != null) {
                for (int i = 0; i < unitList.size(); i++) {
                    if (unitList.get(i).getUnitId().equals(savedUnitId)) {
                        unitSpn.setSelection(i);
                        txtUnitName.setText(savedUnitName);
                        selectUnitId = savedUnitId;
                        break;
                    }
                }
                unitSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectUnitId = unitList.get(position).getUnitId();
                        txtUnitName.setText(unitList.get(position).getUnitName());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
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
            if (amt == null || amt.isEmpty()) {
                Toast.makeText(this, "Enter Amount", Toast.LENGTH_SHORT).show();
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

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
                ///  hideNoConnectionView();
                reloadData();
            } else {
//                showNoConnectionView();
            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (!isReceiverRegistered) {
            registerReceiver(networkReceiver, filter);
            isReceiverRegistered = true;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(networkReceiver);
            isReceiverRegistered = false;
        }
    }
    private void reloadData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {

                return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}