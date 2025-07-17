package com.indosoft.medibridge.Activities;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.indosoft.medibridge.Adapter.StockistListAdapter;
import com.indosoft.medibridge.Model.StockistListResponse;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.ViewModel.StockistListViewModel;
import com.indosoft.medibridge.databinding.ActivityStockistBinding;

import java.util.ArrayList;

public class StockistActivity extends AppCompatActivity {

    ActivityStockistBinding binding;
    ArrayList<StockistListResponse> list = new ArrayList<>();
    StockistListViewModel viewModel;
    StockistListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     binding = ActivityStockistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(StockistListViewModel.class);
        viewModel.init(this);
        String cityId = AppSession.getInstance(this).getValue(Constants.CITY_ID);
        viewModel.stockitsList(cityId);
        onAttachObservers();

        adapter = new StockistListAdapter(this,list);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void onAttachObservers() {
        viewModel.getLiveData().observe(this,responses -> {
            if (responses !=null);
            list.clear();
            list.addAll(responses);
            adapter.notifyDataSetChanged();
        });
        TextView title = binding.txtStockist;
        SpannableString spannable = new SpannableString("Stockist List");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 9, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);
    }
}