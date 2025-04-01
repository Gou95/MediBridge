package com.indosoft.medibridge.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.indosoft.medibridge.Activities.ChangePasswordActivity;
import com.indosoft.medibridge.Activities.EditProfileActivity;
import com.indosoft.medibridge.Activities.LoginActivity;
import com.indosoft.medibridge.Activities.PrivacyPolicyActivity;
import com.indosoft.medibridge.Activities.SubscribeActivity;
import com.indosoft.medibridge.Activities.TermsActivity;
import com.indosoft.medibridge.Activities.UpdateAddressActivity;
import com.indosoft.medibridge.Model.GetSignUpUserResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.GetSignUpUserViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.databinding.FragmentProfileBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class ProfileFragment extends Fragment {

FragmentProfileBinding binding;
ArrayList<GetSignUpUserResponse> list = new ArrayList<>();
GetSignUpUserViewModel viewModel;
SignUpViewModel sign;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(GetSignUpUserViewModel.class);
        viewModel.init(getContext());
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(getContext());
        viewModel.getAllSignUPData();

        initClicks();
        onAttachObservers();
        startNetworkCheckService();
        handleBackPress();
        loadSubscriptionDetails();

        return binding.getRoot();
    }
    private void loadSubscriptionDetails() {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        boolean hasPlan = AppSession.getInstance(getContext()).getBoolean(Constants.PLAN_SELECTED + "_" + retailerId, false);
        long expiry = AppSession.getInstance(getContext()).getLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, 0);
        String planName = AppSession.getInstance(getContext()).getValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId);

        Log.d("Subscription", "Loaded Plan: " + planName + ", Expiry: " + expiry);

        if (hasPlan) {
            if (expiry > System.currentTimeMillis()) {
                long remainingDays = calculateDaysDifference(expiry);

                if (remainingDays >= 0) {
                    binding.txtSubsDays.setText(remainingDays + "");
                    binding.txtSubsPlan.setText(planName);
                } else {
                    binding.txtSubsDays.setText("Expired " + Math.abs(remainingDays) + " days ago");
                    binding.txtSubsPlan.setText("Expired Plan");
                }
            } else {
                binding.txtSubsDays.setText("Plan Expired");
                binding.txtSubsPlan.setText("Expired Plan");
            }
        } else {
            binding.txtSubsDays.setText("No Plan Selected");
            binding.txtSubsPlan.setText("No Plan Selected");
        }
    }


    private void onAttachObservers() {
        viewModel.getLiveData().observe(getViewLifecycleOwner(), getSignUpUserResponses -> {
            if (getSignUpUserResponses != null) {
                String currentRetailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);

                for (GetSignUpUserResponse response : getSignUpUserResponses) {
                    if (response.getRetailerId().equals(currentRetailerId)) {
                        binding.txtEmail.setText(response.getRetailerEmail());

                        String savedPlanName = AppSession.getInstance(getContext()).getValue(Constants.SUBSCRIPTION_PLAN_NAME);


                        Log.d("Subscription", "Loaded Plan Name: " + savedPlanName);

                        if (savedPlanName != null && !savedPlanName.isEmpty()) {
                            binding.txtSubsPlan.setText(savedPlanName);
                        } else {
                          //  binding.txtSubsPlan.setText("No Plan Selected");
                        }
                        String expiryDateStr = (String) response.getSubsExpiryDate(); // "2025-06-20"

                        if (expiryDateStr != null && !expiryDateStr.isEmpty()) {
                            long expiryMillis = convertDateToMillis(expiryDateStr); // ✅ Convert Date to Milliseconds
                            long remainingDays = calculateDaysDifference(expiryMillis);

                            if (remainingDays >= 0) {
                                binding.txtSubsDays.setText(remainingDays + "");
                            } else {
                                binding.txtSubsDays.setText("Expired " + Math.abs(remainingDays) + " days ago");
                                sign.expiryStatus(currentRetailerId);
                                Toast.makeText(getContext(), "Your subscription has expired. Please log in again.", Toast.LENGTH_LONG).show();
                                AppSession.getInstance(getContext()).clear();
                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                requireActivity().finish();
                                return;
                            }
                        } else {
                            binding.txtSubsDays.setText("No expiry date available");
                        }
                    }
                }
            }
        });
    }
    private long convertDateToMillis(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            return date != null ? date.getTime() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private long calculateDaysDifference(long expiryMillis) {
        try {
            long currentTime = System.currentTimeMillis();
            long diffInMillis = expiryMillis - currentTime;
            return TimeUnit.MILLISECONDS.toDays(diffInMillis);
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Error case
        }
    }
    private void initClicks() {

        binding.txtEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        binding.txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UpdateAddressActivity.class);
                startActivity(intent);
            }
        });
        binding.txtLogout.setOnClickListener(v -> {
           logout();
        });
        binding.txtTermsCndition.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), TermsActivity.class);  // Assuming you have a LoginActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        binding.txtPrivacyPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PrivacyPolicyActivity.class);  // Assuming you have a LoginActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        binding.txtChangePass.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChangePasswordActivity.class);  // Assuming you have a LoginActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        binding.txtChat.setOnClickListener(v -> {
            Toast.makeText(getContext(), "coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.txtPlan.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SubscribeActivity.class);
            intent.putExtra("SHOW_ONLY_PAID_PLANS", true); // ✅ Free plan hide karne ka flag
            startActivity(intent);
        });
        binding.txtInvoice.setOnClickListener(v -> {
            String invoicePath = AppSession.getInstance(getContext()).getValue(Constants.INVOICE_PATH);

            if (invoicePath != null && !invoicePath.isEmpty()) {
                File file = new File(invoicePath);

                if (file.exists()) {
                    Uri uri = FileProvider.getUriForFile(
                            getContext(),
                            getContext().getPackageName() + ".provider",
                            file
                    );
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), "No app found to open the invoice", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Invoice file does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Invoice not found", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void startNetworkCheckService() {

        Intent serviceIntent = new Intent(getContext(), NetworkCheckService.class);
        requireActivity().startService(serviceIntent);

    }
    private void handleBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new HomeFragment(), "HomeFragment");
                transaction.commit();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        loadSubscriptionDetails();
    }
    private void logout() {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);

        boolean hasPlan = AppSession.getInstance(getContext()).getBoolean(Constants.PLAN_SELECTED + "_" + retailerId, false);
        long expiry = AppSession.getInstance(getContext()).getLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, 0);
        String planName = AppSession.getInstance(getContext()).getValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId);

        Log.d("Logout", "Saving Plan Data: " + planName + " Expiry: " + expiry);

        AppSession.getInstance(getContext()).clear();

        AppSession.getInstance(getContext()).setBoolean(Constants.PLAN_SELECTED + "_" + retailerId, hasPlan);
        AppSession.getInstance(getContext()).putLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, expiry);
        AppSession.getInstance(getContext()).setValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId, planName);

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finishAffinity();
    }




//    private boolean isNetworkConnected() {
//        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (cm != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                Network network = cm.getActiveNetwork();
//                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
//                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
//            } else {
//
//                return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
//            }
//        }
//        return false;
//    }
//    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (isNetworkConnected()) {
//
//                reloadData();
//            } else {
//
//            }
//        }
//    };
//    public void onResume() {
//        super.onResume();
//
//    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (getContext() != null) {
//            getContext().unregisterReceiver(networkReceiver); // Properly unregister the receiver
//        }
//    }
//    private void reloadData() {
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        }, 5000);
//    }
}