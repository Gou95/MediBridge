package com.indosoft.medibridge.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.indosoft.medibridge.R;
import com.indosoft.medibridge.databinding.ActivityExpiryListBinding;

public class ExpiryListActivity extends AppCompatActivity {

    ActivityExpiryListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityExpiryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initCliks();

    }

    private void initCliks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());


    }
}