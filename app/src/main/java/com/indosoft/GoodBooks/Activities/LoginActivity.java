package com.indosoft.GoodBooks.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

import com.indosoft.GoodBooks.R;
import com.indosoft.GoodBooks.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.txtForgetpass.setPaintFlags(binding.txtForgetpass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        initclicks();

    }

    private void initclicks() {

        binding.btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
            startActivity(intent);

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

                binding.imgEye.setImageResource(R.drawable.eye); // Make sure to have this drawable
            } else {

                binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.imgEye.setImageResource(R.drawable.hide_eye); // Make sure to have this drawable
            }
        });
    }

}