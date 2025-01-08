package com.indosoft.MediBridges.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.MediBridges.Body.UserUpdateBody;
import com.indosoft.MediBridges.Model.GetSignUpUserResponse;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.ViewModel.GetSignUpUserViewModel;
import com.indosoft.MediBridges.ViewModel.UserUpdateViewModel;
import com.indosoft.MediBridges.databinding.ActivityEditProfileBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class

EditProfile extends AppCompatActivity {

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
        loadSessionData();

    }

    private void loadSessionData() {

        String shopName = AppSession.getInstance(this).getValue(Constants.RELAILER_NAME);
        String email = AppSession.getInstance(this).getValue(Constants.Email);
        String phoneNumber = AppSession.getInstance(this).getValue(Constants.RELAILER_PHONE);
        String dlNumber = AppSession.getInstance(this).getValue(Constants.RETAILER_DL);
        String gstNumber = AppSession.getInstance(this).getValue(Constants.RETAILER_GST);
        String contactPerson = AppSession.getInstance(this).getValue(Constants.CONTACT_PERSON);


        binding.edtShopName.setText(shopName);
        binding.txtPhoneNumber.setText(phoneNumber);
        binding.edtEmail.setText(email);
        binding.edtDlNumber.setText(dlNumber);
        binding.edtGstNumber.setText(gstNumber);
        binding.edtContactPerson.setText(contactPerson);
    }

    private void onAttachObservers() {
        // Observing the response after getting user data from the server
        sign.getLiveData().observe(this, responses -> {
            if (responses != null) {
                getAllUserList.clear();
                getAllUserList.addAll(responses);


                for (GetSignUpUserResponse response : responses) {
                    AppSession.getInstance(this).setValue(Constants.Email, response.getRetailerEmail());
                    AppSession.getInstance(this).setValue(Constants.RELAILER_NAME, response.getRetailerName());
                    AppSession.getInstance(this).setValue(Constants.RELAILER_PHONE, response.getRetailerPhone());

                }
            } else {
                Log.e("LoginActivity", "Sign-up response is null or empty");
            }
        });

        // Observing the response after updating the user data
        model.getLiveData().observe(this, userUpdateResponse -> {
            if (userUpdateResponse != null) {
                Toast.makeText(this, userUpdateResponse.getMessage(), Toast.LENGTH_SHORT).show();

                // Update session data after the profile update
                String name = binding.edtShopName.getText().toString();
                String email = binding.edtEmail.getText().toString();
                String dlno = binding.edtDlNumber.getText().toString();
                String gstno = binding.edtGstNumber.getText().toString();
                String contactPerson = binding.edtContactPerson.getText().toString();

                // Save the updated values to the session
                AppSession.getInstance(this).setValue(Constants.RELAILER_NAME, name);
                AppSession.getInstance(this).setValue(Constants.Email, email);
                AppSession.getInstance(this).setValue(Constants.RETAILER_DL, dlno);
                AppSession.getInstance(this).setValue(Constants.RETAILER_GST, gstno);
                AppSession.getInstance(this).setValue(Constants.CONTACT_PERSON, contactPerson);
            }
        });
    }

    private void initClicks() {
        // Back button click listener
        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });


        binding.btnSubmit.setOnClickListener(v -> {
            String name = binding.edtShopName.getText().toString();
            String contactPerson = binding.edtContactPerson.getText().toString();
            String email = binding.edtEmail.getText().toString();
            String dlno = binding.edtDlNumber.getText().toString();
            String gstno = binding.edtGstNumber.getText().toString();


            UserUpdateBody body = new UserUpdateBody();
            body.setRetailerName(name);
            body.setRetailerPhone(AppSession.getInstance(this).getValue(Constants.RELAILER_PHONE));
            body.setRetailerContactName(contactPerson);
            body.setRetailerEmail(email);
            body.setRetailerDlNo(dlno);
            body.setRetailerGst(gstno);
            body.setRetailerId(AppSession.getInstance(this).getValue(Constants.RELAILER_ID));
            body.setCityId("2");
            body.setStateId("13");
            body.setRetailerAddress("");  // Update as required
            body.setStatus("1");
            body.setFirstorderplaced("0");
            body.setAddtime("2024-12-25 14:07:16");
            body.setEdittime("");

            model.getUserUpdateData(body);
        });
    }
}


