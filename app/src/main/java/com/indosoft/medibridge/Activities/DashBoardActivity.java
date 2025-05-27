package com.indosoft.medibridge.Activities;

import static com.itextpdf.io.font.otf.LanguageTags.TODO;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    private int urgentBadgeCount = 0;
    private int cartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNavigation();
        logFCM();
        userSession();

        if (!isNetworkConnected()) {
            showNoConnectionView();
        } else {
            hideNoConnectionView();
        }
        boolean openProfile = getIntent().getBooleanExtra("OPEN_PROFILE_FRAGMENT", false);
        boolean handledFragment = false;

        if (savedInstanceState == null) {
            if (openProfile) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ProfileFragment(), "ProfileFragment")
                        .commit();
                binding.bottomNavigation.setSelectedItemId(R.id.profile);
                handledFragment = true;
            }
        }

        // If no fragment handled yet, load default fragment
        if (!handledFragment && savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HomeFragment(), "HomeFragment")
                    .commit();
            binding.bottomNavigation.setSelectedItemId(R.id.home);
        }
//        Intent intent = getIntent();
//
//        if (intent != null) {
//            int updatedCart = intent.getIntExtra("CART_BADGE_COUNT", -1);
//            int updatedUrgent = intent.getIntExtra("URGENT_BADGE_COUNT", -1);
//            boolean showOrder = intent.getBooleanExtra("SHOW_ORDER_FRAGMENT", false);
//            boolean showHome = intent.getBooleanExtra("SHOW_HOME_FRAGMENT", false); // ✅ Add this line
//            boolean showProfile = intent.getBooleanExtra("SHOW_PROFILE_FRAGMENT", false);
//
//            if (updatedCart != -1) updateBadgeCounter(updatedCart);
//            if (updatedUrgent != -1) updateUrgentBadge(updatedUrgent);
//
//            if (showOrder) {
//                switchFragment(new OrderFragment(), "OrderFragment");
//                binding.bottomNavigation.setSelectedItemId(R.id.order);
//            }else if (showProfile) {
//                switchFragment(new ProfileFragment(), "ProfileFragment");
//                binding.bottomNavigation.setSelectedItemId(R.id.profile);
//            } else if (showHome) { // ✅ Show HomeFragment if coming from CartActivity
//                switchFragment(new HomeFragment(), "HomeFragment");
//                binding.bottomNavigation.setSelectedItemId(R.id.home);
//            }
//        }


//
//        Intent intent = getIntent();
//        if (intent != null) {
//            int updatedCart = intent.getIntExtra("CART_BADGE_COUNT", -1);
//            int updatedUrgent = intent.getIntExtra("URGENT_BADGE_COUNT", -1);
//            boolean showOrder = intent.getBooleanExtra("SHOW_ORDER_FRAGMENT", false);
//
//            if (updatedCart != -1) updateBadgeCounter(updatedCart);
//            if (updatedUrgent != -1) updateUrgentBadge(updatedUrgent);
//            if (showOrder) {
//                switchFragment(new OrderFragment(), "OrderFragment");
//                binding.bottomNavigation.setSelectedItemId(R.id.order);
//            }
//        }
        handleIntent(getIntent());

        initializeBadge();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale > 1.0f) {
            newConfig.fontScale = 1.0f;
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
        super.onConfigurationChanged(newConfig);
    }

    private void userSession() {
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        if (retailerId == null || retailerId.isEmpty()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }}
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
//                    selectedFragment = new UrgentCartFragment();
//                    tag = "UrgentCartFragment";
                    Intent intent = new Intent(DashBoardActivity.this, UrgentCartActivity.class);
                    startActivity(intent);
                    return false;
                } else if (item.getItemId() == R.id.myCart) {
//                    selectedFragment = new MyCartFragment();
//                    tag = "MyCartFragment";
                    Intent intent = new Intent(DashBoardActivity.this, CartActivity.class);
                    startActivity(intent);
                    return false;
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

        // 🔁 Only clear badge if not switching to MyCartFragment
        if (!"MyCartFragment".equals(tag)) {
            //  updateBadgeCounter(0);
        } else {
            // 👇 Re-apply badge from saved count to ensure it's visible
            String savedCartCount = AppSession.getInstance(this).getValue(Constants.CART_COUNT);
            int currentCartCount = 0;
            try {
                currentCartCount = Integer.parseInt(savedCartCount);
            } catch (NumberFormatException e) {
                currentCartCount = 0;
            }
           // setcartBadge(currentCartCount);
        }
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
       }
//       else if (visibleFragment instanceof UrgentCartFragment) {
//            manuallySelectTab(R.id.urgent);
//        } else if (visibleFragment instanceof MyCartFragment) {
//            manuallySelectTab(R.id.myCart);
//       }
       else if (visibleFragment instanceof OrderFragment) {
            manuallySelectTab(R.id.order);} else if (visibleFragment instanceof ProfileFragment) {
            manuallySelectTab(R.id.profile);
       }
       // finishAffinity();
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
        initializeBadge();
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
    private void logFCM() {
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
                        AppSession.getInstance(DashBoardActivity.this).setValue(Constants.STOCKIST_FCM_TOKEN, token);
                    }
                });
    }
    public void updateBadgeCounter(int count) {
        cartCount = count;
        AppSession.getInstance(this).setValue(Constants.CART_COUNT, String.valueOf(count));
        if (count <= 0) {
            binding.bottomNavigation.removeBadge(R.id.myCart);
        } else {
            BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.myCart);
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
            badge.setBadgeTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }
    public void updateUrgentBadge(int count) {
        urgentBadgeCount = count;
        AppSession.getInstance(this).setValue(Constants.URGENT_BADGE_COUNT, String.valueOf(count));

        if (count <= 0) {
            binding.bottomNavigation.removeBadge(R.id.urgent);
        } else {
            BadgeDrawable badge = binding.bottomNavigation.getOrCreateBadge(R.id.urgent);
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(ContextCompat.getColor(this, R.color.red));
            badge.setBadgeTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }
    public int getCartBadgeCount() {
        return cartCount;
    }
    public  int getUrgentBadgeCount() {
        return urgentBadgeCount;
    }

    public void clearMycartBadge() {
        binding.bottomNavigation.removeBadge(R.id.myCart);
        cartCount = 0;
        AppSession.getInstance(this).setValue(Constants.CART_COUNT, "0");
    }

    public void clearUrgentBadge() {
        binding.bottomNavigation.removeBadge(R.id.urgent);
        urgentBadgeCount = 0;
        AppSession.getInstance(this).setValue(Constants.URGENT_BADGE_COUNT, "0");
    }
    public void initializeBadge() {
        try {
            cartCount = Integer.parseInt(AppSession.getInstance(this).getValue(Constants.CART_COUNT));
        } catch (NumberFormatException e) {
            cartCount = 0;
        }
        updateBadgeCounter(cartCount);

        try {
            urgentBadgeCount = Integer.parseInt(AppSession.getInstance(this).getValue(Constants.URGENT_BADGE_COUNT));
        } catch (NumberFormatException e) {
            urgentBadgeCount = 0;
        }
        updateUrgentBadge(urgentBadgeCount);
    }

    private void showFreePlanActivatedPopup() {
        boolean isPopupShown = AppSession.getInstance(this).getBoolean(Constants.POPUP_SHOWN, false);
        boolean hasPaidPlan = AppSession.getInstance(this).getBoolean(Constants.PLAN_SELECTED + "_" + AppSession.getInstance(this).getValue(Constants.RELAILER_ID), false);

        // If the user has a paid plan, do not show the popup.
        if (isPopupShown || hasPaidPlan) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.free_subscription, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        view.findViewById(R.id.popup_yes).setOnClickListener(v -> {
            dialog.dismiss();

            // Mark the popup as shown
            AppSession.getInstance(DashBoardActivity.this).setBoolean(Constants.POPUP_SHOWN, true); // Using setBoolean instead of setValue for boolean

            // Proceed with showing the HomeFragment
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new HomeFragment(), "HomeFragment")
                    .commit();
        });

        dialog.show();
    }
    private void handleIntent(Intent intent) {
        if (intent == null) return;

        int updatedCart = intent.getIntExtra("CART_BADGE_COUNT", -1);
        int updatedUrgent = intent.getIntExtra("URGENT_BADGE_COUNT", -1);
        boolean showOrder = intent.getBooleanExtra("SHOW_ORDER_FRAGMENT", false);
        boolean showProfile = intent.getBooleanExtra("SHOW_PROFILE_FRAGMENT", false);
        boolean showHome = intent.getBooleanExtra("SHOW_HOME_FRAGMENT", false);

        if (updatedCart != -1) updateBadgeCounter(updatedCart);
        if (updatedUrgent != -1) updateUrgentBadge(updatedUrgent);

        if (showOrder) {
            switchFragment(new OrderFragment(), "OrderFragment");
            binding.bottomNavigation.setSelectedItemId(R.id.order);
        } else if (showProfile) {
            switchFragment(new ProfileFragment(), "ProfileFragment");
            binding.bottomNavigation.setSelectedItemId(R.id.profile);
        } else if (showHome) {
            switchFragment(new HomeFragment(), "HomeFragment");
            binding.bottomNavigation.setSelectedItemId(R.id.home);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent); // reuse same logic for consistency
    }
}






