package com.example.myapplication;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class SearchWebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_web);

        WebView myWebView = findViewById(R.id.searchWebView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());

        String url = getIntent().getStringExtra("url");
        if (url != null && !url.isEmpty()) {
            myWebView.loadUrl(url);
        }
    }
}