package com.indosoft.mediBridge.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.indosoft.mediBridge.databinding.ActivityForgetPasswordBinding;

public class ForgetPasswordActivity extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}