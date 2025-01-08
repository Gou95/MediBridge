package com.indosoft.MediBridges.Fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.indosoft.MediBridges.Activities.CartActivity;
import com.indosoft.MediBridges.Adapter.CardListAdapter;
import com.indosoft.MediBridges.Model.ShowCartResponse;
import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.ViewModel.DeleteCartViewModel;
import com.indosoft.MediBridges.ViewModel.DeliveryDayViewModel;
import com.indosoft.MediBridges.ViewModel.ProceedOrderViewModel;
import com.indosoft.MediBridges.ViewModel.QuantityChangeViewModel;
import com.indosoft.MediBridges.ViewModel.ShowCartViewModel;
import com.indosoft.MediBridges.ViewModel.UrgentCartViewModel;
import com.indosoft.MediBridges.databinding.FragmentMyCartBinding;

import java.util.ArrayList;


public class MyCartFragment extends Fragment {

    FragmentMyCartBinding binding;

    ShowCartViewModel showCartViewModel;
    CardListAdapter adapter;

    UrgentCartViewModel cartViewModel;
    DeleteCartViewModel deleteCartViewModel;
    ProceedOrderViewModel orderViewModel;
    QuantityChangeViewModel quantityChangeViewModel;
    DeliveryDayViewModel dayViewModel;

    ArrayList<ShowCartResponse> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyCartBinding.inflate(inflater,container, false);


        showCartViewModel = new ViewModelProvider(this).get(ShowCartViewModel.class);
        showCartViewModel.init(getContext());

        cartViewModel = new ViewModelProvider(this).get(UrgentCartViewModel.class);
        cartViewModel.init(getContext());
        deleteCartViewModel = new ViewModelProvider(this).get(DeleteCartViewModel.class);
        deleteCartViewModel.init(getContext());
        quantityChangeViewModel = new ViewModelProvider(this).get(QuantityChangeViewModel.class);
        quantityChangeViewModel.init(getContext());
        dayViewModel = new ViewModelProvider(this).get(DeliveryDayViewModel.class);
        dayViewModel.init(getContext());

        orderViewModel = new ViewModelProvider(this).get(ProceedOrderViewModel.class);  // Ensure this is initialized
        orderViewModel.init(getContext());
        onAttachObservers();
        initClicks();


        binding.recyclerView.setVisibility(View.GONE);
        binding.linearHide.setVisibility(View.VISIBLE);
        binding.relativeTotal.setVisibility(View.GONE);
        binding.btnAddCart.setVisibility(View.GONE);

        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);
        Log.d("TAG", "onCreate: "+retailerId);


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
        showCartViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response != null && !response.isEmpty()) {
                list.clear();
                list.addAll(response);

                if (adapter == null) {
                    adapter = new CardListAdapter(getContext(), cartViewModel, dayViewModel, list);
                    binding.recyclerView.setAdapter(adapter);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    binding.recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
                    binding.relativeTotal.setVisibility(View.VISIBLE); // Show relativeTotal
                    binding.btnAddCart.setVisibility(View.VISIBLE);   // Show Add to Cart button
                } else {
                    adapter.notifyDataSetChanged();
                }


                binding.linearHide.setVisibility(View.GONE);
                Log.d("CartActivity", "Cart updated with new data.");
            } else {
                Log.e("CartActivity", "Cart data is empty or null.");

                binding.recyclerView.setVisibility(View.GONE);
                binding.linearHide.setVisibility(View.VISIBLE);
                binding.relativeTotal.setVisibility(View.GONE);
                binding.btnAddCart.setVisibility(View.GONE);
            }
            checkCartVisibility();
        });

        cartViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                refreshCartData();
            }
        });

        deleteCartViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                refreshCartData();
            } else {
                Toast.makeText(getContext(), "Failed to delete item.", Toast.LENGTH_SHORT).show();
            }
        });

        dayViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showPopup() {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to confirm your order?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (list == null || list.isEmpty()) {
                        Toast.makeText(getContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show();
                        binding.linearHide.setVisibility(View.VISIBLE);
                        binding.relativeTotal.setVisibility(View.GONE);
                        binding.btnAddCart.setVisibility(View.GONE);
                        dialog.dismiss();
                    } else {
                        // Proceed with placing the order
                        fetchLastOrderAndPlaceOrder();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }


    private void fetchLastOrderAndPlaceOrder() {
        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);

        if (orderViewModel != null) {
            Log.d("CartActivity", "Attempting to place order for retailer: " + retailerId);
            orderViewModel.getProccedOrder(retailerId);

            orderViewModel.getLiveData().observe(getViewLifecycleOwner(), response -> {
                if (response != null) {
                    Log.d("CartActivity", "Order successfully placed: " + response.getMessage());
                   // Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                    refreshCartData();

                    navigateToOrderFragment();
                } else {
                    Log.e("CartActivity", "Failed to place order.");
                  //  Toast.makeText(getContext(), "Failed to place order.", Toast.LENGTH_SHORT).show();
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
    }
    private void checkCartVisibility() {
        if (list.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.linearHide.setVisibility(View.VISIBLE);
            binding.relativeTotal.setVisibility(View.GONE);
            binding.btnAddCart.setVisibility(View.GONE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.linearHide.setVisibility(View.GONE);
            binding.relativeTotal.setVisibility(View.VISIBLE);
            binding.btnAddCart.setVisibility(View.VISIBLE);
        }
    }

}