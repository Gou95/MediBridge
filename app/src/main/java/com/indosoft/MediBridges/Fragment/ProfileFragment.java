package com.indosoft.MediBridges.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indosoft.MediBridges.Activities.AddressActivity;
import com.indosoft.MediBridges.Activities.EditProfile;
import com.indosoft.MediBridges.Activities.LoginActivity;
import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentProfileBinding.inflate(inflater, container, false);

        initClicks();
        String retailer_Id = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);

        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
            }
        });
        return binding.getRoot();
    }

    private void initClicks() {

        binding.txtEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getContext(), EditProfile.class);
                startActivity(intent);
            }
        });
        binding.txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddressActivity.class);
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
    }
}