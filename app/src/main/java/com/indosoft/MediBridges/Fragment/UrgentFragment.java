package com.indosoft.MediBridges.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.indosoft.MediBridges.Adapter.UrgentCartAdapter;
import com.indosoft.MediBridges.Body.OrderDetailsBody;
import com.indosoft.MediBridges.Model.GetUrgentCartResponse;
import com.indosoft.MediBridges.Model.LastOrderResponse;
import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.ViewModel.GetUrgentCartViewModel;
import com.indosoft.MediBridges.ViewModel.LastOrderViewModel;
import com.indosoft.MediBridges.ViewModel.OrderDetailsViewModel;
import com.indosoft.MediBridges.ViewModel.OrderViewModel;
import com.indosoft.MediBridges.ViewModel.UrgentDeleteViewModel;
import com.indosoft.MediBridges.ViewModel.UrgentProceedViewModel;
import com.indosoft.MediBridges.databinding.FragmentUrgentBinding;

import java.util.ArrayList;


public class UrgentFragment extends Fragment {

    FragmentUrgentBinding binding;
    GetUrgentCartViewModel viewModel;
    UrgentCartAdapter urgentCartAdapter;
    ArrayList<GetUrgentCartResponse> list = new ArrayList<>();
    ArrayList<LastOrderResponse> lastOrderList = new ArrayList<>();
    UrgentDeleteViewModel urgentDeleteViewModel;

    LastOrderViewModel lastOrderViewModel;
    OrderViewModel orderViewModel;
    OrderDetailsViewModel detailsViewModel;
    UrgentProceedViewModel urgentProceedViewModel;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentUrgentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(GetUrgentCartViewModel.class);
        viewModel.init(getContext());
        urgentDeleteViewModel = new ViewModelProvider(this).get(UrgentDeleteViewModel.class);
        urgentDeleteViewModel.init(getContext());

        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        Toast.makeText(getContext(), retailerId, Toast.LENGTH_SHORT).show();

        binding.recyclerView.setVisibility(View.GONE);  // Initially hide RecyclerView
        binding.linearHide.setVisibility(View.VISIBLE);
        binding.relativeTotal.setVisibility(View.GONE); // Hide relativeTotal
        binding.btnAddCart.setVisibility(View.GONE);

        viewModel.getRemoveAllCartData(retailerId);
        onAttachobservers();
        initClicks();


        urgentCartAdapter = new UrgentCartAdapter(getContext(), list, totalQuantity ->
                binding.txtTotalPrice.setText(String.valueOf(totalQuantity))
        );
        binding.recyclerView.setAdapter(urgentCartAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (retailerId != null && !retailerId.isEmpty()) {
            viewModel.getRemoveAllCartData(retailerId);
        } else {

        }


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

        binding.btnAddCart.setOnClickListener(v -> {

            showPopup();
        });
        binding.imgBack.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment()) // Replace with your HomeFragment
                        .commit();
            }
        });



    }
    private void onAttachobservers() {
        viewModel.getLiveData().observe(getViewLifecycleOwner(), responses -> {
            if (responses != null && !responses.isEmpty()) {
                // If the list is not empty, populate the list and show RecyclerView
                list.clear();
                list.addAll(responses);

                // Notify the adapter that data has changed
                urgentCartAdapter.notifyDataSetChanged();

                // Calculate total quantity
                int totalQuantity = calculateTotalQuantity();
                binding.txtTotalPrice.setText(String.valueOf(totalQuantity));


                binding.recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
                binding.linearHide.setVisibility(View.GONE); // Hide linearHide
                binding.relativeTotal.setVisibility(View.VISIBLE); // Show relativeTotal
                binding.btnAddCart.setVisibility(View.VISIBLE); // Show btnAddCart
            } else {

                list.clear();
                urgentCartAdapter.notifyDataSetChanged();


                binding.txtTotalPrice.setText("0");

                binding.recyclerView.setVisibility(View.GONE); // Hide RecyclerView
                binding.linearHide.setVisibility(View.VISIBLE); // Show linearHide
                binding.relativeTotal.setVisibility(View.GONE); // Hide relativeTotal
                binding.btnAddCart.setVisibility(View.GONE); // Hide btnAddCart
            }
        });
    }



    private void showPopup() {

        urgentProceedViewModel = new ViewModelProvider(this).get(UrgentProceedViewModel.class);
        urgentProceedViewModel.init(getContext());

        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to confirm your order?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (list == null || list.isEmpty()) {
                        binding.linearHide.setVisibility(View.VISIBLE);
                        binding.relativeTotal.setVisibility(View.GONE);
                        binding.btnAddCart.setVisibility(View.GONE);
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Your data is null.", Toast.LENGTH_SHORT).show();
                    } else {
                        fetchLastOrderAndPlaceOrder();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void fetchLastOrderAndPlaceOrder() {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        urgentProceedViewModel.getUrgentProceed(retailerId);
        urgentProceedViewModel.getLiveData().observe(getViewLifecycleOwner(),response -> {
            if (response !=null){
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                navigateToOrderFragment();
                refreshUrgentCartData();
            }
        });

    }
    private void refreshUrgentCartData() {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        viewModel.getRemoveAllCartData(retailerId);
    }
    private int calculateTotalQuantity() {
        int total = 0;
        for (GetUrgentCartResponse item : list) {
            total += (int) Float.parseFloat(item.getQty());
        }
        return total;
    }
    private void navigateToOrderFragment() {
        if (isAdded() && getActivity() != null) {
            OrderFragment orderFragment = new OrderFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, orderFragment) // Replace with your actual container ID
                    .addToBackStack(null) // Optionally add to backstack
                    .commit();
        } else {
            Log.e("UrgentFragment", "Fragment is not attached to the activity. Navigation failed.");
        }
    }



}