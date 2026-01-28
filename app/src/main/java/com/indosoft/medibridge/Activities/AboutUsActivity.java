package com.indosoft.medibridge.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import com.indosoft.medibridge.Services.NetworkCheckService;
import com.indosoft.medibridge.databinding.ActivityAboutUsBinding;

public class AboutUsActivity extends AppCompatActivity {
    ActivityAboutUsBinding binding;
    boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initClicks();
        initWebView();
        startNetworkService();
        binding.swipeRefreshLayout.setOnRefreshListener(() -> binding.webView.reload());
    }
    private void initClicks() {
        // binding.webView.loadUrl("https://medibro.in/privacy_notice.html");
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        TextView title = binding.txtAboutUs;
        SpannableString spannable = new SpannableString("About Us");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 5, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannable);
    }
    private void initWebView() {
        try {
            binding.webView.getSettings().setJavaScriptEnabled(true);
            binding.webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    binding.swipeRefreshLayout.setRefreshing(false); // stop loader when page loads
                }
            });
            binding.webView.loadUrl("https://medibro.in/aboutus.html");
        } catch (Exception e) {
            Toast.makeText(this, "Privacy Policy cannot be displayed.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (binding.webView != null) {
            binding.webView.destroy();
        }
        if (isReceiverRegistered) {
            unregisterReceiver(networkReceiver);
            isReceiverRegistered = false;
        }
        super.onDestroy();
    }
    private void startNetworkService() {
        Intent networkServiceIntent = new Intent(this, NetworkCheckService.class);
        startService(networkServiceIntent);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = cm.getActiveNetwork();
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            } else {
                return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
            }
        }
        return false;
    }

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkConnected()) {
                reloadData();
            }
        }
    };

    private void reloadData() {
        new Handler().postDelayed(() -> {
            // You can refresh some data here if needed
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            isReceiverRegistered = true;
        }
    }


}