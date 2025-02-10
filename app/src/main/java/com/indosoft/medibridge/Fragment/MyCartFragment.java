package com.indosoft.medibridge.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.indosoft.medibridge.Activities.DashBoardActivity;
import com.indosoft.medibridge.Adapter.CardListAdapter;
import com.indosoft.medibridge.Model.ShowCartResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.DeleteCartViewModel;
import com.indosoft.medibridge.ViewModel.DeliveryDayViewModel;
import com.indosoft.medibridge.ViewModel.ProceedOrderViewModel;
import com.indosoft.medibridge.ViewModel.QuantityChangeViewModel;
import com.indosoft.medibridge.ViewModel.ShowCartViewModel;
import com.indosoft.medibridge.databinding.FragmentMyCartBinding;

import java.util.ArrayList;


public class MyCartFragment extends Fragment {
    FragmentMyCartBinding binding;
    ShowCartViewModel showCartViewModel;
    CardListAdapter adapter;
    DeleteCartViewModel deleteCartViewModel;
    ProceedOrderViewModel orderViewModel;
    QuantityChangeViewModel quantityChangeViewModel;
    DeliveryDayViewModel dayViewModel;
    ArrayList<ShowCartResponse> list = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding= FragmentMyCartBinding.inflate(inflater, container, false);
        showCartViewModel = new ViewModelProvider(this).get(ShowCartViewModel.class);
        showCartViewModel.init(getContext());
        deleteCartViewModel = new ViewModelProvider(this).get(DeleteCartViewModel.class);
        deleteCartViewModel.init(getContext());
        quantityChangeViewModel = new ViewModelProvider(this).get(QuantityChangeViewModel.class);
        quantityChangeViewModel.init(getContext());
        dayViewModel = new ViewModelProvider(this).get(DeliveryDayViewModel.class);
        dayViewModel.init(getContext());
        orderViewModel = new ViewModelProvider(this).get(ProceedOrderViewModel.class);  // Ensure this is initialized
        orderViewModel.init(getContext());
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        onAttachObservers();
        initClicks();
        handleBackPress();

        startNetworkCheckService();
        adapter = new CardListAdapter(getContext(), dayViewModel, list, showCartViewModel);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setVisibility(View.GONE);
        binding.linearHide.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setRefreshing(false);
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        showCartViewModel.getShowPostCartData(retailerId);
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
    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        showCartViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (response != null && !response.isEmpty()) {
                list.clear();
                list.addAll(response);
                adapter.notifyDataSetChanged();
                updateCartUI(true);
                updateBadgeCount(list.size());
            } else {
                updateCartUI(false);
                updateBadgeCount(0);
            }
        });
        deleteCartViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (response != null) {
                //  Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                refreshCartData();
            } else {
                // Toast.makeText(getContext(), "Failed to delete item.", Toast.LENGTH_SHORT).show();
            }
            if (getActivity() instanceof DashBoardActivity) {
                if (list == null || list.isEmpty()) {
                    ((DashBoardActivity) getActivity()).updateCartBadge(0); // Hide badge when cart is empty
                } else {
                    ((DashBoardActivity) getActivity()).updateCartBadge(list.size()); // Show count
                }
            }
        });
        dayViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (response != null) {
                //  Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showPopup() {
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(popupView);
        TextView title = popupView.findViewById(R.id.popup_title);
        TextView cancel = popupView.findViewById(R.id.popup_cancel);
        TextView confirm = popupView.findViewById(R.id.popup_confirm);
        title.setText("Are you sure you want to confirm your order?");
        AlertDialog dialog = builder.create();
        confirm.setOnClickListener(v -> {
            if (list == null || list.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show();
                updateCartUI(false);
            } else {
                fetchLastOrderAndPlaceOrder();
            }
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.show();
    }
    private void fetchLastOrderAndPlaceOrder() {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        if (orderViewModel != null) {
            Log.d("CartActivity", "Attempting to place order for retailer: " + retailerId);
            orderViewModel.getProccedOrder(retailerId);
            orderViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
                if (response != null) {
                    Log.d("CartActivity", "Order successfully placed: " + response.getMessage());
                    if (getActivity() instanceof DashBoardActivity) {
                        ((DashBoardActivity) getActivity()).resetBadgeCount();  // Reset the badge in parent activity
                    }
                   // clearCartData();
                    refreshCartData(); // Cart List Update Karein
                    navigateToOrderFragment(); // Order Fragment par Navigate Karein
                } else {
                    Log.e("CartActivity", "Failed to place order.");
                }
            });
        } else {
            Log.e("CartActivity", "orderViewModel is not initialized.");
        }
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
    public void refreshCartData() {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        showCartViewModel.getShowPostCartData(retailerId);
        binding.btnAddCart.setEnabled(true);
        binding.btnAddCart.setAlpha(1.0f);
    }
    private void startNetworkCheckService() {
        Intent serviceIntent = new Intent(getContext(), NetworkCheckService.class);
        requireActivity().startService(serviceIntent);

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
    private void handleBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToOrderFragment();
            }
        });
    }


    private void updateBadgeCount(int count) {
        if (getActivity() instanceof DashBoardActivity) {
            ((DashBoardActivity) getActivity()).updateCartBadge(count);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        updateBadgeCount(list.isEmpty() ? 0 : list.size());
    }

}