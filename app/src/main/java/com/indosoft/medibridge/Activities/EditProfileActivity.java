package com.indosoft.medibridge.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding binding;
    UserUpdateViewModel model;
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
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );
    }
    private void loadSessionData() {
        binding.edtShopName.setText(AppSession.getInstance(this).getValue(Constants.RELAILER_NAME));
        binding.txtPhoneNumber.setText(AppSession.getInstance(this).getValue(Constants.RELAILER_PHONE));
        binding.edtEmail.setText(AppSession.getInstance(this).getValue(Constants.Email));
        binding.edtDlNumber.setText(AppSession.getInstance(this).getValue(Constants.RETAILER_DL));
        binding.edtGstNumber.setText(AppSession.getInstance(this).getValue(Constants.RETAILER_GST));
        binding.edtContactPerson.setText(AppSession.getInstance(this).getValue(Constants.CONTACT_PERSON));
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale > 1.0f) {
            newConfig.fontScale = 1.0f;
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
        super.onConfigurationChanged(newConfig);
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

                    }
                }
            }
        });
        signUpViewModel.getLiveData().observe(this, signUpResponse -> {
            if (signUpResponse != null) {
               // Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();
                selectedImageFile = null; // Optional: Reset after upload
            }
        });



    }

    private void initClicks() {
        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.btnSubmit.setOnClickListener(v -> {
            // Collect inputs
            String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
            String dlNumber = binding.edtDlNumber.getText().toString().trim();
            String gstNumber = binding.edtGstNumber.getText().toString().trim();

            // ✅ DL Regex (Drug License Format: MH-1234-20-123456)
          //  String dlRegex = "^[A-Z]{2}-\\d{2,4}-(20|21)-\\d{5,7}$";

            // ✅ GST Regex (15 characters format: 22AAAAA0000A1Z5)
            String gstRegex = "\\d{2}[A-Z]{5}\\d{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}";

            // Validation
//            if (!dlNumber.matches(dlRegex)) {


//                Toast.makeText(this, "DL Number not correct! Format: MH-1234-21-123456", Toast.LENGTH_LONG).show();
//                return;
//            }
//            if (!gstNumber.matches(gstRegex)) {
//                Toast.makeText(this, "GST Number not correct! Format: 22AAAAA0000A1Z5", Toast.LENGTH_LONG).show();
//                return;
//            }


            // ✅ If both are valid then submit
            UserUpdateBody body = new UserUpdateBody();
            body.setRetailerName(binding.edtShopName.getText().toString());
            body.setRetailerContactName(binding.edtContactPerson.getText().toString());
            body.setRetailerPassword(AppSession.getInstance(this).getValue(Constants.RELAILER_PASSWORD));
            body.setRetailerEmail(binding.edtEmail.getText().toString());
            body.setRetailerPhone(binding.txtPhoneNumber.getText().toString());
            body.setStateId(AppSession.getInstance(this).getValue(Constants.STATE_ID));
            body.setStateName(AppSession.getInstance(this).getValue(Constants.STATE_NAME));
            body.setCityId(AppSession.getInstance(this).getValue(Constants.CITY_ID));
            body.setCity(AppSession.getInstance(this).getValue(Constants.CITY_NAME));
            body.setRetailerDlNo(dlNumber);
            body.setRetailerGst(gstNumber);
            body.setPhotoPath("uploads/image" + retailerId + ".png");

            model.getUserUpdateData(retailerId, body);

            if (selectedImageFile != null) {
                signUpViewModel.uploadImage(selectedImageFile);
            }
        });


        binding.btnImage.setOnClickListener(v -> showImagePickerDialog());

        // Heading color
        TextView title = binding.txtProfile;
        SpannableString spannable = new SpannableString("Profile");
        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 3 , spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);


        binding.edtDlNumber.setHint("Ex: MH-1234-20-123456");
        binding.edtDlNumber.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                (source, start, end, dest, dstart, dend) -> {
                    for (int i = start; i < end; i++) {
                        char c = source.charAt(i);
                        if (!Character.isLetterOrDigit(c) && c != '-') {
                            return ""; // block invalid chars
                        }
                    }
                    return null;
                }
        });

        binding.edtDlNumber.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        binding.edtGstNumber.setHint("Ex: 22AAAAA0000A1Z5");
        binding.edtGstNumber.setFilters(new InputFilter[]{
                new InputFilter.AllCaps(),
                new InputFilter.LengthFilter(15) // GST always 15 chars
        });

        binding.edtGstNumber.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        // Inside initClicks() after setting hints
        binding.edtDlNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Example DL format: DL-XX-1234567
                int length = s.length();

                if (length <= 5) {
                    // "DL-XX" => letters allowed
                    binding.edtDlNumber.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                } else {
                    // After 6th char => digits allowed
                    binding.edtDlNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                }

                // cursor ko last me le jane ke liye
                binding.edtDlNumber.setSelection(binding.edtDlNumber.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

// GST Number (Format: 22AAAAA0000A1Z5)
        binding.edtGstNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();

                if (length < 2) {
                    // First 2 chars => digits (State code)
                    binding.edtGstNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if (length < 7) {
                    // Next 5 chars => letters
                    binding.edtGstNumber.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                } else if (length < 11) {
                    // Next 4 chars => digits
                    binding.edtGstNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if (length == 11) {
                    // Next 1 char => letter
                    binding.edtGstNumber.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                } else if (length == 12) {
                    // Next 1 => digit or letter
                    binding.edtGstNumber.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                } else if (length == 13) {
                    // Must be 'Z'
                    binding.edtGstNumber.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                } else {
                    // Last 1 => alphanumeric
                    binding.edtGstNumber.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                }

                // cursor ko last me le jane ke liye
                binding.edtGstNumber.setSelection(binding.edtGstNumber.getText().length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
        }, 1000);
    }

    private void showImagePickerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_select_image);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        dialog.findViewById(R.id.btnCamera).setOnClickListener(v -> {
            dialog.dismiss();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });

        dialog.findViewById(R.id.btnGallery).setOnClickListener(v -> {
            dialog.dismiss();
            openGallery();
        });

        dialog.show();
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

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
            if (requestCode == GALLERY_REQUEST_CODE && data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                selectedImageFile = getFileFromUri(this, selectedImageUri);
                Glide.with(this).load(selectedImageUri).into(binding.getImage); // Use your correct ImageView ID
            } else if (requestCode == CAMERA_REQUEST_CODE && data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                selectedImageFile = bitmapToFile(photo, "image_" + System.currentTimeMillis());
                Glide.with(this).load(selectedImageFile).into(binding.getImage); // Use your correct ImageView ID
            }
        }
    }
    private File bitmapToFile(Bitmap bitmap, String fileName) {
        File file = new File(getCacheDir(), fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static File getFileFromUri(Context context, Uri uri) {
        File file = null;
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            String fileName = "image_" + System.currentTimeMillis() + ".png";
            File tempFile = File.createTempFile("upload_", fileName, context.getCacheDir());
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                file = tempFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    private Uri getImageUriFromBitmap(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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