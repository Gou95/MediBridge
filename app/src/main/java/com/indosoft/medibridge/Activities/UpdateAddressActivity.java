package com.indosoft.medibridge.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.indosoft.medibridge.Body.AddressUpdateBody;
import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.AddressUpdateViewModel;
import com.indosoft.medibridge.ViewModel.GetSignUpUserViewModel;
import com.indosoft.medibridge.databinding.ActivityUpdateAddressBinding;

public class UpdateAddressActivity extends AppCompatActivity {

    ActivityUpdateAddressBinding binding;
    AddressUpdateViewModel addressUpdateViewModel;

    GetSignUpUserViewModel signUpUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityUpdateAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        addressUpdateViewModel = new ViewModelProvider(this).get(AddressUpdateViewModel.class);
        addressUpdateViewModel.init(this);

        signUpUserViewModel = new ViewModelProvider(this).get(GetSignUpUserViewModel.class);
        signUpUserViewModel.init(this);
        signUpUserViewModel.getAllSignUPData();
        initClicks();
        onAttachObservers();
        startNetworkService();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);

    }
    private void initClicks() {

        binding.imgBack.setOnClickListener(v -> onBackPressed());
        binding.btnSubmit.setOnClickListener(v -> {
            String address = binding.edtPermanent.getText().toString();

            if (address.isEmpty()){
                Toast.makeText(this, "enter permanent address", Toast.LENGTH_SHORT).show();
            }
            else {
                AddressUpdateBody body = new AddressUpdateBody();
                body.setRetailerAddress(address);
                String retailerId = AppSession.getInstance(UpdateAddressActivity.this).getValue(Constants.RELAILER_ID);
                Toast.makeText(this, retailerId, Toast.LENGTH_SHORT).show();
                addressUpdateViewModel.getUpdateAddress(retailerId,body);

                AppSession.getInstance(UpdateAddressActivity.this).setValue(Constants.PERMANENTADDRESS,address);

            }
        });
    }
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        addressUpdateViewModel.getLiveData().observe(this,response -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (response!=null){
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        signUpUserViewModel.getLiveData().observe(this,getSignUpUserResponses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (getSignUpUserResponses !=null){
                String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
                for (GetSignUpUserResponse response : getSignUpUserResponses) {
                    if (response != null && response.getRetailerId() != null && response.getRetailerId().equals(retailerId)) {
                        binding.txtState.setText(response.getStateName() != null ? response.getStateName() : "State not available");
                        binding.txtCity.setText(response.getCity() != null ? response.getCity() : "City not available");
                        binding.edtPermanent.setText(response.getRetailerAddress() != null ? response.getRetailerAddress() : "Address not available");
                    }
                }
            }
        });
    }

    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
        Log.d("LoginActivity", "NetworkCheckService started");
    }

}