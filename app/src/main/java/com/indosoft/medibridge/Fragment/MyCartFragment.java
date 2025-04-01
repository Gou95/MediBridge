package com.indosoft.medibridge.Fragment;


import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.indosoft.medibridge.Activities.NotificationActivity;
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
    private static final String CHANNEL_ID = "myFirebaseChannel";
    int counter = 0;
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
        adapter = new CardListAdapter(getContext(), dayViewModel, list, showCartViewModel );
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setVisibility(View.GONE);
        binding.btnAddCart.setVisibility(View.GONE);
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
            list.clear();
            if (response != null && !response.isEmpty()) {
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
                refreshCartData();
            } else {

            }

        });
        dayViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (response != null) {

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

            orderViewModel.getLiveData().removeObservers(getViewLifecycleOwner());
            orderViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
                if (response != null) {
                    sendNotification("Order Confirmation", "Your order has been successfully placed!");
                    AppSession.getInstance(getContext()).setValue(Constants.CART_COUNT, "0");


                    if (getActivity() instanceof DashBoardActivity) {
                        ((DashBoardActivity) getActivity()).updateBadgeCounter(0);
                    }

                    showCartViewModel.getLiveData().observe(getViewLifecycleOwner(),showCartResponses -> {
                       if (showCartResponses!=null){
                           list.clear();
                           list.addAll(showCartResponses);

                       }
                    });

                    refreshCartData();
                    navigateToOrderFragment();
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

        if (!retailerId.isEmpty() && !AppSession.getInstance(getContext()).getValue(Constants.CART_COUNT).equals("0")) {
            showCartViewModel.getShowPostCartData(retailerId);
        }
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
    @Override
    public void onResume() {
        super.onResume();
        String cartCount = AppSession.getInstance(getContext()).getValue(Constants.CART_COUNT);

        Log.d("MyCartFragment", "Cart count on resume: " + cartCount);

        int count = (cartCount == null || cartCount.isEmpty()) ? 0 : Integer.parseInt(cartCount);

        if (list.isEmpty()) {
            count = 0;
            AppSession.getInstance(getContext()).setValue(Constants.CART_COUNT, "0");
        }
        updateBadgeCount(count);
    }
    private void sendNotification(String title, String message) {
        createNotificationChannel();
        Intent intent = new Intent(getContext(), NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // Ensure this drawable exists
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getContext());
        if (managerCompat.areNotificationsEnabled()) {
            managerCompat.notify(101, builder.build());
        }
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Firebase Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for Firebase push notifications");

            NotificationManager manager = requireContext().getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

    }
    private void updateBadgeCount(int count) {
        String cartValue = count > 0 ? String.valueOf(count) : "0";
        AppSession.getInstance(getContext()).setValue(Constants.CART_COUNT, cartValue);

        if (getActivity() instanceof DashBoardActivity) {
            DashBoardActivity dashBoardActivity = (DashBoardActivity) getActivity();
            dashBoardActivity.updateBadgeCounter(count);  // Ensure this method is properly called
        }
    }

    }





