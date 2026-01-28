package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.Model.LoginResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.GetSignUpUserViewModel;
import com.indosoft.medibridge.ViewModel.LoginViewModel;
import com.indosoft.medibridge.databinding.ActivityLoginBinding;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    boolean isPasswordVisible = false;
    LoginViewModel loginViewModel;
    GetSignUpUserViewModel signUpViewModel;
    ArrayList<GetSignUpUserResponse> getAllUserList = new ArrayList<>();
    private boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.init(this);

        signUpViewModel = new ViewModelProvider(this).get(GetSignUpUserViewModel.class);
        signUpViewModel.init(this);
        signUpViewModel.getAllSignUPData();

        initClicks();
        observeSignUpData();
        startNetworkService();
        checkSubscriptionStatusOnLaunch();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );
    }

    private void checkSubscriptionStatusOnLaunch() {
        AppSession session = AppSession.getInstance(this);
        String isFirstLogin = session.getValue(Constants.IS_FIRST_LOGIN);
        String status = session.getValue(Constants.RETAILER_STATUS);

        if ("true".equals(isFirstLogin)) {
            if ("Active".equalsIgnoreCase(status)) {
                navigateToDashboard();
            } else {
                showSubscriptionExpiredDialog();
            }
        }
    }

    private void observeSignUpData() {
        signUpViewModel.getLiveData().observe(this, responses -> {
            if (responses != null) {
                getAllUserList.clear();
                getAllUserList.addAll(responses);
            } else {
                Log.e("LoginActivity", "Sign-up response is null");
            }
        });
    }

    private void initClicks() {
        binding.txtForgetpass.setPaintFlags(binding.txtForgetpass.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        binding.btnLogin.setOnClickListener(v -> {
            String mobile = binding.edtMobile.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (mobile.isEmpty()) {
                Toast.makeText(this, "Enter mobile number", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
            } else {
                validateCredentials(mobile, password);


            }
        });

        binding.txtForgetpass.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgetPasswordActivity.class));
        });

        binding.btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
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

    private void validateCredentials(String mobile, String password) {
        if (!isNetworkConnected()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }
        loginViewModel.getLoginResData(mobile, password);
        loginViewModel.getLiveData().observe(this, loginResponse -> {
            loginViewModel.getLiveData().removeObservers(this);
            if (loginResponse != null) {
                String msg = loginResponse.getMessage();

                switch (msg) {
                    case "Login successful":
                          saveSessionData(loginResponse, password);
                        break;

                    case "Incorrect password":
                        Toast.makeText(this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        break;

                    case "Incorrect mobile number":
                        Toast.makeText(this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                Toast.makeText(this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void saveSessionData(LoginResponse response, String password) {
        AppSession session = AppSession.getInstance(this);
        String retailerId = String.valueOf(response.getData().getRetailerId());

        session.setValue(Constants.RELAILER_ID, retailerId);
        session.setValue(Constants.RELAILER_NAME, response.getData().getRetailerName());
        session.setValue(Constants.RELAILER_PHONE, response.getData().getRetailerPhone());
        session.setValue(Constants.RELAILER_PASSWORD, password);
        session.setValue(Constants.RETAILER_STATUS, response.getData().getStatus());
        session.setValue(Constants.EXPIRY_DATE, response.getData().getSubsExpiryDate());
        

        for (GetSignUpUserResponse user : getAllUserList) {
            if (user.getRetailerId().equals(retailerId)) {
                session.setValue(Constants.Email, user.getRetailerEmail());
                session.setValue(Constants.STATE_ID, user.getStateId());
                session.setValue(Constants.CITY_ID, user.getCityId());
                break;
            }
        }

        session.setValue(Constants.IS_FIRST_LOGIN, "true");

        if ("Active".equalsIgnoreCase(response.getData().getStatus())) {
            navigateToDashboard();
        } else {
            showSubscriptionExpiredDialog();
        }
    }

    private void showSubscriptionExpiredDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View popupView = inflater.inflate(R.layout.popup_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setView(popupView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView title = popupView.findViewById(R.id.popup_title);
        TextView message = popupView.findViewById(R.id.popup_message);
        TextView confirm = popupView.findViewById(R.id.popup_confirm);
        TextView cancel = popupView.findViewById(R.id.popup_cancel);
        TextView productName = popupView.findViewById(R.id.popup_product_name);

        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        title.setText("Retailer Information");
      //  productName.setText("Retailer ID: " + retailerId);
        message.setText("Your plan has expired. Please renew to continue.");

        confirm.setText("Renew Now");
        confirm.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(this, "Redirecting to Payment...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SubscribeActivity.class));
        });

        cancel.setOnClickListener(v -> {
            dialog.dismiss();

        });
    }

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
            }
        }
        return false;
    }

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
                reloadData();
            }
        }
    };

    private void reloadData() {
        new Handler().postDelayed(() -> {
            // You can refresh some data here if needed
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            isReceiverRegistered = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(networkReceiver);
            isReceiverRegistered = false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale > 1.0f) {
            newConfig.fontScale = 1.0f;
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
        super.onConfigurationChanged(newConfig);
    }
}
