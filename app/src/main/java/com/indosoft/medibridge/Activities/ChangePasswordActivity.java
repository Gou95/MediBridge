package com.indosoft.medibridge.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.databinding.ActivityChangePasswordBinding;

public class ChangePasswordActivity extends AppCompatActivity {
    ActivityChangePasswordBinding binding;
    SignUpViewModel viewModel;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        viewModel.init(this);

        onAttachObservers();
        initClick();

    }

    private void initClick() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.btnChangePassword.setOnClickListener(v -> {

            String newPassword = binding.edtPassword.getText().toString().trim();

            if (newPassword.isEmpty()) {
                Toast.makeText(this, "Enter new password", Toast.LENGTH_SHORT).show();
            } else if (newPassword.length() < 4) {
                Toast.makeText(this, "Password must be at least 4 characters", Toast.LENGTH_SHORT).show();
            } else {
                String retailerId = AppSession.getInstance(this)
                        .getValue(Constants.RELAILER_ID);
                viewModel.resetPassword(retailerId, newPassword);
            }
        });
        binding.imgEye.setOnClickListener(v -> togglePasswordVisibility());
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

    private void onAttachObservers() {
        viewModel.getLiveData().observe(this, signUpResponse -> {
            if (signUpResponse != null) {
               // Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ChangePasswordActivity.this, DashBoardActivity.class);
                    intent.putExtra("OPEN_PROFILE_FRAGMENT", true);
                    startActivity(intent);
                    finish();

            }
        });
    }

}