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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
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
    private boolean isReceiverRegistered = false;
    private int previousCartCount = -1;
    private int urgentBadgeCount = 0;
    private int cartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initClicks();
        bottomNavigation();
        logFCM();
        userSession();
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
    private void userSession() {
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        if (retailerId == null || retailerId.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
    }
    private void initClicks() {
        binding.flotingBtn.setOnClickListener(v -> {
            new Handler().postDelayed(() -> binding.flotingBtn.setEnabled(true), 1000);
            resetBottomNavigationSelection();
            updateBadgeCounter(0);

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
        updateBadgeCounter(0);
    }
    private void resetBottomNavigationSelection() {
        binding.bottomNavigation.clearFocus();
    }
    private void showNoConnectionView() {
        binding.noConnectionLayout.setVisibility(View.VISIBLE);
        binding.fragmentContainer.setVisibility(View.GONE);
        binding.bottomNavigation.setVisibility(View.GONE);
//        binding.frameLayout.setVisibility(View.GONE); // Hide the floating button
    }
    private void hideNoConnectionView() {
        binding.noConnectionLayout.setVisibility(View.GONE);
        binding.fragmentContainer.setVisibility(View.VISIBLE);
        binding.bottomNavigation.setVisibility(View.VISIBLE);
      // binding.frameLayout.setVisibility(View.VISIBLE);
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
        String cartCount = AppSession.getInstance(this).getValue(Constants.CART_COUNT);
        int count = (cartCount != null && !cartCount.isEmpty()) ? Integer.parseInt(cartCount) : 0;

        if (count == 0) {
            AppSession.getInstance(this).setValue(Constants.CART_COUNT, null);
        }
        updateBadgeCounter(count);
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
    private void logFCM(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.i("##########FCM_TOKEN##########", "Fetching FCM token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.i("##########FCM_TOKEN##########", "FCM Token: " + token);
                        AppSession.getInstance(DashBoardActivity.this).setValue(Constants.STOCKIST_FCM_TOKEN,token);
                    }
                });
}
    public void updateBadgeCounter(Integer count) {
        Log.d("CartCount_Debug", "updateBadgeCounter called with: " + count);
        previousCartCount = count != null ? count : 0;

        if (previousCartCount > 0) {
            binding.badgeCounter.setVisibility(View.VISIBLE);
            binding.badgeCounter.setText(String.valueOf(previousCartCount));
        } else {
            binding.badgeCounter.setVisibility(View.GONE);
        }
    }


    public void updateUrgentBadge(int count) {
        urgentBadgeCount = count; // Update the count
        AppSession.getInstance(this).setValue(Constants.URGENT_BADGE_COUNT, String.valueOf(count));
        setBadge(urgentBadgeCount);
    }
    private void setBadge(int urgentBadgeCount) {
        if (urgentBadgeCount <= 0) {
            binding.bottomNavigation.removeBadge(R.id.urgent);  // Remove the badge if count is 0
        } else {
            BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.urgent);
            badge.setNumber(urgentBadgeCount);
            badge.setBackgroundColor(getResources().getColor(R.color.red));
            badge.setBadgeTextColor(getResources().getColor(R.color.white));
        }
    }
    public void updateCartBadge(int count) {
        if (binding.badgeCounter != null) {
            binding.badgeCounter.setText(String.valueOf(count));
            binding.badgeCounter.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }
    public int getCartBadgeCount() {
        return previousCartCount;
    }
    public int getUrgentBadgeCount() {
        return urgentBadgeCount;}

    public void clearUrgentBadge() {
        binding.bottomNavigation.removeBadge(R.id.urgent);
        urgentBadgeCount = 0;

        AppSession.getInstance(this).setValue(Constants.URGENT_BADGE_COUNT, "0");
    }

}






