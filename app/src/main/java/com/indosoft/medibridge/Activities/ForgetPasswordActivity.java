package com.indosoft.medibridge.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.GetOtpViewModel;
import com.indosoft.medibridge.ViewModel.OtpViewModel;
import com.indosoft.medibridge.databinding.ActivityForgetPasswordBinding;

public class ForgetPasswordActivity extends AppCompatActivity {
    ActivityForgetPasswordBinding binding;
        OtpViewModel otpViewModel;
    GetOtpViewModel getOtpViewModel;
    //private FirebaseAuth mAuth;
    private String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
                otpViewModel = new ViewModelProvider(this).get(OtpViewModel.class);
        otpViewModel.init(this);
        getOtpViewModel = new ViewModelProvider(this).get(GetOtpViewModel.class);
        getOtpViewModel.init(this);
        initClicks();
        onAttachObservers();
        startNetworkService();

        String otp = AppSession.getInstance(this).getValue(Constants.OTP);
        if (otp != null && !otp.isEmpty()) {
            binding.edtEnterOtp.setText(otp);
        }
    }
    private void onAttachObservers() {

        otpViewModel.getLiveData().observe(this, response -> {
            if (response != null) {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
          });

        getOtpViewModel.getLiveData().observe(this, response -> {
            if (response != null) {
                String otp = response.getMobileOtp(); // Extract OTP from response
                AppSession.getInstance(this).setValue(Constants.OTP, otp);
                binding.edtEnterOtp.setText(otp);
            }
        });
    }

    private void initClicks() {

        binding.linearOtp.setVisibility(View.GONE);
        binding.linearMobile.setVisibility(View.VISIBLE);


        binding.btnSubmit.setOnClickListener(v -> {
            String mobile = binding.edtMobileNumber.getText().toString();

            if (mobile.isEmpty() || mobile.length()<10){
                Toast.makeText(this, "Enter a valid mobile number", Toast.LENGTH_SHORT).show();
            }
            else {

                otpViewModel.getOrderNumData(mobile);
                binding.linearOtp.setVisibility(View.VISIBLE);
                binding.linearMobile.setVisibility(View.GONE);

                getOtpViewModel.getOtpRes(mobile);

            }

        });

        binding.btnOtpVerify.setOnClickListener(v -> {
            String otp = binding.edtEnterOtp.getText().toString().trim();
            if (otp.isEmpty()) {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
        Log.d("LoginActivity", "NetworkCheckService started");
    }
}