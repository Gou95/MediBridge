package com.indosoft.mediBridge.Activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.indosoft.mediBridge.Fragment.HomeFragment;
import com.indosoft.mediBridge.Fragment.OrderFragment;
import com.indosoft.mediBridge.Fragment.ProfileFragment;
import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.databinding.ActivityDashBoardBinding;

import me.ertugrul.lib.OnItemSelectedListener;

public class DashBoardActivity extends AppCompatActivity {

//    R.layout.activity_dash_board

 ActivityDashBoardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
        bottomNavigation();

        binding.flotingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(intent);
        });


    }

    private void bottomNavigation() {

        binding.bottomNavigation.setOnItemSelectListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelect(int i) {
                Log.d(TAG, "onItemSelect: "+i);
                // Update icons based on selected item
                if (i == 0) {

                    Log.d(TAG, "onItemSelect:INSIDE "+i);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();
                }
                else if (i == 2) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new OrderFragment(), "fragmentTag")
                            .commit();
                }
                else if (i == 3) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment(), "fragmentTag")
                            .commit();
                }

            }
        });
    }
}