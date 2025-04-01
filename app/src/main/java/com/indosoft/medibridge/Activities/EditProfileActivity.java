package com.indosoft.medibridge.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.indosoft.medibridge.Body.UserUpdateBody;
import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.GetSignUpUserViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.UserUpdateViewModel;
import com.indosoft.medibridge.databinding.ActivityEditProfileBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    UserUpdateViewModel model;
    ArrayList<GetSignUpUserResponse> getAllUserList = new ArrayList<>();
    GetSignUpUserViewModel sign;
    SignUpViewModel signUpViewModel;
    private boolean isReceiverRegistered = false;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private File selectedImageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = new ViewModelProvider(this).get(UserUpdateViewModel.class);
        model.init(this);
        sign = new ViewModelProvider(this).get(GetSignUpUserViewModel.class);
        sign.init(this);
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        signUpViewModel.init(this);

        sign.getAllSignUPData();
        initClicks();

        onAttachObservers();

        loadSessionData();
        startNetworkService();
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        binding.swipeRefreshLayout.setRefreshing(false);
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
//                        String baseUrl = "http://103.212.120.177/mediapp/v2/api/users.php";
//                        if (response.getRetailerId().equals("3")) {
//
//                            String profileImagePath = response.getPhotoPath();
//
//                            if (profileImagePath != null && !profileImagePath.isEmpty()) {
//                                String fullImageUrl = baseUrl + profileImagePath;
//
//                                Glide.with(this)
//                                        .load(fullImageUrl)
//                                        .into(binding.imgAdd);
//                            }
//                            break;
//                        }
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
            body.setPhotoPath("uploads/image" + retailerId + ".png");


            Log.d("UpdateRequest", "RetailerId: " + retailerId + ", Body: " + body);

            model.getUserUpdateData(retailerId, body);

            if (selectedImageFile == null) {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
                return;
            }

            signUpViewModel.uploadImage(selectedImageFile);

            signUpViewModel.getLiveData().observe(this, signUpResponse -> {
                if (signUpResponse != null) {
                    Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();


//                    if (selectedImageFile != null) {
//                        Glide.with(this).load(selectedImageFile).into(binding.imgAdd);
//                    }
                }
            });

        });
        binding.btnImage.setOnClickListener(v -> {
            showImagePickerDialog();
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

    private void showImagePickerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_image);
        dialog.setCancelable(true);

        LinearLayout btnCamera = dialog.findViewById(R.id.btnCamera);
        LinearLayout btnGallery = dialog.findViewById(R.id.btnGallery);

        btnCamera.setOnClickListener(v -> {
            dialog.dismiss();
            checkCameraPermission();
        });

        btnGallery.setOnClickListener(v -> {
            dialog.dismiss();
            openGallery();
        });

        dialog.show();
    }
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    // Open Camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    // Open Gallery
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    // Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Handle image selection result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            Uri imageUri = null;

            if (requestCode == CAMERA_REQUEST_CODE && data != null && data.getExtras() != null) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == GALLERY_REQUEST_CODE && data != null) {
                imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                binding.getImage.setImageBitmap(bitmap);
                selectedImageFile = convertBitmapToFile(bitmap);  // Convert to file
                String base64Image = getBase64FromBitmap(bitmap); // Convert to Base64 (if needed)

                if (selectedImageFile == null) {
                    Toast.makeText(this, "Failed to save image file", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("ImageSelection", "File Path: " + selectedImageFile.getAbsolutePath());
                    Log.d("ImageSelection", "Base64 String: " + base64Image);
                }
            } else {
                Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getBase64FromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
    private File convertBitmapToFile(Bitmap bitmap) {

        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        if (retailerId == null || retailerId.isEmpty()) {
            retailerId = "default";
        }

        File filesDir = getApplicationContext().getCacheDir();
        File imageFile = new File(filesDir, "image" + retailerId + ".png");

        try (FileOutputStream fos = new FileOutputStream(imageFile);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
            byte[] imageBytes = baos.toByteArray();
            fos.write(imageBytes);
            return imageFile;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}