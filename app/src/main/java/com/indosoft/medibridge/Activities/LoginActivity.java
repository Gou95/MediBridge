package com.indosoft.medibridge.Activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.Model.LoginResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.GetSignUpUserViewModel;
import com.indosoft.medibridge.ViewModel.LoginViewModel;
import com.indosoft.medibridge.databinding.ActivityLoginBinding;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
ActivityLoginBinding binding;
    boolean isPasswordVisible = false;
    LoginViewModel loginViewModel;
    ArrayList<LoginResponse> loginList = new ArrayList<>();
    ArrayList<GetSignUpUserResponse> getAllUserList = new ArrayList<>();
    GetSignUpUserViewModel sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.init(this);
        sign = new ViewModelProvider(this).get(GetSignUpUserViewModel.class);
        sign.init(this);

        sign.getAllSignUPData();
        initClicks();
        attachObservers();
        checkUserSession();

    }
    private void checkUserSession() {

        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        if (retailerId != null && !retailerId.isEmpty()) {
            navigateToDashboard();
        }
    }

    private void attachObservers() {
        sign.getLiveData().observe(this, responses -> {
            if (responses != null) {
                getAllUserList.clear();
                getAllUserList.addAll(responses);


            } else {
                Log.e("LoginActivity", "Sign-up response is null or empty");
            }
        });

        loginViewModel.getLiveData().observe(this, loginResponses -> {
            if (loginResponses != null) {
                navigateToDashboard();
            }
        });
    }
    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
        Log.d("LoginActivity", "NetworkCheckService started");
    }


    private void initClicks() {
        binding.txtForgetpass.setPaintFlags(binding.txtForgetpass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        binding.btnLogin.setOnClickListener(v -> {
            String enteredMobile = binding.edtMobile.getText().toString().trim();
            String enteredPassword = binding.edtPassword.getText().toString().trim();

            if (enteredMobile.isEmpty()) {
                Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show();
            } else if (enteredPassword.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            } else {
                validateCredentials(enteredMobile, enteredPassword);
            }
        });

        binding.txtForgetpass.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
            startActivity(intent);
        });

        binding.btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });

        binding.imgEye.setOnClickListener(v -> togglePasswordVisibility());
    }
    private void validateCredentials(String mobile, String password) {
        boolean isValidUser = false;

        for (GetSignUpUserResponse user : getAllUserList) {
            if (mobile.equals(user.getRetailerPhone()) && password.equals(user.getRetailerPassword())) {
                isValidUser = true;

                AppSession appSession = AppSession.getInstance(this);
                appSession.setValue(Constants.RELAILER_ID, user.getRetailerId());
                appSession.setValue(Constants.RELAILER_NAME, user.getRetailerName());
                appSession.setValue(Constants.RELAILER_PASSWORD, user.getRetailerPassword());
                appSession.setValue(Constants.RELAILER_PHONE, user.getRetailerPhone());
                appSession.setValue(Constants.CITY_NAME, user.getCity());
                appSession.setValue(Constants.STATE_NAME, user.getStateName());
                appSession.setValue(Constants.STATE_ID, user.getStateId());
                appSession.setValue(Constants.CITY_ID, user.getCityId());

                Log.d("log", "validateCredentials: "+ user.getCity());
                Log.d("log", "validateCredentials: "+ user.getStateName());

                break;
            }
        }

        if (isValidUser) {
            loginViewModel.getLoginResData(mobile, password);
        } else {
            Toast.makeText(this, "Incorrect mobile number or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            binding.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.imgEye.setImageResource(R.drawable.eye);
        } else {
            binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.imgEye.setImageResource(R.drawable.hide_eye);
        }
    }
    private void navigateToDashboard() {
        Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}