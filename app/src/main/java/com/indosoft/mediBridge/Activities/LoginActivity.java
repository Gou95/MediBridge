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

//        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
//        loginViewModel.init(this);
        sign = new ViewModelProvider(this).get(GetSignUpUserViewModel.class);
        sign.init(this);

        sign.getAllSignUPData();
        initclicks();
        onAttachObservers();

    }

    private void onAttachObservers() {

        sign.getLiveData().observe(this, responses -> {
            if (responses != null) {
                getAllUserList.clear();
                getAllUserList.addAll(responses);

                for (GetSignUpUserResponse response : responses) {
                    // Use HashMap to store user data
                    HashMap<String, String> userData = new HashMap<>();
                    userData.put(Constants.RELAILER_PHONE, response.getRetailerPhone());
                    userData.put(Constants.RELAILER_PASSWORD, response.getRetailerPassword());
                    userData.put(Constants.RELAILER_ID, response.getRetailerId());
                    userData.put(Constants.RELAILER_NAME, response.getRetailerName());

                    // Store all data in AppSession
                    for (HashMap.Entry<String, String> entry : userData.entrySet()) {
                        AppSession.getInstance(this).putString(entry.getKey(), entry.getValue());
                    }

                    // Log data for debugging
                    Log.d("AppSessionData", "Phone: " + response.getRetailerPhone());
                    Log.d("AppSessionData", "Name: " + response.getRetailerName());
                    Log.d("AppSessionData", "ID: " + response.getRetailerId());
                    Log.d("AppSessionData", "Password: " + response.getRetailerPassword());
                }
            } else {
                Log.e("LoginActivity", "Sign-up response is null or empty");
            }
        });



    }

    private void initclicks() {
        binding.txtForgetpass.setPaintFlags(binding.txtForgetpass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        binding.btnLogin.setOnClickListener(v -> {

            Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
            startActivity(intent);
//
//            String storedPhone = AppSession.getInstance(this).getString(Constants.RELAILER_PHONE);
//            String storedPassword = AppSession.getInstance(this).getString(Constants.RELAILER_PASSWORD);
//
//            String enteredMobile = binding.edtMobile.getText().toString().trim();
//            String enteredPassword = binding.edtPassword.getText().toString().trim();
//
//            if (enteredMobile.isEmpty()) {
//                Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show();
//            } else if (!enteredMobile.equals(storedPhone)) {
//                Toast.makeText(this, "Incorrect mobile number", Toast.LENGTH_SHORT).show();
//            } else if (enteredPassword.isEmpty()) {
//                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
//            } else if (!enteredPassword.equals(storedPassword)) {
//                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
//            } else {
//
//            }
        });



        binding.txtForgetpass.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
            startActivity(intent);
        });

        binding.btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });

        binding.imgEye.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;

            if (isPasswordVisible) {
                binding.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.imgEye.setImageResource(R.drawable.eye);
            } else {
                binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.imgEye.setImageResource(R.drawable.hide_eye);
            }
        });
    }

}