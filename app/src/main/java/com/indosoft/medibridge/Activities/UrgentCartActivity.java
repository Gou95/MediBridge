package com.indosoft.medibridge.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.UrgentCartAdapter;
import com.indosoft.medibridge.Fragment.HomeFragment;
import com.indosoft.medibridge.Fragment.OrderFragment;
import com.indosoft.medibridge.Model.GetUrgentCartResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.GetUrgentCartViewModel;
import com.indosoft.medibridge.ViewModel.SignUpViewModel;
import com.indosoft.medibridge.ViewModel.UrgentDeleteViewModel;
import com.indosoft.medibridge.ViewModel.UrgentProceedViewModel;
import com.indosoft.medibridge.databinding.ActivityUrgentCartBinding;

import java.util.ArrayList;

public class UrgentCartActivity extends AppCompatActivity {

    ActivityUrgentCartBinding binding;
    GetUrgentCartViewModel viewModel;
    UrgentCartAdapter urgentCartAdapter;
    ArrayList<GetUrgentCartResponse> list = new ArrayList<>();
    UrgentDeleteViewModel urgentDeleteViewModel;
    UrgentProceedViewModel urgentProceedViewModel;
    SignUpViewModel sign;

    private int cartCount = 0;
    private boolean isReceiverRegistered = false;
    private int updatedCartCount = 0;
    private int updatedUrgentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUrgentCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(GetUrgentCartViewModel.class);
        viewModel.init(this);
        urgentDeleteViewModel = new ViewModelProvider(this).get(UrgentDeleteViewModel.class);
        urgentDeleteViewModel.init(this);
        sign = new ViewModelProvider(this).get(SignUpViewModel.class);
        sign.init(this);

        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);

        viewModel.getRemoveAllCartData(retailerId);
        onAttachobservers();
        initClicks();
        startNetworkCheckService();
        //handleBackPress();

        binding.recyclerView.setVisibility(View.GONE);
        binding.linearHide.setVisibility(View.VISIBLE);
        binding.linearButtons.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
        binding.swipeRefreshLayout.setOnRefreshListener(this::onAttachobservers);

        urgentCartAdapter = new UrgentCartAdapter(this, list);
        binding.recyclerView.setAdapter(urgentCartAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (retailerId != null && !retailerId.isEmpty()) {
            viewModel.getRemoveAllCartData(retailerId);
        } else {

        }
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
        TextView title = binding.txtUrgent;
        SpannableString spannable = new SpannableString("Urgent Cart");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 7, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);
    }
    private void onAttachobservers() {
        binding.swipeRefreshLayout.setRefreshing(true);
        viewModel.getLiveData().observe(this, responses -> {
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
        sign.getLiveData().observe(this, signUpResponse -> {

            if (signUpResponse != null) {

                Toast.makeText(this, signUpResponse.getMessage(), Toast.LENGTH_SHORT).show();

                // ✅ Cart UI clear
                list.clear();
                urgentCartAdapter.notifyDataSetChanged();

                updateCartUI(false);

                // ✅ Session cart count reset
                AppSession.getInstance(this).setValue(Constants.URGENT_BADGE_COUNT, "0");

                // ✅ Dashboard badge reset
                if (getApplicationContext() instanceof DashBoardActivity) {
                    ((DashBoardActivity) getApplicationContext()).updateUrgentBadge(0);
                }
            }
        });
    }
    private void showPopup() {
        urgentProceedViewModel = new ViewModelProvider(this).get(UrgentProceedViewModel.class);
        urgentProceedViewModel.init(this);

        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                Toast.makeText(this, "Your data is null.", Toast.LENGTH_SHORT).show();
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
        urgentProceedViewModel.getUrgentProceed(retailerId);
        urgentProceedViewModel.getLiveData().observe(this, response -> {
            if (response != null) {
                list.clear();
                urgentCartAdapter.notifyDataSetChanged();
                AppSession.getInstance(getApplicationContext()).setValue(Constants.URGENT_BADGE_COUNT, "0");

                // Step 2: Update badge in DashBoardActivity
                if (getApplicationContext() instanceof DashBoardActivity) {
                    ((DashBoardActivity) getApplicationContext()).updateUrgentBadge(0);
                }

                // Step 3: Redirect to OrderRegisterActivity
                Intent intent = new Intent(UrgentCartActivity.this, OrderRegisterActivity.class);
                startActivity(intent);
                finish();
//                Intent intent = new Intent(UrgentCartActivity.this, DashBoardActivity.class);
//                intent.putExtra("SHOW_ORDER_FRAGMENT", true);
//                intent.putExtra("URGENT_BADGE_COUNT", updatedUrgentCount);
//              //  intent.putExtra("CART_BADGE_COUNT", updatedCartCount);
//                startActivity(intent);
//                finish();

                refreshUrgentCartData();
            } else {
                Toast.makeText(this, "Failed to proceed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshUrgentCartData() {
        String retailerId = AppSession.getInstance(this).getValue(Constants.RELAILER_ID);
        viewModel.getRemoveAllCartData(retailerId);
    }

    public void returnToDashboard() {
        Intent intent = new Intent(UrgentCartActivity.this, DashBoardActivity.class);
        intent.putExtra("SHOW_HOME_FRAGMENT", true);
        intent.putExtra("URGENT_BADGE_COUNT", list.size());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
    private void startNetworkCheckService() {
        Intent serviceIntent = new Intent(this, NetworkCheckService.class);
       startService(serviceIntent);

    }
//    private void handleBackPress() {
//        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, new HomeFragment(), "HomeFragment");
//                transaction.commit();
//            }
//        });
//    }

    private void updateCartUI(boolean isCartNotEmpty) {
        if (isCartNotEmpty) {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.linearHide.setVisibility(View.GONE);
            binding.linearButtons.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.GONE);
            binding.linearHide.setVisibility(View.VISIBLE);
            binding.linearButtons.setVisibility(View.GONE);
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        returnToDashboard(); // replaces super.onBackPressed()
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale > 1.0f) {
            newConfig.fontScale = 1.0f;
            getResources().updateConfiguration(newConfig, getResources().getDisplayMetrics());
        }
        super.onConfigurationChanged(newConfig);
    }
}