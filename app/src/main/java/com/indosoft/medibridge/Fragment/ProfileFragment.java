package com.indosoft.medibridge.Fragment;

import static androidx.compose.ui.semantics.SemanticsPropertiesKt.setText;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.indosoft.medibridge.Activities.AboutUsActivity;
import com.indosoft.medibridge.Activities.ChangePasswordActivity;
import com.indosoft.medibridge.Activities.DashBoardActivity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        String imageUrl = "https://medibro.in/v2/api/uploads/image" + retailerId + ".png";

        Glide.with(getContext())
                .load(imageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.imgProfile);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );
        return binding.getRoot();
    }



    private void onAttachObservers() {
        viewModel.getLiveData().observe(getViewLifecycleOwner(), getSignUpUserResponses -> {
            if (getSignUpUserResponses != null) {
                String currentRetailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);

                for (GetSignUpUserResponse response : getSignUpUserResponses) {
                    if (response.getRetailerId().equals(currentRetailerId)) {
                        binding.txtEmail.setText(response.getRetailerEmail());
                        binding.txtName.setText(response.getRetailerName());

                        String savedPlanName = AppSession.getInstance(getContext())
                                .getValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + currentRetailerId);
                        if (savedPlanName != null && !savedPlanName.isEmpty()) {
                            binding.txtSubsPlan.setText(savedPlanName);
                        } else {
                          //  binding.txtSubsPlan.setText("No Plan Selected");
                        }

                        String expiryDateStr = response.getSubsExpiryDate(); // format: "2025-06-20"
                        if (expiryDateStr != null && !expiryDateStr.isEmpty()) {
                            long expiryMillis = convertDateToMillis(expiryDateStr);
                            long remainingDays = calculateDaysDifference(expiryMillis);

                            if (remainingDays >= 0) {
                                binding.txtSubsDays.setText(remainingDays + " ");
                            } else {
                                binding.txtSubsPlan.setText("Expired");
                                binding.txtSubsDays.setText("Expired " + Math.abs(remainingDays) + " days ago");

                                sign.expiryStatus(currentRetailerId);
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

                        break;
                    }
                }
            }
        });
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
        binding.txtAboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AboutUsActivity.class);  // Assuming you have a LoginActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });


        binding.txtPlan.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SubscribeActivity.class);
            boolean isActive = AppSession.getInstance(getContext()).getBoolean(Constants.IS_ACTIVE, false);
            intent.putExtra("SHOW_ONLY_PAID_PLANS", isActive);  // Pass true if active
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

        int nightModeFlags = getActivity().getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
         //   holder.userImage.setColorFilter(android.graphics.Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
         //   holder.userImage.setColorFilter(android.graphics.Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);
        }

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

    }
    private void logout() {

        String retailerId = AppSession.getInstance(getContext())
                .getValue(Constants.RELAILER_ID);

        // 🔒 SAVE POPUP FLAG BEFORE CLEAR
        boolean popupShown = AppSession.getInstance(getContext())
                .getBoolean("FREE_PLAN_POPUP_SHOWN_" + retailerId, false);

        boolean hasPlan = AppSession.getInstance(getContext())
                .getBoolean(Constants.PLAN_SELECTED + "_" + retailerId, false);

        long expiry = AppSession.getInstance(getContext())
                .getLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, 0);

        String planName = AppSession.getInstance(getContext())
                .getValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId);

        // ❌ CLEAR SESSION
        AppSession.getInstance(getContext()).clear();

        // ✅ RESTORE REQUIRED DATA
        AppSession.getInstance(getContext())
                .setBoolean("FREE_PLAN_POPUP_SHOWN_" + retailerId, popupShown);

        AppSession.getInstance(getContext())
                .setBoolean(Constants.PLAN_SELECTED + "_" + retailerId, hasPlan);

        AppSession.getInstance(getContext())
                .putLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, expiry);

        AppSession.getInstance(getContext())
                .setValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId, planName);

        // 🚀 GO TO LOGIN
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finishAffinity();
    }

    //    private void logout() {
//        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
//
//        boolean hasPlan = AppSession.getInstance(getContext()).getBoolean(Constants.PLAN_SELECTED + "_" + retailerId, false);
//        long expiry = AppSession.getInstance(getContext()).getLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, 0);
//        String planName = AppSession.getInstance(getContext()).getValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId);
//
//        Log.d("Logout", "Saving Plan Data: " + planName + " Expiry: " + expiry);
//
//        AppSession.getInstance(getContext()).clear();
//
//        AppSession.getInstance(getContext()).setBoolean(Constants.PLAN_SELECTED + "_" + retailerId, hasPlan);
//        AppSession.getInstance(getContext()).putLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, expiry);
//        AppSession.getInstance(getContext()).setValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId, planName);
//
//        Intent intent = new Intent(getContext(), LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        requireActivity().finishAffinity();
//    }
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
    private void loadSubscriptionDetails(TextView subscriptionPlan, TextView subscriptionDays) {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        boolean hasPlan = AppSession.getInstance(getContext()).getBoolean(Constants.PLAN_SELECTED + "_" + retailerId, false);
        long expiry = AppSession.getInstance(getContext()).getLong(Constants.PLAN_EXPIRY_DATE + "_" + retailerId, 0);
        String planName = AppSession.getInstance(getContext()).getValue(Constants.SUBSCRIPTION_PLAN_NAME + "_" + retailerId);

        Log.d("Subscription", "Loaded Plan: " + planName + ", Expiry: " + expiry);

        if (hasPlan && expiry > System.currentTimeMillis()) {
            long remainingDays = calculateDaysDifference(expiry);
            if (remainingDays >= 0) {
                subscriptionPlan.setText(planName);
                subscriptionDays.setText( remainingDays + "");
            } else {
                subscriptionPlan.setText("Expired");
                subscriptionDays.setText("Expired " + Math.abs(remainingDays) + " days ago");
            }
        } else {
            subscriptionPlan.setText("No Plan Selected");
            subscriptionDays.setText("N/A");
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