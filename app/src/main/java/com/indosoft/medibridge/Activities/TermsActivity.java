package com.indosoft.medibridge.Activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.indosoft.medibridge.R;
import com.indosoft.medibridge.databinding.ActivityTermsBinding;

public class TermsActivity extends AppCompatActivity {

    ActivityTermsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            WebView webView = new WebView(this);
            webView.getSettings().setJavaScriptEnabled(true); // if your privacy page needs JS
            webView.loadUrl("https://medibro.in/terms.html");
            binding.webViewContainer.addView(webView);
        } catch (Exception e) {
            Log.e("TermsActivity", "WebView initialization failed", e);
            Toast.makeText(this, "Terms and  conditions cannot be displayed.", Toast.LENGTH_LONG).show();
            finish(); // or show fallback content
        }
        initClicks();
    }

    private void initClicks() {
//        binding.webView.loadUrl("https://medibro.in/terms.html");
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        TextView title = binding.txtCondition;
        SpannableString spannable = new SpannableString("Terms And Conditions");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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