package com.indosoft.medibridge.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.indosoft.medibridge.Body.AddressUpdateBody;
import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.AddressUpdateViewModel;
import com.indosoft.medibridge.ViewModel.GetSignUpUserViewModel;
import com.indosoft.medibridge.databinding.ActivityUpdateAddressBinding;

public class UpdateAddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityUpdateAddressBinding binding;
    private AddressUpdateViewModel addressUpdateViewModel;
    private GetSignUpUserViewModel signUpUserViewModel;

    private MapView mapView;
    private GoogleMap googleMap;
    private boolean isMapReady = false;

    private FusedLocationProviderClient fusedLocationsClient;
    private LocationCallback locationCallback;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showCurrentLocationOnMap();
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Init viewModels
        addressUpdateViewModel = new ViewModelProvider(this).get(AddressUpdateViewModel.class);
        addressUpdateViewModel.init(this);
        signUpUserViewModel = new ViewModelProvider(this).get(GetSignUpUserViewModel.class);
        signUpUserViewModel.init(this);
        signUpUserViewModel.getAllSignUPData();

        fusedLocationsClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize map
        mapView = binding.mapView;
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        initClicks();
        onAttachObservers();
        startNetworkService();

        showCurrentLocationOnMap();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.btnSubmit.setOnClickListener(v -> {
            String address = binding.edtPermanent.getText().toString();
            if (address.isEmpty()) {
                Toast.makeText(this, "Enter permanent address", Toast.LENGTH_SHORT).show();
            } else {
                AddressUpdateBody body = new AddressUpdateBody();
                body.setRetailerAddress(address);
                String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
                addressUpdateViewModel.getUpdateAddress(retailerId, body);
                AppSession.getInstance(this).setValue(Constants.PERMANENTADDRESS, address);
            }
        });

        binding.imgMap.setOnClickListener(v -> {
            if (!isMapReady) {
                Toast.makeText(this, "Map is still loading...", Toast.LENGTH_SHORT).show();
                return;
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                showCurrentLocationOnMap();
            }
        });

        binding.mapView.setOnClickListener(v -> showCurrentLocationOnMap());

        SpannableString spannable = new SpannableString("Add Address");
        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 4, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.txtAddress.setText(spannable);
    }

    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);

        addressUpdateViewModel.getLiveData().observe(this, response -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (response != null) {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        signUpUserViewModel.getLiveData().observe(this, responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null) {
                String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
                for (GetSignUpUserResponse response : responses) {
                    if (response != null && retailerId.equals(response.getRetailerId())) {
                        binding.txtState.setText(response.getStateName() != null ? response.getStateName() : "State not available");
                        binding.txtCity.setText(response.getCity() != null ? response.getCity() : "City not available");


                        binding.edtPermanent.setText(response.getRetailerAddress() != null ? (CharSequence) response.getRetailerAddress() : "Address not available");
                    }
                }
            }
        });
    }

    private void startNetworkService() {
        startService(new Intent(this, NetworkCheckService.class));
    }

    private void showCurrentLocationOnMap() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationResult.getLastLocation() == null) {
                    Toast.makeText(UpdateAddressActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                    return;
                }

                double lat = locationResult.getLastLocation().getLatitude();
                double lng = locationResult.getLastLocation().getLongitude();
                LatLng latLng = new LatLng(lat, lng);

                if (googleMap != null) {
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here"));
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));
                }

                fusedLocationsClient.removeLocationUpdates(locationCallback);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationsClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }


    // Google Map ready callback
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        isMapReady = true;

        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }


    // MapView Lifecycle
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
