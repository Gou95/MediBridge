package com.indosoft.GoodBooks.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.GoodBooks.Adapter.CardListAdapter;
import com.indosoft.GoodBooks.Model.CardListResponse;
import com.indosoft.GoodBooks.R;
import com.indosoft.GoodBooks.ViewModel.CartViewModel;
import com.indosoft.GoodBooks.databinding.ActivityCartBinding;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;

    CartViewModel cartViewModel;
    CardListAdapter adapter;
    ArrayList<CardListResponse> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.init(this);

        onAttachObservers();



        cartViewModel.getModelData();

    }

    private void onAttachObservers() {
        cartViewModel.getLiveData().observe(this, response -> {
            if (response != null) {
                list.clear();  // Optional: Use this to clear any old data
                list.addAll(response);  // Add the new data to the list



                adapter = new CardListAdapter(this,list);
                binding.recyclerView.setAdapter(adapter);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                Log.d("CartActivity", "Data received and updated in adapter.");
            } else {
                // Handle error or empty response case
                Log.d("CartActivity", "No data received or data was empty");
            }
        });
    }

}