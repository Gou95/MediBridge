package com.indosoft.medibridge.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.CompanyNameAdapter;
import com.indosoft.medibridge.Adapter.StockistListAdapter;
import com.indosoft.medibridge.Body.StockistBody;
import com.indosoft.medibridge.Model.CompanyResponse;
import com.indosoft.medibridge.Model.IndiaStateResponse;
import com.indosoft.medibridge.Model.StateCityResponse;
import com.indosoft.medibridge.Model.StockistListResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.CityViewModel;
import com.indosoft.medibridge.ViewModel.CompanyViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.StatesViewModel;
import com.indosoft.medibridge.ViewModel.StockistListViewModel;
import com.indosoft.medibridge.databinding.ActivityStockistBinding;

import java.util.ArrayList;
import java.util.List;

public class StockistActivity extends AppCompatActivity {
    ActivityStockistBinding binding;
    ArrayList<StockistListResponse> list = new ArrayList<>();
    ArrayList<StockistListResponse> originalList = new ArrayList<>();
    StockistListViewModel viewModel;
    StockistListAdapter adapter;
    CompanyNameAdapter companyNameAdapter;
    CompanyViewModel companyViewModel;
    StatesViewModel statesViewModel;
    CityViewModel cityViewModel;
    ArrayList<IndiaStateResponse> stateList = new ArrayList<>();
    ArrayList<StateCityResponse> cityList = new ArrayList<>();
    SignUpViewModel sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityStockistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(StockistListViewModel.class);
        viewModel.init(this);
        companyViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);
        companyViewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);
        String cityId = AppSession.getInstance(this).getValue(Constants.CITY_ID);
        viewModel.stockitsList(cityId);
        onAttachObservers();
        initClicks();

        adapter = new StockistListAdapter(this,list,companyViewModel);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void initClicks() {
        binding.imgAddStockist.setOnClickListener(v -> {
            showPopupStockist();
        });
        binding.autoStockist.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                filterStockistByDealer(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }
    private void onAttachObservers() {
        viewModel.getLiveData().observe(this, responses -> {
            if (responses != null) {
                list.clear();
                originalList.clear();

                originalList.addAll(responses); // full data
                list.addAll(responses);         // display data

                adapter.notifyDataSetChanged();
            }
        });

        TextView title = binding.txtStockist;
        SpannableString spannable = new SpannableString("Stockist List");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 9, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);
    }
    private void filterStockistByDealer(String dealerName) {
        list.clear();

        if (dealerName.isEmpty()) {
            list.addAll(originalList);
        } else {
            for (StockistListResponse item : originalList) {
                if (item.getDealerName() != null &&
                        item.getDealerName().toLowerCase().contains(dealerName.toLowerCase())) {
                    list.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    @SuppressLint("MissingInflatedId")
    private void showPopupStockist() {
        statesViewModel = new ViewModelProvider(this).get(StatesViewModel.class);
        statesViewModel.init(this);
        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);
        cityViewModel.init(this);
        statesViewModel.getStateData();

        View popupView = LayoutInflater.from(this).inflate(R.layout.add_new_stockist, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        AutoCompleteTextView stockistName = popupView.findViewById(R.id.auto_stockist);
        AutoCompleteTextView stateName = popupView.findViewById(R.id.edt_state);
        AutoCompleteTextView cityName = popupView.findViewById(R.id.edt_city);
        EditText address = popupView.findViewById(R.id.edt_stockistAdd);
        CardView submit = popupView.findViewById(R.id.btn_submit);
        ImageView cancel = popupView.findViewById(R.id.img_cancle);

        statesViewModel.getLiveData().observe(this, indiaStateResponses -> {
            if (indiaStateResponses != null) {
                stateList.clear();
                stateList.addAll(indiaStateResponses);

                Log.d("States", "onAttachObservers: " + stateList);

                ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line
                );

                for (IndiaStateResponse state : stateList) {
                    stateAdapter.add(state.getState());
                }
                stateAdapter.setNotifyOnChange(true);

                stateName.setAdapter(stateAdapter);
                stateName.setThreshold(1);
                stateName.showDropDown();


                stateName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                        String selectedState = (String) parentView.getItemAtPosition(position);
                        Log.d("State Selected", selectedState);
                        IndiaStateResponse state = getStateByName(selectedState);

                        if (state != null) {
                            String stateId = state.getId();
                            Log.d("State ID", "Selected State ID: " + stateId);
                            //  AppSession.getInstance(UnlistedStockistActivity.this).setValue(Constants.STATE_ID, stateId);
                            cityViewModel.getCityData(stateId);
                        }
                    }
                });
            }
        });
        cityViewModel.getLiveData().observe(this, stateCityResponses -> {
            if (stateCityResponses != null) {
                cityList.clear();

                for (StateCityResponse city : stateCityResponses) {
                    if (city.getStateId().equals(AppSession.getInstance(StockistActivity.this).getValue(Constants.STATE_ID))) {
                        cityList.add(city);
                    }
                }

                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
                for (StateCityResponse city : cityList) {
                    cityAdapter.add(city.getCity()); // Adjust this to the actual property of city name
                }
                cityName.setAdapter(cityAdapter);
                cityName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                        String selectedCity = (String) parentView.getItemAtPosition(position);
                        Log.d("City Selected", selectedCity);
                        StateCityResponse city = getCityByName(selectedCity);

                        if (city != null) {
                            String cityId = city.getCityId();
                            Log.d("City ID", "Selected City ID: " + cityId);

                            //  AppSession.getInstance(UnlistedStockistActivity.this).setValue(Constants.CITY_ID, cityId);


                            Log.d("City ID", "Selected City ID: " + city.getCity());

                        }
                    }
                });
            }
        });
        viewModel.getLiveData().observe(this, responses -> {
            if (responses != null) {
                list.clear();
                list.addAll(responses);

                List<String> dealerNames = new ArrayList<>();
                for (StockistListResponse dealer : list) {
                    if (dealer.getDealerName() != null) {
                        dealerNames.add(dealer.getDealerName());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        dealerNames
                );

                stockistName.setAdapter(adapter);
                stockistName.setThreshold(1); // show suggestions after typing 1 letter
            }
        });
        submit.setOnClickListener(v -> {

            String dealerName = stockistName.getText().toString().trim();
            String stateNm = stateName.getText().toString().trim();
            String cityNm = cityName.getText().toString().trim();
            String add = address.getText().toString().trim();

            String stateId = AppSession.getInstance(this).getValue(Constants.STATE_ID);
            String cityId = AppSession.getInstance(this).getValue(Constants.CITY_ID);

            if (dealerName.isEmpty()) {
                Toast.makeText(this, "Enter stockist name", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ NEW CHECK (IMPORTANT)
            if (isDealerAlreadyExists(dealerName)) {
                stockistName.setError("Dealer name already exist, please enter new dealer name");
                stockistName.requestFocus();
                return;
            }

            if (stateNm.isEmpty() || stateId == null || stateId.isEmpty()) {
                Toast.makeText(this, "Select a state", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cityNm.isEmpty() || cityId == null || cityId.isEmpty()) {
                Toast.makeText(this, "Select a city", Toast.LENGTH_SHORT).show();
                return;
            }

            if (add.isEmpty()) {
                Toast.makeText(this, "Enter address", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ API CALL ONLY IF NAME IS UNIQUE
            StockistBody body = new StockistBody();
            body.setDealerName(dealerName);
            body.setStateId(stateId);
            body.setCityId(cityId);
            body.setFcmId(AppSession.getInstance(this).getValue(Constants.STOCKIST_FCM_TOKEN));
            body.setDealerAddress(add);

            sign.registerStockist(body);

            sign.getLiveData().observe(this, signUpResponse -> {
                if (signUpResponse != null) {
                    viewModel.stockitsList(cityId);
                    Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        cancel.setOnClickListener(v -> dialog.dismiss() );
        stockistName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (isDealerAlreadyExists(s.toString().trim())) {
                    stockistName.setError("Dealer already exists");
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        dialog.show();

    }
    private boolean isDealerAlreadyExists(String dealerName) {
        for (StockistListResponse item : originalList) {
            if (item.getDealerName() != null &&
                    item.getDealerName().equalsIgnoreCase(dealerName)) {
                return true;
            }
        }
        return false;
    }

    private IndiaStateResponse getStateByName(String stateName) {
        for (IndiaStateResponse state : stateList) {
            if (state.getState().equalsIgnoreCase(stateName)) {
                return state;
            }
        }
        return null;
    }
    private StateCityResponse getCityByName(String cityName) {
        for (StateCityResponse city : cityList) {
            if (city.getCity().equalsIgnoreCase(cityName)) {
                return city;
            }
        }
        return null;
    }
}