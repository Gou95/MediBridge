package com.indosoft.mediBridge.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indosoft.mediBridge.Activities.AddressActivity;
import com.indosoft.mediBridge.Activities.EditProfile;
import com.indosoft.mediBridge.Activities.LoginActivity;
import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.Session.AppSession;
import com.indosoft.mediBridge.Session.Constants;
import com.indosoft.mediBridge.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentProfileBinding.inflate(inflater, container, false);

        initClicks();
        String retailer_Id = AppSession.getInstance(getContext()).getString(Constants.RELAILER_ID);


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