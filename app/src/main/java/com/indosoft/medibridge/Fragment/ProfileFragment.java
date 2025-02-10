package com.indosoft.medibridge.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indosoft.medibridge.Activities.ChangePasswordActivity;
import com.indosoft.medibridge.Activities.EditProfileActivity;
import com.indosoft.medibridge.Activities.LoginActivity;
import com.indosoft.medibridge.Activities.PrivacyPolicyActivity;
import com.indosoft.medibridge.Activities.TermsConditionActivity;
import com.indosoft.medibridge.Activities.UpdateAddressActivity;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= FragmentProfileBinding.inflate(inflater, container, false);

        initClicks();
        startNetworkCheckService();
        handleBackPress();
        return binding.getRoot();
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

            AppSession.getInstance(getContext()).clear();

            Intent intent = new Intent(getContext(), LoginActivity.class);  // Assuming you have a LoginActivity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            getActivity().finish();
        });
        binding.txtTermsCndition.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), TermsConditionActivity.class);  // Assuming you have a LoginActivity
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

    }

    private void startNetworkCheckService() {
        Intent serviceIntent = new Intent(getContext(), NetworkCheckService.class);
        requireActivity().startService(serviceIntent);

    }
    private void handleBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate to HomeFragment when back is pressed
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new HomeFragment(), "HomeFragment");
                transaction.commit();
            }
        });
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