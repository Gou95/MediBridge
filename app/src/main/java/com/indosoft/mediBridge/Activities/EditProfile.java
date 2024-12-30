package com.indosoft.mediBridge.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.mediBridge.Body.UserUpdateBody;
import com.indosoft.mediBridge.Model.GetSignUpUserResponse;
import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.Session.AppSession;
import com.indosoft.mediBridge.Session.Constants;
import com.indosoft.mediBridge.ViewModel.GetSignUpUserViewModel;
import com.indosoft.mediBridge.ViewModel.UserUpdateViewModel;
import com.indosoft.mediBridge.databinding.ActivityEditProfileBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    ActivityEditProfileBinding binding;
    UserUpdateViewModel model;

    ArrayList<GetSignUpUserResponse> getAllUserList = new ArrayList<>();
    GetSignUpUserViewModel sign;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = new ViewModelProvider(this).get(UserUpdateViewModel.class);
        model.init(this);
        sign = new ViewModelProvider(this).get(GetSignUpUserViewModel.class);
        sign.init(this);

        initClicks();

        onAttachObservers();

        String shopName = AppSession.getInstance(this).getString(Constants.RELAILER_NAME);
        String email = AppSession.getInstance(this).getString(Constants.Email);
        String phoneNumber = AppSession.getInstance(this).getString(Constants.RELAILER_PHONE);
        String dlNumber = AppSession.getInstance(this).getString(Constants.RETAILER_DL);
        String gstNumber = AppSession.getInstance(this).getString(Constants.RETAILER_GST);

        binding.txtShopName.setText(shopName);
        binding.txtPhoneNumber.setText(phoneNumber);
        binding.edtEmail.setText(email);
        binding.edtDlNumber.setText(dlNumber);
        binding.edtGstNumber.setText(gstNumber);
    }

    private void onAttachObservers() {
        sign.getLiveData().observe(this, responses -> {
            if (responses != null) {
                getAllUserList.clear();
                getAllUserList.addAll(responses);

                for (GetSignUpUserResponse response : responses) {
                    // Use HashMap to store user data
                    HashMap<String, String> userData = new HashMap<>();
                    userData.put(Constants.Email, response.getRetailerEmail());
                    userData.put(Constants.RETAILER_GST, response.getRetailerGst());
                    userData.put(Constants.RETAILER_DL, response.getRetailerDlNo());
                   // userData.put(Constants.RELAILER_NAME, response.getRetailerName());

                    // Store all data in AppSession
                    for (HashMap.Entry<String, String> entry : userData.entrySet()) {
                        AppSession.getInstance(this).putString(entry.getKey(), entry.getValue());
                    }

                    // Log data for debugging
                    Log.d("AppSessionData", "email: " + response.getRetailerEmail());
                    Log.d("AppSessionData", "gst: " + response.getRetailerGst());
                    Log.d("AppSessionData", "dl: " + response.getRetailerDlNo());
//                    Log.d("AppSessionData", "Password: " + response.getRetailerPassword());
                }
            } else {
                Log.e("LoginActivity", "Sign-up response is null or empty");
            }
        });

        model.getLiveData().observe(this,userUpdateResponse -> {
            if (userUpdateResponse !=null){
                Toast.makeText(this, userUpdateResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnSubmit.setOnClickListener(v -> {
            String contactPerson = binding.edtContactPerson.getText().toString();
            String email = binding.edtEmail.getText().toString();
            String dlno = binding.edtDlNumber.getText().toString();
            String gstno = binding.edtGstNumber.getText().toString();

            if (contactPerson.isEmpty() || email.isEmpty() || dlno.isEmpty() || gstno.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            UserUpdateBody body = new UserUpdateBody();
            body.setRetailerName(AppSession.getInstance(this).getString(Constants.RELAILER_NAME));
            body.setRetailerPhone(AppSession.getInstance(this).getString(Constants.RELAILER_PHONE));
            body.setRetailerContactName(contactPerson);
            body.setRetailerEmail(email);
            body.setRetailerDlNo(dlno);
            body.setRetailerGst(gstno);
            body.setRetailerId(AppSession.getInstance(this).getString(Constants.RELAILER_ID));
            body.setCityId("2");
            body.setStateId("13");
            body.setRetailerAddress(""); // Update as required
            body.setStatus("1");
            body.setFirstorderplaced("0");
            body.setAddtime("2024-12-25 14:07:16");
            body.setEdittime("");

            // Log for debugging
            Log.d("UserUpdateRequest", "Body: " + body.toString());

            // Call ViewModel to update user data
            model.getUserUpdateData(body);
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

}