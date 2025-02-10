package com.indosoft.medibridge.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.Body.UserUpdateBody;
import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.GetSignUpUserViewModel;
import com.indosoft.medibridge.ViewModel.UserUpdateViewModel;
import com.indosoft.medibridge.databinding.ActivityEditProfileBinding;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {

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

        sign.getAllSignUPData();
        initClicks();

        onAttachObservers();
        loadSessionData();
        startNetworkService();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
    }
    private void loadSessionData() {
        binding.edtShopName.setText(AppSession.getInstance(this).getValue(Constants.RELAILER_NAME));
        binding.txtPhoneNumber.setText(AppSession.getInstance(this).getValue(Constants.RELAILER_PHONE));
        binding.edtEmail.setText(AppSession.getInstance(this).getValue(Constants.Email));
        binding.edtDlNumber.setText(AppSession.getInstance(this).getValue(Constants.RETAILER_DL));
        binding.edtGstNumber.setText(AppSession.getInstance(this).getValue(Constants.RETAILER_GST));
        binding.edtContactPerson.setText(AppSession.getInstance(this).getValue(Constants.CONTACT_PERSON));
    }

    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        model.getLiveData().observe(this, userUpdateResponse -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (userUpdateResponse != null) {
                Toast.makeText(this, userUpdateResponse.getMessage(), Toast.LENGTH_SHORT).show();

                AppSession.getInstance(this).setValue(Constants.RELAILER_NAME, binding.edtShopName.getText().toString());
                AppSession.getInstance(this).setValue(Constants.CONTACT_PERSON, binding.edtContactPerson.getText().toString());
                AppSession.getInstance(this).setValue(Constants.Email, binding.edtEmail.getText().toString());
                AppSession.getInstance(this).setValue(Constants.RELAILER_PHONE, binding.txtPhoneNumber.getText().toString());
                AppSession.getInstance(this).setValue(Constants.RETAILER_GST, binding.edtGstNumber.getText().toString());
                AppSession.getInstance(this).setValue(Constants.RETAILER_DL, binding.edtDlNumber.getText().toString());
            }
        });
        sign.getLiveData().observe(this, getSignUpUserResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (getSignUpUserResponses != null) {
                String currentRetailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

                for (GetSignUpUserResponse response : getSignUpUserResponses) {
                    if (response.getRetailerId().equals(currentRetailerId)) {

                        binding.edtShopName.setText(response.getRetailerName());
                        binding.edtContactPerson.setText(response.getRetailerContactName());
                        binding.edtEmail.setText(response.getRetailerEmail());
                        binding.txtPhoneNumber.setText(response.getRetailerPhone());
                        binding.edtGstNumber.setText(response.getRetailerGst());
                        binding.edtDlNumber.setText(response.getRetailerDlNo());
                        break;
                    }
                }
            }
        });


    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.btnSubmit.setOnClickListener(v -> {
            String retailerName = binding.edtShopName.getText().toString();
            String contactName = binding.edtContactPerson.getText().toString();
            String email = binding.edtEmail.getText().toString();
            String phone = binding.txtPhoneNumber.getText().toString();
            String gst = binding.edtGstNumber.getText().toString();
            String dl = binding.edtDlNumber.getText().toString();

            String stateId = AppSession.getInstance(this).getValue(Constants.STATE_ID);
            String stateName = AppSession.getInstance(this).getValue(Constants.STATE_NAME);
            String cityId = AppSession.getInstance(this).getValue(Constants.CITY_ID);
            String city = AppSession.getInstance(this).getValue(Constants.CITY_NAME);

            String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

            UserUpdateBody body = new UserUpdateBody();
            body.setRetailerName(retailerName);
            body.setRetailerContactName(contactName);
            body.setRetailerPassword(AppSession.getInstance(this).getValue(Constants.RELAILER_PASSWORD)); // Replace with dynamic password if required
            body.setRetailerEmail(email);
            body.setRetailerPhone(phone);
            body.setStateId(stateId);
            body.setStateName(stateName);
            body.setCityId(cityId);
            body.setCity(city);
            body.setRetailerDlNo(dl);
            body.setRetailerGst(gst);

            Log.d("UpdateRequest", "RetailerId: " + retailerId + ", Body: " + body);

            model.getUserUpdateData(retailerId, body);
        });

    }
    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
        Log.d("LoginActivity", "NetworkCheckService started");
    }
}