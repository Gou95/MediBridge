package com.indosoft.MediBridges.Fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.indosoft.MediBridges.Adapter.OrderListAdapter;
import com.indosoft.MediBridges.Model.OrderListResponse;
import com.indosoft.MediBridges.R;
import com.indosoft.MediBridges.Session.AppSession;
import com.indosoft.MediBridges.Session.Constants;
import com.indosoft.MediBridges.ViewModel.OrderListViewModel;
import com.indosoft.MediBridges.databinding.FragmentOrderBinding;

import java.util.ArrayList;


public class OrderFragment extends Fragment {

    FragmentOrderBinding binding;
    OrderListViewModel viewModel;
    OrderListAdapter adapter;
    ArrayList<OrderListResponse> list = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentOrderBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(OrderListViewModel.class);
        viewModel.init(requireContext());

        String retailerId = AppSession.getInstance(getContext()).getValue(Constants.RELAILER_ID);

        viewModel.orderList(retailerId);

        onAttachObserver();

        adapter = new OrderListAdapter(requireContext(), list);
        binding.orderRecyclerview.setAdapter(adapter);
        binding.orderRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        initClicks();

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
        binding.imgBack.setOnClickListener(v -> {
            if (getFragmentManager() != null) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment()) // Replace with your HomeFragment
                        .commit();
            }
        });
    }

    private void onAttachObserver() {
        viewModel.getLiveData().observe(getViewLifecycleOwner(),responses -> {
            if (responses != null){
                list.clear();
                list.addAll(responses);

                for (OrderListResponse response : responses){

                    AppSession.getInstance(getContext()).setValue(Constants.ORDER_NO, response.getOrderNo());
                }

                adapter.notifyDataSetChanged();

            }
        });
    }
}