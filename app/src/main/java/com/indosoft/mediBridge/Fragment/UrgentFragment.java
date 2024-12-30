package com.indosoft.mediBridge.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indosoft.mediBridge.Adapter.UrgentCartAdapter;
import com.indosoft.mediBridge.Model.GetUrgentCartResponse;
import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.Session.AppSession;
import com.indosoft.mediBridge.Session.Constants;
import com.indosoft.mediBridge.ViewModel.GetUrgentCartViewModel;
import com.indosoft.mediBridge.databinding.FragmentUrgentBinding;

import java.io.Serializable;
import java.util.ArrayList;


public class UrgentFragment extends Fragment {

    FragmentUrgentBinding binding;
    GetUrgentCartViewModel viewModel;
    UrgentCartAdapter urgentCartAdapter;
    ArrayList<GetUrgentCartResponse> list = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentUrgentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(GetUrgentCartViewModel.class);
        viewModel.init(getContext());

        String retailerId = AppSession.getInstance(getContext()).getString(Constants.RELAILER_ID);
        viewModel.getRemoveAllCartData(retailerId);
        onAttachobservers();


        urgentCartAdapter = new UrgentCartAdapter(getContext(), list);
        binding.recyclerView.setAdapter(urgentCartAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (retailerId != null && !retailerId.isEmpty()) {
            viewModel.getRemoveAllCartData(retailerId);
        } else {
            // Handle the case where retailerId is null or empty
            // Optionally, show an error message to the user
        }

        return binding.getRoot();
    }

    private void onAttachobservers() {
        viewModel.getLiveData().observe(getViewLifecycleOwner(),responses -> {
            if (responses != null && !responses.isEmpty()) {
                list.clear();
                list.addAll(responses);

                // Notify the adapter about data changes
                urgentCartAdapter.notifyDataSetChanged();
            } else {
                // Handle the case where responses are null or empty
                list.clear();
                urgentCartAdapter.notifyDataSetChanged();
            }
        });
    }
}