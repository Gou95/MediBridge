package com.indosoft.medibridge.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.indosoft.medibridge.Body.StockistBody;
import com.indosoft.medibridge.Model.IndiaStateResponse;
import com.indosoft.medibridge.Model.StateCityResponse;
import com.indosoft.medibridge.Model.StockistListResponse;
import com.indosoft.medibridge.Model.UnitResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.CityViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.StatesViewModel;
import com.indosoft.medibridge.ViewModel.StockistListViewModel;
import com.indosoft.medibridge.ViewModel.UnitViewModel;
import com.indosoft.medibridge.databinding.ActivityUnlistedStockistBinding;

import java.util.ArrayList;
import java.util.List;

public class UnlistedStockistActivity extends AppCompatActivity {
ActivityUnlistedStockistBinding binding;
    SignUpViewModel sign;
    StatesViewModel statesViewModel;
    CityViewModel cityViewModel;
    ArrayList<IndiaStateResponse> stateList = new ArrayList<>();
    ArrayList<StateCityResponse> cityList = new ArrayList<>();
    private boolean isReceiverRegistered = false;
    ArrayList<StockistListResponse> list = new ArrayList<>();
    StockistListViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityUnlistedStockistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);
        statesViewModel = new ViewModelProvider(this).get(StatesViewModel.class);
        statesViewModel.init(this);
        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);
        cityViewModel.init(this);
        viewModel = new ViewModelProvider(this).get(StockistListViewModel.class);
        viewModel.init(this);

        statesViewModel.getStateData();
        String cityId = AppSession.getInstance(this).getValue(Constants.CITY_ID);
        viewModel.stockitsList(cityId);
        onAttachObservers();
        intiClicks();
        logFCM();
    }
    private void intiClicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.btnSubmit.setOnClickListener(v -> {

            String stockistName = binding.autoStockist.getText().toString().trim();
            String stateName = binding.edtState.getText().toString().trim();
            String cityName = binding.edtCity.getText().toString().trim();
            String address = binding.edtStockistAdd.getText().toString().trim();

            String stateId = AppSession.getInstance(this).getValue(Constants.STATE_ID);
            String cityId = AppSession.getInstance(this).getValue(Constants.CITY_ID);


            if (stockistName.isEmpty()) {
                Toast.makeText(this, "enter stockist name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (stateName.isEmpty() || stateId == null || stateId.isEmpty()) {
                Toast.makeText(this, "select a state", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cityName.isEmpty() || cityId == null || cityId.isEmpty()) {
                Toast.makeText(this, "select a city", Toast.LENGTH_SHORT).show();
                return;
            }
            if (address.isEmpty()) {
                Toast.makeText(this, "enter address", Toast.LENGTH_SHORT).show();
                return;
            }

            StockistBody body = new StockistBody();
            body.setDealerName(stockistName);
            body.setStateId(stateId);
            body.setCityId(cityId);
            body.setFcmId(AppSession.getInstance(this).getValue(Constants.STOCKIST_FCM_TOKEN));
            body.setDealerAddress(address);
            sign.registerStockist(body);

            sign.getLiveData().observe(this, signUpResponse -> {
                if (signUpResponse != null) {
                    //String message = signUpResponse.getMessage();
                   binding.autoStockist.setText("");
                  binding.edtState.setText("");
                  binding.edtCity.setText("");
                  binding.edtStockistAdd.setText("");
                    Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            });
        });

        TextView title = binding.txtUnlisted;
        SpannableString spannable = new SpannableString("Unlisted Stockist");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 9, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);

    }


    private void onAttachObservers() {
        statesViewModel.getLiveData().observe(this, indiaStateResponses -> {
            if (indiaStateResponses != null) {
                stateList.clear();
                stateList.addAll(indiaStateResponses);

                Log.d("States", "onAttachObservers: " + stateList);

                ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
                for (IndiaStateResponse state : stateList) {
                    stateAdapter.add(state.getState());  // Adjust this to the actual property of state name
                }
                binding.edtState.setAdapter(stateAdapter);

                binding.edtState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                    if (city.getStateId().equals(AppSession.getInstance(UnlistedStockistActivity.this).getValue(Constants.STATE_ID))) {
                        cityList.add(city);
                    }
                }

                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
                for (StateCityResponse city : cityList) {
                    cityAdapter.add(city.getCity()); // Adjust this to the actual property of city name
                }
                binding.edtCity.setAdapter(cityAdapter);
                binding.edtCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

                binding.autoStockist.setAdapter(adapter);
                binding.autoStockist.setThreshold(1); // show suggestions after typing 1 letter
            }
        });


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
    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {

                reloadData();
            } else {

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
        }, 5000);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale > 1.0f) {
            newConfig.fontScale = 1.0f;
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
        super.onConfigurationChanged(newConfig);
    }
    private void logFCM(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.i("##########FCM_TOKEN##########", "Fetching FCM token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.i("##########FCM_TOKEN##########", "FCM Token: " + token);
                        AppSession.getInstance(UnlistedStockistActivity.this).setValue(Constants.STOCKIST_FCM_TOKEN,token);
                    }
                });


    }
}