package com.indosoft.mediBridge.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.indosoft.mediBridge.Adapter.ViewPagerAdapter;
import com.indosoft.mediBridge.R;
import com.indosoft.mediBridge.databinding.ActivitySplashBinding;

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
            return; // Stop execution of the rest of the code
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