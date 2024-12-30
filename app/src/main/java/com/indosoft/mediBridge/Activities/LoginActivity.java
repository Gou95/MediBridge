package com.indosoft.mediBridge.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.Toast;

import com.indosoft.mediBridge.Model.GetSignUpUserResponse;
import com.indosoft.mediBridge.Model.LoginResponse;
import com.indosoft.mediBridge.Model.MedicineListResponse;
import com.indosoft.mediBridge.Model.UnitResponse;
import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.Session.AppSession;
import com.indosoft.mediBridge.Session.Constants;
import com.indosoft.mediBridge.ViewModel.GetSignUpUserViewModel;
import com.indosoft.mediBridge.ViewModel.LoginViewModel;
import com.indosoft.mediBridge.databinding.ActivityLoginBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        String retailerId = AppSession.getInstance(this).getString(Constants.RELAILER_ID);
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
            if (mobile.equals(user.getRetailerPhone()) &&
                    password.equals(user.getRetailerPassword())) {
                isValidUser = true;

                // Save user details in AppSession
                AppSession.getInstance(this).putString(Constants.RELAILER_PHONE, user.getRetailerPhone());
                AppSession.getInstance(this).putString(Constants.RELAILER_PASSWORD, user.getRetailerPassword());
                AppSession.getInstance(this).putString(Constants.RELAILER_ID, user.getRetailerId());
                AppSession.getInstance(this).putString(Constants.RELAILER_NAME, user.getRetailerName());
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