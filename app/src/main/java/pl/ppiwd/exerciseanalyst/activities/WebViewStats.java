package pl.ppiwd.exerciseanalyst.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import pl.ppiwd.exerciseanalyst.R;
import pl.ppiwd.exerciseanalyst.common.auth.TokenStore;


public class WebViewStats extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_stats);

        initViews();
    }

    private void initViews() {
        WebView wbvStats = findViewById(R.id.wbv_stats);
        setupWebView(wbvStats);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(WebView wbvStats) {
        wbvStats.getSettings().setJavaScriptEnabled(true);
        wbvStats.getSettings().setDomStorageEnabled(true);
        wbvStats.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                String accessToken = TokenStore.getInstance().getAccessToken();
                String js = "Window.localStorage.setItem('ppiwd-access-token'," + accessToken + ");";

                wbvStats.evaluateJavascript(js, null);
            }
        });
        wbvStats.loadUrl("http://pawelkob-002-site10.itempurl.com/");
    }
}