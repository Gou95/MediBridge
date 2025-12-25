package com.indosoft.medibridge.Activities;

import static androidx.webkit.internal.ApiHelperForLollipop.getUrl;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
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
import com.indosoft.medibridge.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicyActivity extends AppCompatActivity {
    ActivityPrivacyPolicyBinding binding;
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.swipeRefreshLayout.setOnRefreshListener(() -> binding.webView.reload());

        initClicks();
        initWebView();
    }

    private void initClicks() {
        // binding.webView.loadUrl("https://medibro.in/privacy_notice.html");
        binding.imgBack.setOnClickListener(v -> onBackPressed());
        TextView title = binding.txtPravicy;
        SpannableString spannable = new SpannableString("Privacy Policy");


        int blue = ContextCompat.getColor(this, R.color.blue_light);
        int red = ContextCompat.getColor(this, R.color.orange_dark);
        spannable.setSpan(new ForegroundColorSpan(blue), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(red), 10, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
            binding.webView.loadUrl("https://medibro.in/privacy_notice.html");
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
        super.onDestroy();
    }
}