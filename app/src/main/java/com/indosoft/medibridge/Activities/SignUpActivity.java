package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.Body.SignUpBody;
import com.indosoft.medibridge.Model.IndiaStateResponse;
import com.indosoft.medibridge.Model.StateCityResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.CityViewModel;
import com.indosoft.medibridge.ViewModel.ExitMobileViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.StatesViewModel;
import com.indosoft.medibridge.databinding.ActivitySignUpBinding;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    StatesViewModel statesViewModel;
    SignUpViewModel signUpViewModel;
    CityViewModel cityViewModel;
    String text = "I accept and agree to the Terms & Conditions and privacy policy";
    SpannableString spannableString = new SpannableString(text);
    ArrayList<IndiaStateResponse> stateList = new ArrayList<>();
    ArrayList<StateCityResponse> cityList = new ArrayList<>();
    ExitMobileViewModel exitMobileViewModel;
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
        startNetworkService();

        int termsStart = text.indexOf("Terms & Conditions");
        int termsEnd = termsStart + "Terms & Conditions".length();
        int privacyStart = text.indexOf("privacy policy");
        int privacyEnd = privacyStart + "privacy policy".length();

        spannableString.setSpan(new StyleSpan(Typeface.BOLD), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

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
                            AppSession.getInstance(SignUpActivity.this).setValue(Constants.STATE_ID, stateId);
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
                    if (city.getStateId().equals(AppSession.getInstance(SignUpActivity.this).getValue(Constants.STATE_ID))) {
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

                            AppSession.getInstance(SignUpActivity.this).setValue(Constants.CITY_ID, cityId);


                            Log.d("City ID", "Selected City ID: " + city.getCity());

                        }
                    }
                });
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

            String stateId = AppSession.getInstance(this).getValue(Constants.STATE_ID);
            String cityId = AppSession.getInstance(this).getValue(Constants.CITY_ID);

            // Validations
            if (shopName.isEmpty()) {
                Toast.makeText(this, "Enter shop name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mobileNumber.isEmpty()) {
                Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (stateName.isEmpty() || stateId == null || stateId.isEmpty()) {
                Toast.makeText(this, "Select a state", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cityName.isEmpty() || cityId == null || cityId.isEmpty()) {
                Toast.makeText(this, "Select a city", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!binding.checkbox.isChecked()) {
                Toast.makeText(this, "You must agree to the Terms & Conditions to proceed.", Toast.LENGTH_SHORT).show();
                return;
            }

            SignUpBody signbody = new SignUpBody();
            signbody.setRetailerName(shopName);
            signbody.setRetailerPhone(mobileNumber);
            signbody.setRetailerPassword(password);
            signbody.setStateId(Integer.parseInt(stateId));
            signbody.setCityId(Integer.parseInt(cityId));

            signUpViewModel.getSignData(signbody);

            signUpViewModel.getLiveData().observe(this, signUpResponse -> {
                if (signUpResponse != null) {
                    String message = signUpResponse.getMessage();
                    if ("Mobile No. already registered!".equals(message)) {
                        Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if ("User added successfully".equals(message)) {
                       // Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        AppSession.getInstance(this).setValue(Constants.RELAILER_NAME, shopName);
                        AppSession.getInstance(this).setValue(Constants.RELAILER_PHONE, mobileNumber);
                        AppSession.getInstance(this).setValue(Constants.RELAILER_PASSWORD, password);
                        AppSession.getInstance(this).setValue(Constants.STATE_NAME, stateName);
                        AppSession.getInstance(this).setValue(Constants.CITY_NAME, cityName);


                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

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
        View view = getLayoutInflater().inflate(R.layout.popup_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setView(view)
                .setCancelable(false);

        TextView title = view.findViewById(R.id.popup_title);
        TextView productName = view.findViewById(R.id.popup_product_name);
        TextView message = view.findViewById(R.id.popup_message);
        TextView cancel = view.findViewById(R.id.popup_cancel);
        TextView confirm = view.findViewById(R.id.popup_confirm);

        title.setText("Terms & Conditions");
        message.setText("Do you accept the Terms & Conditions and Privacy Policy?");

        AlertDialog dialog = builder.create();

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });


        confirm.setOnClickListener(v -> {
            binding.checkbox.setChecked(true);
            dialog.dismiss();
        });

        // Show the AlertDialog
        dialog.show();
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

    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
        Log.d("LoginActivity", "NetworkCheckService started");
    }

}