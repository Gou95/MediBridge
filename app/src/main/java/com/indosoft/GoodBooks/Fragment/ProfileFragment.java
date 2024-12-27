package com.indosoft.GoodBooks.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indosoft.GoodBooks.Activities.EditProfile;
import com.indosoft.GoodBooks.R;
import com.indosoft.GoodBooks.databinding.FragmentProfileBinding;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentProfileBinding.inflate(inflater, container, false);

        initClicks();

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
    }
}