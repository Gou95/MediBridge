package com.indosoft.medibridge.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.indosoft.medibridge.Body.AddressUpdateBody;
import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.R;
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
    private boolean isReceiverRegistered = false;
    private FusedLocationProviderClient fusedLocationsClient;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {

                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityUpdateAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        addressUpdateViewModel = new ViewModelProvider(this).get(AddressUpdateViewModel.class);
        addressUpdateViewModel.init(this);
        fusedLocationsClient = LocationServices.getFusedLocationProviderClient(this);
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
        binding.imgMap.setOnClickListener(v -> {
            checkLocationPermission();
        });
        TextView title = binding.txtAddress;
        SpannableString spannable = new SpannableString("Add Address");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 4, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);
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
                        binding.edtPermanent.setText
                                 (response.getRetailerAddress() != null ? (CharSequence) response.getRetailerAddress() : "Address not available");
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
            } else {

            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (!isReceiverRegistered) {
            registerReceiver(networkReceiver, filter);
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
    private void reloadData() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 5000);
    }

    private void checkLocationPermission() {
        if (!isLocationEnabled()){
            showLocationEnableDialog();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {

            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationsClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Show location in Toast or use it
                Toast.makeText(this, "Lat: " + latitude + ", Lng: " + longitude, Toast.LENGTH_SHORT).show();

                // Open Google Maps with the location
                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Error getting location: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    private void showLocationEnableDialog() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popup_layout,null);

        TextView title = view.findViewById(R.id.popup_title);
        TextView massege = view.findViewById(R.id.popup_message);
        TextView confirm = view.findViewById(R.id.popup_confirm);
        TextView cancel = view.findViewById(R.id.popup_cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        title.setText("Enable Location");
        massege.setText("Your Location is turned off .Please enabale it to continue");
        cancel.setOnClickListener(v -> dialog.dismiss());

        // Turn On Location Button Click
        confirm.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            dialog.dismiss();
        });
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