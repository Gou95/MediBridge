package com.indosoft.medibridge.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.indosoft.medibridge.Body.SendEmailBody;
import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.GetOtpViewModel;
import com.indosoft.medibridge.ViewModel.GetSignUpUserViewModel;
import com.indosoft.medibridge.ViewModel.OtpViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.databinding.ActivityForgetPasswordBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordActivity extends AppCompatActivity {
    ActivityForgetPasswordBinding binding;

    SignUpViewModel viewModel;
    ArrayList<GetSignUpUserResponse> getAllUserList = new ArrayList<>();
    GetSignUpUserViewModel sign;
    String retailerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.init(this);
        sign = new ViewModelProvider(this).get(GetSignUpUserViewModel.class);
        sign.init(this);
        sign.getAllSignUPData();

        initClicks();
        onAttachObservers();
        startNetworkService();

    }

    private void onAttachObservers() {
        viewModel.getLiveData().observe(this, signUpResponse -> {
            if (signUpResponse != null) {
                binding.edtEmailId.setText("");
                binding.edtEnterMobile.setText("");

                // ❗ Clear session before redirecting to LoginActivity
                AppSession.getInstance(this).clear(); // 👈 Clear session

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(this, signUpResponse.getStatus(), Toast.LENGTH_SHORT).show();
            }
        });


        sign.getLiveData().observe(this, responses -> {
            if (responses != null) {
                getAllUserList.clear();
                getAllUserList.addAll(responses);

            }
            else {
                Log.e("LoginActivity", "Sign-up response is null or empty");
            }
        });


    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.btnResetPassword.setOnClickListener(v -> {
            String email = binding.edtEmailId.getText().toString().trim();
            String mobile = binding.edtEnterMobile.getText().toString().trim(); // corrected to newPassword field

            if (email.isEmpty() || !email.contains("@")) {
                Toast.makeText(this, "Enter a valid email ID", Toast.LENGTH_SHORT).show();
            }  else if (mobile.isEmpty()) {
            Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show();
        } else if (mobile.length() < 10) {
            Toast.makeText(this, "Mobile number must be 10 digits", Toast.LENGTH_SHORT).show();
        }
        else {
            validateCredentials(email,mobile);

            }
        });
    }

    private void validateCredentials(String email, String mobile) {
        boolean isUserFound = false;
        String retailerId = "";
        String retailerEmail = "";

        for (GetSignUpUserResponse user : getAllUserList) {
            String userPhone = user.getRetailerPhone() != null ? user.getRetailerPhone().toString().trim() : "";
            String userEmail = user.getRetailerEmail() != null ? user.getRetailerEmail().toString().trim() : "";

            AppSession appSession = AppSession.getInstance(this);
            appSession.setValue(Constants.RELAILER_ID, user.getRetailerId());
            appSession.setValue(Constants.RELAILER_NAME, user.getRetailerName());
            appSession.setValue(Constants.RELAILER_PASSWORD, user.getRetailerPassword());
            appSession.setValue(Constants.RELAILER_PHONE, user.getRetailerPhone());
            appSession.setValue(Constants.CITY_NAME, user.getCity());
            appSession.setValue(Constants.STATE_NAME, user.getStateName());
            appSession.setValue(Constants.STATE_ID, user.getStateId());
            appSession.setValue(Constants.CITY_ID, user.getCityId());
            appSession.setValue(Constants.RETAILER_STATUS, user.getStatus());

            if (email.equalsIgnoreCase(userEmail) && mobile.equals(userPhone)) {
                isUserFound = true;
                retailerId = user.getRetailerId();
                retailerEmail = userEmail;
                break;
            }
        }

        if (isUserFound) {
            String randomPassword = generateRandomPassword(6);

            SendEmailBody body = new SendEmailBody();
            body.setTo(retailerEmail);
            body.setSubject("Password Reset");
            body.setBody(randomPassword);
            body.setRetailerId(retailerId);

            viewModel.sendEmail(body);

            Toast.makeText(this, "Password sent to your email", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Email or Mobile number is incorrect", Toast.LENGTH_SHORT).show();
        }
    }
//    private String generateRandomPassword(int length) {
//        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//        StringBuilder password = new StringBuilder();
//        for (int i = 0; i < length; i++) {
//            int index = (int) (Math.random() * characters.length());
//            password.append(characters.charAt(index));
//        }
//        return password.toString();
//    }
private String generateRandomPassword(int length) {
    String digits = "0123456789";
    StringBuilder password = new StringBuilder();
    for (int i = 0; i < length; i++) {
        int index = (int) (Math.random() * digits.length());
        password.append(digits.charAt(index));
    }
    return password.toString();
}



    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
        Log.d("LoginActivity", "NetworkCheckService started");
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