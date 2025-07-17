package com.indosoft.medibridge.Activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.indosoft.medibridge.R;
import com.indosoft.medibridge.databinding.ActivityHelpBinding;

public class HelpActivity extends AppCompatActivity {

    ActivityHelpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initClicks();

        try {
            WebView webView = new WebView(this);
            webView.setWebViewClient(new WebViewClient()); // Keeps it in the app
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true); // Enable JS if needed

            binding.webViewContainer.removeAllViews();
            binding.webViewContainer.addView(webView);


            webView.loadUrl("https://medibro.in/help.html");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "WebView failed to load. Please update WebView from Play Store.", Toast.LENGTH_LONG).show();
            finish();
        }
            binding.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void initClicks() {
        TextView title = binding.txtHelp;
        SpannableString spannable = new SpannableString("MediBro Help");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 8, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);
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