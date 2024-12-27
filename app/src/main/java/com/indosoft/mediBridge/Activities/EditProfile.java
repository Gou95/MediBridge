package com.indosoft.mediBridge.Activities;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.databinding.ActivityEditProfileBinding;

public class EditProfile extends AppCompatActivity {

    ActivityEditProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initClicks();


    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

}