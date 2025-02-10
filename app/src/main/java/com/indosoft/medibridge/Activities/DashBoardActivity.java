package com.indosoft.medibridge.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationBarView;
import com.indosoft.medibridge.Fragment.HomeFragment;
import com.indosoft.medibridge.Fragment.MyCartFragment;
import com.indosoft.medibridge.Fragment.OrderFragment;
import com.indosoft.medibridge.Fragment.ProfileFragment;
import com.indosoft.medibridge.Fragment.UrgentCartFragment;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.databinding.ActivityDashBoardBinding;

public class DashBoardActivity extends AppCompatActivity {
    ActivityDashBoardBinding binding;
    private int urgentBadgeCount = 0;
    private int cartCount = 0;
    private boolean isReceiverRegistered = false;
    private int currentSelectedItemId = R.id.home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initClicks();
        bottomNavigation();
        initializeBadge();

        if (!isNetworkConnected()) {
            showNoConnectionView();
        } else {
            hideNoConnectionView();
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HomeFragment(), "HomeFragment")
                    .commit();
        }

    }
    private void initClicks() {
        binding.flotingBtn.setOnClickListener(v -> {
            new Handler().postDelayed(() -> binding.flotingBtn.setEnabled(true), 1000);
            resetBottomNavigationSelection();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MyCartFragment(), "MY_CART_FRAGMENT")
                    .addToBackStack(null)
                    .commit();
        });
    }
    private void bottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String tag = "";
                if (item.getItemId() == R.id.home) {
                    selectedFragment = new HomeFragment();
                    tag = "HomeFragment";
                } else if (item.getItemId() == R.id.urgent) {
                    selectedFragment = new UrgentCartFragment();
                    tag = "UrgentFragment";
                } else if (item.getItemId() == R.id.order) {
                    selectedFragment = new OrderFragment();
                    tag = "OrderFragment";
                } else if (item.getItemId() == R.id.profile) {
                    selectedFragment = new ProfileFragment();
                    tag = "ProfileFragment";
                }

                if (selectedFragment != null) {
                    switchFragment(selectedFragment, tag);
                    return true;
                }
                return false;

            }
        });
    }
    private void switchFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (existingFragment != null) {
            transaction.replace(R.id.fragment_container, existingFragment, tag);
        } else {
            transaction.replace(R.id.fragment_container, fragment, tag);
        }
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        transaction.commit();
        updateCartBadge(0);
    }
    private void resetBottomNavigationSelection() {
        binding.bottomNavigation.clearFocus();
    }
    private void showNoConnectionView() {
        binding.noConnectionLayout.setVisibility(View.VISIBLE);
        binding.fragmentContainer.setVisibility(View.GONE);
        binding.bottomNavigation.setVisibility(View.GONE);
        binding.frameLayout.setVisibility(View.GONE); // Hide the floating button
    }
    private void hideNoConnectionView() {
        binding.noConnectionLayout.setVisibility(View.GONE);
        binding.fragmentContainer.setVisibility(View.VISIBLE);
        binding.bottomNavigation.setVisibility(View.VISIBLE);
        binding.frameLayout.setVisibility(View.VISIBLE);
    }
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
        Fragment visibleFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (visibleFragment instanceof HomeFragment) {
            manuallySelectTab(R.id.home);
        } else if (visibleFragment instanceof UrgentCartFragment) {
            manuallySelectTab(R.id.urgent);
        } else if (visibleFragment instanceof OrderFragment) {
            manuallySelectTab(R.id.order);
        } else if (visibleFragment instanceof ProfileFragment) {
            manuallySelectTab(R.id.profile);
        }
    }
    private void manuallySelectTab(int tabId) {
        View tabView = binding.bottomNavigation.findViewById(tabId);
        if (tabView != null) {
            tabView.performClick(); // Simulate a click on the tab to trigger its selection
        }
    }
    public int getUrgentBadgeCount() {
        return urgentBadgeCount;
    }
    public void updateUrgentBadge(int count) {
        urgentBadgeCount = count; // Update the count
        AppSession.getInstance(this).setValue(Constants.URGENT_BADGE_COUNT, String.valueOf(count));
        setBadge(urgentBadgeCount);
    }
    private void setBadge(int urgentBadgeCount) {
        if (urgentBadgeCount == 0) {
            binding.bottomNavigation.removeBadge(R.id.urgent);  // Remove the badge if count is 0
        } else {
            BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.urgent);
            badge.setNumber(urgentBadgeCount);
            badge.setBackgroundColor(getResources().getColor(R.color.red));
            badge.setBadgeTextColor(getResources().getColor(R.color.white));
        }
    }
    public void clearUrgentBadge() {
        binding.bottomNavigation.removeBadge(R.id.urgent);
        urgentBadgeCount = 0;
        
        AppSession.getInstance(this).setValue(Constants.URGENT_BADGE_COUNT, "0");
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
                hideNoConnectionView();
                reloadData();
            } else {
                showNoConnectionView();
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        if (!isReceiverRegistered) {
            registerReceiver(networkReceiver, filter);
            isReceiverRegistered = true;
        }
        int cartCount = Integer.parseInt(AppSession.getInstance(this).getValue(Constants.CART_COUNT));
        updateCartBadge(cartCount);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isReceiverRegistered) {
            unregisterReceiver(networkReceiver);
            isReceiverRegistered = false;
        }
    }

    private void reloadData() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 5000);
    }
    public void updateTxtBadge(int count) {
        TextView badgeTextView = findViewById(R.id.txt_badge);
        if (badgeTextView != null) {
            if (count > 0) {
                badgeTextView.setText(String.valueOf(count));
                badgeTextView.setVisibility(View.VISIBLE);
            } else {
                badgeTextView.setText("");
                badgeTextView.setVisibility(View.GONE);
            }
        }
    }

    public void resetBadgeCount() {
        AppSession.getInstance(this).setValue(Constants.CART_COUNT, "0");
        updateTxtBadge(0);
    }
    public void updateCartBadge(int count) {
        if (count == 0) {
            binding.txtBadge.setVisibility(View.GONE);
            binding.txtBadge.setText("");// ✅ Hide the badge when cart is empty
        } else {
            binding.txtBadge.setVisibility(View.VISIBLE);
            binding.txtBadge.setText(String.valueOf(count));
        }

        AppSession.getInstance(this).setValue(Constants.CART_COUNT, String.valueOf(count));
    }


    private void initializeBadge() {
        String savedBadgeCount = AppSession.getInstance(this).getValue(Constants.CART_COUNT);
        int badgeCount = 0;
        try {
            badgeCount = Integer.parseInt(savedBadgeCount);
        } catch (NumberFormatException e) {
            badgeCount = -1;
        }
        this.cartCount = badgeCount;
        updateTxtBadge(cartCount);

        String savedUrgentCount = AppSession.getInstance(this).getValue(Constants.URGENT_BADGE_COUNT);
        int urgentBadgeCount = 0;
        try {
            urgentBadgeCount = Integer.parseInt(savedUrgentCount);
        } catch (NumberFormatException e) {
            urgentBadgeCount = -1;
        }
        this.urgentBadgeCount = urgentBadgeCount;
        setBadge(urgentBadgeCount);
    }

    public int getCartBadgeCount() {
        return cartCount;
    }
    public int getUrgentBadge(){
        return urgentBadgeCount;
    }
}






