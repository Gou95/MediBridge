package com.indosoft.mediBridge.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.mediBridge.Adapter.CardListAdapter;
import com.indosoft.mediBridge.Model.CardListResponse;
import com.indosoft.mediBridge.Model.ShowCartResponse;
import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.Session.AppSession;
import com.indosoft.mediBridge.Session.Constants;
import com.indosoft.mediBridge.ViewModel.CartViewModel;
import com.indosoft.mediBridge.ViewModel.ShowCartViewModel;
import com.indosoft.mediBridge.databinding.ActivityCartBinding;

import java.util.ArrayList;
import java.util.Collection;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;

    ShowCartViewModel showCartViewModel;
    CardListAdapter adapter;
    ArrayList<ShowCartResponse> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showCartViewModel = new ViewModelProvider(this).get(ShowCartViewModel.class);
        showCartViewModel.init(this);

        onAttachObservers();


        String retailerId = AppSession.getInstance(this).getString(Constants.RELAILER_ID);


        showCartViewModel.getShowPostCartData(retailerId);


    }

    private void onAttachObservers() {
        showCartViewModel.getLiveData().observe(this, response -> {
            if (response != null && !response.isEmpty()) {
                list.clear();
                list.addAll(response);

                if (adapter == null) {
                    adapter = new CardListAdapter(this, list);
                    binding.recyclerView.setAdapter(adapter);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
                } else {

                }

                Log.d("CartActivity", "Cart updated with new data.");
            } else {
                Log.e("CartActivity", "Cart data is empty or null.");
            }
        });
    }


}