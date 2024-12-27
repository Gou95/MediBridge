package com.indosoft.mediBridge.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.indosoft.mediBridge.databinding.ActivityOtpVerificationBinding;

public class OtpVerificationActivity extends AppCompatActivity {

    ActivityOtpVerificationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initclicks();
    }

    private void initclicks() {

        binding.btnOtpverify.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
            startActivity(intent);
        });
    }

}