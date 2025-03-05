package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = "WebViewLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();


        WebView myWebView = findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());

        myWebView.addJavascriptInterface(new JavaScriptInterface(), "Android");

        myWebView.loadUrl("https://www.bilibili.com/");

        String jsCode = "javascript:(function() {" +
                "    function hideCards() {" +
                "        var videoCards = document.querySelectorAll('a.v-card');" +
                "        Android.logMessage('Found ' + videoCards.length + ' video cards');" +
                "        for (var i = 0; i < videoCards.length; i++) {" +
                "            var card = videoCards[i];" +
                "            var titleElement = card.querySelector('p.v-card__title');" +
                "            var title = titleElement ? titleElement.textContent.trim() : '';" +
                "            Android.logMessage('Card ' + i + ': ' + title);" +
                "            if (title.toLowerCase().indexOf('1') !== -1) {" +
                "                Android.logMessage('Hiding card with title: ' + title);" +
                "                card.style.display = 'none';" +
                "                titleElement.textContent = '';" +
                "            }" +
                "        }" +
                "    }" +
                "    hideCards();" +
                "    var observer = new MutationObserver(function(mutations) {" +
                "        mutations.forEach(function(mutation) {" +
                "            if (mutation.addedNodes.length > 0) {" +
                "                hideCards();" +
                "            }" +
                "        });" +
                "    });" +
                "    observer.observe(document.body, { childList: true, subtree: true });" +
                "})();";

// 等待页面加载完成后执行JavaScript代码
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // 创建一个Handler来引入延迟
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 在延迟后执行JavaScript代码
                        boolean isSwitchOn = intent.getBooleanExtra("switch_state", false);

                        if(isSwitchOn){
                            myWebView.evaluateJavascript(jsCode, null);
                            Toast.makeText(WebViewActivity.this, "鹰眼保护已开启", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(WebViewActivity.this, "鹰眼保护已关闭", Toast.LENGTH_SHORT).show();

                        }
                    }
                }, 1000); // 延迟1秒（1000毫秒），根据需要调整时间
            }
        });
    }

    // 定义一个 JavaScript 接口类
    public class JavaScriptInterface {
        @JavascriptInterface
        public void logMessage(String message) {
            Log.d(TAG, message);
        }
    }
}

