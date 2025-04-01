package com.indosoft.medibridge.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.indosoft.medibridge.Adapter.ViewPagerAdapter;
import com.indosoft.medibridge.R;
import com.indosoft.medibridge.Session.AppSession;
import com.indosoft.medibridge.Session.Constants;
import com.indosoft.medibridge.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    private ViewPagerAdapter viewPagerAdapter;
    private int currentPage;
    private TextView[] dots;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        boolean isOnboardingCompleted = sharedPreferences.getBoolean("isOnboardingCompleted", false);

        if (isOnboardingCompleted) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
         //   checkUserSession();
            return;
        }

        viewPagerAdapter = new ViewPagerAdapter(this);
        binding.slideViewPager.setAdapter(viewPagerAdapter);
        addDotsIndicator(0);

        binding.previousButton.setOnClickListener(v -> {
            binding.slideViewPager.setCurrentItem(currentPage - 1);
        });

        binding.nextButton.setOnClickListener(v -> {
            if (currentPage < dots.length - 1) {
                binding.slideViewPager.setCurrentItem(currentPage + 1);
            } else {
                // When it's the last page, navigate to another activity or close the splash
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isOnboardingCompleted", true);
                editor.apply();

                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });

        binding.slideViewPager.addOnPageChangeListener(viewListener);
       logFCM();
    }
//    private void checkUserSession() {
//        AppSession session = AppSession.getInstance(this);
//        String retailerId = session.getValue(Constants.RELAILER_ID);
//        String status = session.getValue(Constants.RELAILER_STATUS);
//
//        Log.i("SESSION_DEBUG", "Retailer ID: " + retailerId);
//        Log.i("SESSION_DEBUG", "Retailer Status: " + status);
//
//        if (retailerId != null && !retailerId.isEmpty() && "Active".equalsIgnoreCase(status)) {
//            navigateToDashboard();
//        } else {
//            navigateToLogin();
//        }
//    }
//
//
//    private void navigateToDashboard() {
//        Intent intent = new Intent(this, DashBoardActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
//
//    private void navigateToLogin() {
//        Intent intent = new Intent(this, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
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
                        AppSession.getInstance(SplashActivity.this).setValue(Constants.FCM_TOKEN,token);
                    }
                });


    }
    private void addDotsIndicator(int position) {
        dots = new TextView[3];  // Adjust based on the number of pages
        binding.dots.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorGray));
            binding.dots.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }
    private ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            currentPage = position;

            if (position == 0) {
                // First page: Hide "Previous", show "Next"
                binding.previousButton.setVisibility(View.INVISIBLE);
                binding.nextButton.setText(getResources().getText(R.string.next));
            } else if (position == dots.length - 1) {
                // Last page: Hide "Next" and show "Finish"
                binding.previousButton.setVisibility(View.VISIBLE);
                binding.nextButton.setText(getResources().getText(R.string.finish));
            } else {
                // Middle pages: Show both "Previous" and "Next"
                binding.previousButton.setVisibility(View.VISIBLE);
                binding.nextButton.setText(getResources().getText(R.string.next));
            }


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}