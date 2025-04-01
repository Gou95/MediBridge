package com.indosoft.medibridge.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.indosoft.medibridge.Activities.DashBoardActivity;
import com.indosoft.medibridge.Adapter.UrgentCartAdapter;
import com.indosoft.medibridge.Model.GetUrgentCartResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.GetUrgentCartViewModel;
import com.indosoft.medibridge.ViewModel.UrgentDeleteViewModel;
import com.indosoft.medibridge.ViewModel.UrgentProceedViewModel;
import com.indosoft.medibridge.databinding.FragmentUrgentCartBinding;

import java.util.ArrayList;


public class UrgentCartFragment extends Fragment {

    FragmentUrgentCartBinding binding;
    GetUrgentCartViewModel viewModel;
    UrgentCartAdapter urgentCartAdapter;
    ArrayList<GetUrgentCartResponse> list = new ArrayList<>();
    UrgentDeleteViewModel urgentDeleteViewModel;
    UrgentProceedViewModel urgentProceedViewModel;

    private int cartCount = 0;
    private boolean isReceiverRegistered = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentUrgentCartBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(GetUrgentCartViewModel.class);
        viewModel.init(getContext());
        urgentDeleteViewModel = new ViewModelProvider(this).get(UrgentDeleteViewModel.class);
        urgentDeleteViewModel.init(getContext());

        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);

        viewModel.getRemoveAllCartData(retailerId);
        onAttachobservers();
        initClicks();
        startNetworkCheckService();
        handleBackPress();

        binding.recyclerView.setVisibility(View.GONE);
        binding.linearHide.setVisibility(View.VISIBLE);
        binding.btnAddCart.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachobservers);

        urgentCartAdapter = new UrgentCartAdapter(getContext(), list);
        binding.recyclerView.setAdapter(urgentCartAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (retailerId != null && !retailerId.isEmpty()) {
            viewModel.getRemoveAllCartData(retailerId);
        } else {

        }
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
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(getViewLifecycleOwner(), responses -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (responses != null && !responses.isEmpty()) {
                list.clear();
                list.addAll(responses);
                urgentCartAdapter.notifyDataSetChanged();
                updateCartUI(true);

            } else {
                updateCartUI(false);

            }

        });
    }
    private void showPopup() {
        urgentProceedViewModel = new ViewModelProvider(this).get(UrgentProceedViewModel.class);
        urgentProceedViewModel.init(getContext());

        View popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(popupView);

        TextView title = popupView.findViewById(R.id.popup_title);
        TextView cancel = popupView.findViewById(R.id.popup_cancel);
        TextView confirm = popupView.findViewById(R.id.popup_confirm);

        title.setText("Are you sure you want to confirm your order?");

        AlertDialog dialog = builder.create();

        confirm.setOnClickListener(v -> {
            if (list == null || list.isEmpty()) {
                updateCartUI(false);
                dialog.dismiss(); // Dismiss the dialog
                Toast.makeText(getContext(), "Your data is null.", Toast.LENGTH_SHORT).show();
            } else {
                fetchLastOrderAndPlaceOrder();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void fetchLastOrderAndPlaceOrder() {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        urgentProceedViewModel.getUrgentProceed(retailerId);
        urgentProceedViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                list.clear();
                urgentCartAdapter.notifyDataSetChanged();

                if (getActivity() instanceof DashBoardActivity) {
                    ((DashBoardActivity) getActivity()).clearUrgentBadge();  // Clear the badge on the floating button
                }

                navigateToOrderFragment();
                refreshUrgentCartData();
            } else {
                Toast.makeText(getContext(), "Failed to proceed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshUrgentCartData() {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        viewModel.getRemoveAllCartData(retailerId);
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
    private void updateCartUI(boolean isCartNotEmpty) {
        if (isCartNotEmpty) {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.linearHide.setVisibility(View.GONE);
            binding.btnAddCart.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.linearHide.setVisibility(View.VISIBLE);
            binding.btnAddCart.setVisibility(View.GONE);
        }

    }
}