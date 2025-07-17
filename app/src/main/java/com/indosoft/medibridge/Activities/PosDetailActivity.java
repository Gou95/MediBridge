package com.indosoft.medibridge.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.indosoft.medibridge.R;
import com.indosoft.medibridge.databinding.ActivityPosDetailBinding;

public class PosDetailActivity extends AppCompatActivity {
ActivityPosDetailBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityPosDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initClicks();
        onAttachObservers();

    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());

    }

    private void onAttachObservers() {
    }
}