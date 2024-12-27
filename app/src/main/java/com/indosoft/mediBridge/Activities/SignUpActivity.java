package com.indosoft.mediBridge.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.indosoft.mediBridge.Body.SignUpBody;
import com.indosoft.mediBridge.Model.IndiaStateResponse;
import com.indosoft.mediBridge.Model.StateCityResponse;
import com.indosoft.mediBridge.Session.AppSession;
import com.indosoft.mediBridge.Session.Constants;
import com.indosoft.mediBridge.ViewModel.CityViewModel;
import com.indosoft.mediBridge.ViewModel.SignUpViewModel;
import com.indosoft.mediBridge.ViewModel.StatesViewModel;
import com.indosoft.mediBridge.databinding.ActivitySignUpBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    StatesViewModel statesViewModel;
    SignUpViewModel signUpViewModel;
    CityViewModel cityViewModel;
    String text = "I accept and agree to the Terms & Conditions and privacy policy";
    SpannableString spannableString = new SpannableString(text);
    ArrayList<IndiaStateResponse> stateList = new ArrayList<>();
    ArrayList<StateCityResponse> cityList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        statesViewModel = new ViewModelProvider(this).get(StatesViewModel.class);
        statesViewModel.init(this);
        cityViewModel = new ViewModelProvider(this).get(CityViewModel.class);
        cityViewModel.init(this);
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        signUpViewModel.init(this);

        statesViewModel.getStateData();

        initClicks();

        onAttachObservers();

        int termsStart = text.indexOf("Terms & Conditions");
        int termsEnd = termsStart + "Terms & Conditions".length();
        int privacyStart = text.indexOf("privacy policy");
        int privacyEnd = privacyStart + "privacy policy".length();

        spannableString.setSpan(new StyleSpan(Typeface.BOLD), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Set the SpannableString to the CheckBox
        binding.checkbox.setText(spannableString);

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
                            Toast.makeText(SignUpActivity.this, stateId, Toast.LENGTH_SHORT).show();

                            AppSession.getInstance(SignUpActivity.this).putString(Constants.STATE_ID, stateId);

                            cityViewModel.getCityData(stateId); // Ensure this fetches cities for the selected state
                        }
                    }
                });
            }
        });

        cityViewModel.getLiveData().observe(this, stateCityResponses -> {
            if (stateCityResponses != null) {
                cityList.clear();

                for (StateCityResponse city : stateCityResponses) {
                    if (city.getStateId().equals(AppSession.getInstance(SignUpActivity.this).getString(Constants.STATE_ID))) {
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
                            AppSession.getInstance(SignUpActivity.this).putString(Constants.CITY_ID, cityId);
                        }
                    }
                });
            }
        });


        signUpViewModel.getLiveData().observe(this, signUpResponse -> {
            if (signUpResponse != null) {
                Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, DashBoardActivity.class);
                startActivity(intent);
               // AppSession.getInstance(this).putObject(Constants.SIGN_UP_MASSAGE,signUpResponse);
                finish();
            }
        });
    }


    private void initClicks() {
        binding.txtLogin.setPaintFlags(binding.txtLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.txtLogin.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));

        binding.btnSignin.setOnClickListener(v -> {
            String shopName = binding.edtShopName.getText().toString().trim();
            String mobileNumber = binding.edtMobileNo.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();
            String stateName = binding.edtState.getText().toString().trim();
            String cityName = binding.edtCity.getText().toString().trim();

            String stateId = AppSession.getInstance(this).getString(Constants.STATE_ID);
            String cityId = AppSession.getInstance(this).getString(Constants.CITY_ID);



            if (shopName.isEmpty()) {
                Toast.makeText(this, "Enter shop name", Toast.LENGTH_SHORT).show();
            } else if (mobileNumber.isEmpty()) {
                Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            } else if (stateName.isEmpty() || stateId.isEmpty()) {
                Toast.makeText(this, "Select a state", Toast.LENGTH_SHORT).show();
            } else if (cityName.isEmpty() || cityId.isEmpty()) {
                Toast.makeText(this, "Select a city", Toast.LENGTH_SHORT).show();
            } if (!binding.checkbox.isChecked()) {
                Toast.makeText(this, "You must agree to the Terms & Conditions to proceed.", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                SignUpBody body = new SignUpBody();
                body.setRetailerName(shopName);
                body.setRetailerPhone(mobileNumber);
                body.setRetailerPassword(password);
                body.setStateId(Integer.valueOf(stateId));
                body.setCityId(Integer.valueOf(cityId));
                signUpViewModel.getSignData(body);
            }
        });

        binding.checkbox.setOnClickListener(v -> showTermsConditionsPopup());

        binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.btnSignin.setEnabled(isChecked);
            } else {

            }
        });
    }

    private void showTermsConditionsPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Terms & Conditions")
                .setMessage("Here are the Terms & Conditions of the app...")
                .setCancelable(false) // Prevents closing without agreeing
                .setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        binding.checkbox.setChecked(true);
                    }
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
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