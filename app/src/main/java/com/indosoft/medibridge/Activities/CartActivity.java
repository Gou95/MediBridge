package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.databinding.ActivityCartBinding;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity implements CardListAdapter.OnUrgentMovedListener {
    ActivityCartBinding binding;
    ShowCartViewModel showCartViewModel;
    CardListAdapter adapter;
    DeleteCartViewModel deleteCartViewModel;
    ProceedOrderViewModel orderViewModel;
    QuantityChangeViewModel quantityChangeViewModel;
    DeliveryDayViewModel dayViewModel;
    SignUpViewModel sign;
    ArrayList<ShowCartResponse> list = new ArrayList<>();
    private static final String CHANNEL_ID = "myFirebaseChannel";
    private int updatedCartCount = 0;
    private int updatedUrgentCount = 0;
    boolean isReceiverRegistered = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityCartBinding.inflate(getLayoutInflater());
      setContentView(binding.getRoot());
        showCartViewModel = new ViewModelProvider(this).get(ShowCartViewModel.class);
        showCartViewModel.init(this);
        deleteCartViewModel = new ViewModelProvider(this).get(DeleteCartViewModel.class);
        deleteCartViewModel.init(this);
        quantityChangeViewModel = new ViewModelProvider(this).get(QuantityChangeViewModel.class);
        quantityChangeViewModel.init(this);
        dayViewModel = new ViewModelProvider(this).get(DeliveryDayViewModel.class);
        dayViewModel.init(this);
        orderViewModel = new ViewModelProvider(this).get(ProceedOrderViewModel.class);  // Ensure this is initialized
        orderViewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);  // Ensure this is initialized
        sign.init(this);
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachObservers);
        onAttachObservers();
        initClicks();
       // handleBackPress();

        startNetworkCheckService();

        adapter = new CardListAdapter(this, dayViewModel, list, showCartViewModel, () -> {
            updatedUrgentCount++;
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setVisibility(View.GONE);
        binding.linearHide.setVisibility(View.VISIBLE);
        binding.linearButtons.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        showCartViewModel.getShowPostCartData(retailerId);

        View buttonView = binding.btnAddCart;

        ViewCompat.setOnApplyWindowInsetsListener(buttonView, (v, insets) -> {
            int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
            v.setPadding(
                    v.getPaddingLeft(),
                    v.getPaddingTop(),
                    v.getPaddingRight(),
                    bottomInset
            );
            return insets;
        });


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale > 1.0f) {
            newConfig.fontScale = 1.0f;
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
        super.onConfigurationChanged(newConfig);
    }

    private void initClicks() {
        binding.btnAddCart.setOnClickListener(v -> {
            showPopup();
        });
        binding.imgBack.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnClearCart.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Clear Cart")
                    .setMessage("Are you sure you want to delete all items?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        String retailerId = AppSession.getInstance(this)
                                .getValue(Constants.RELAILER_ID);

                        sign.deleteAllCart(retailerId);

                    })
                    .setNegativeButton("No", null)
                    .show();
        });


        TextView title = binding.txtCart;
        SpannableString spannable = new SpannableString("My Cart");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 3, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);

    }

    private void onAttachObservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        showCartViewModel.getLiveData().observe(this, response -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            list.clear();

            if (response != null && !response.isEmpty()) {
                list.addAll(response);
                adapter.notifyDataSetChanged();
                updateCartUI(true);   // ✅ List me data

            } else {
                updateCartUI(false);  // ✅ List empty
            }
        });

        deleteCartViewModel.getLiveData().observe(this, response -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (response != null) {
                refreshCartData();
            } else {

            }

        });
        dayViewModel.getLiveData().observe(this, response -> {
            binding.swipeRefreshLayout.setRefreshing(false);
            if (response != null) {

            }
        });
        sign.getLiveData().observe(this, signUpResponse -> {

            if (signUpResponse != null) {

                Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();

                // ✅ Cart UI clear
                list.clear();
                adapter.notifyDataSetChanged();

                updateCartUI(false);

                // ✅ Session cart count reset
                AppSession.getInstance(this).setValue(Constants.CART_COUNT, "0");

                // ✅ Dashboard badge reset
                if (getApplicationContext() instanceof DashBoardActivity) {
                    ((DashBoardActivity) getApplicationContext()).updateBadgeCounter(0);
                }
            }

        });

    }

    private void showPopup() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(popupView);
        TextView title = popupView.findViewById(R.id.popup_title);
        TextView cancel = popupView.findViewById(R.id.popup_cancel);
        TextView confirm = popupView.findViewById(R.id.popup_confirm);
        title.setText("Are you sure you want to confirm your order?");
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirm.setOnClickListener(v -> {
            if (list == null || list.isEmpty()) {
                updateCartUI(false);
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
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        if (orderViewModel != null) {
            Log.d("CartActivity", "Attempting to place order for retailer: " + retailerId);
            orderViewModel.getProccedOrder(retailerId);

            orderViewModel.getLiveData().observe(this, response -> {
                if (response != null) {
                    sendNotification("Order Confirmation", "Your order has been successfully placed!");
                    list.clear();
                    adapter.notifyDataSetChanged();

                    AppSession.getInstance(getApplicationContext()).setValue(Constants.CART_COUNT, "0");

                    if (getApplicationContext() instanceof DashBoardActivity) {
                        ((DashBoardActivity) getApplicationContext()).updateBadgeCounter(0);
                    }
                    Intent intent = new Intent(CartActivity.this, OrderRegisterActivity.class);
                    startActivity(intent);
                    finish();
                    refreshCartData();
                   // navigateToOrderFragment();
                } else {
                    Log.e("CartActivity", "Failed to place order.");
                }
            });
        } else {
            Log.e("CartActivity", "orderViewModel is not initialized.");
        }
    }

    public void refreshCartData() {
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        showCartViewModel.getShowPostCartData(retailerId);
    }

    private void startNetworkCheckService() {
        Intent intent = new Intent(this, com.indosoft.medibridge.Services.NetworkCheckService.class);
        startService(intent);
    }

    private void updateCartUI(boolean isCartNotEmpty) {

        if (isCartNotEmpty) {
            // ✅ Data hai → List show, Empty layout hide
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.linearHide.setVisibility(View.GONE);
            binding.linearButtons.setVisibility(View.VISIBLE);

        } else {
            // ✅ Data nahi hai → List hide, Empty layout show
            binding.recyclerView.setVisibility(View.GONE);
            binding.linearHide.setVisibility(View.VISIBLE);
            binding.linearButtons.setVisibility(View.GONE);
        }
    }

    public void returnToDashboard() {
        AppSession.getInstance(this).setValue(Constants.CART_COUNT, String.valueOf(list.size()));
        String savedUrgent = AppSession.getInstance(this).getValue(Constants.URGENT_BADGE_COUNT);

        int urgentCount = 0;
        try {
            urgentCount = Integer.parseInt(savedUrgent);
        } catch (NumberFormatException e) {
            urgentCount = 0;
        }
        Intent intent = new Intent(CartActivity.this, DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("CART_BADGE_COUNT", list.size());
        intent.putExtra("URGENT_BADGE_COUNT", urgentCount); // Pass urgent badge
        intent.putExtra("SHOW_HOME_FRAGMENT", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnToDashboard();

    }


    private void sendNotification(String title, String message) {
        createNotificationChannel();
        Intent intent = new Intent(getApplicationContext(), NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // Ensure this drawable exists
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
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

            NotificationManager manager =getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

    }


    @Override
    public void onUrgentItemMoved() {
        String urgent = AppSession.getInstance(this).getValue(Constants.URGENT_BADGE_COUNT);
        int newUrgent = 1;
        try {
            newUrgent = Integer.parseInt(urgent) + 1;
        } catch (NumberFormatException ignored) {}

        AppSession.getInstance(this).setValue(Constants.URGENT_BADGE_COUNT, String.valueOf(newUrgent));
    }
    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
            }
        }
        return false;
    }

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
                reloadData();
            }
        }
    };

    private void reloadData() {
        new Handler().postDelayed(() -> {
            // You can refresh some data here if needed
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            isReceiverRegistered = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(networkReceiver);
            isReceiverRegistered = false;
        }
    }
}