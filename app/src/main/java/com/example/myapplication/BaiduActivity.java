package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class BaiduActivity extends AppCompatActivity {

    private static final String TAG = "BaiduLog";
    private WebView myWebView;
    private boolean isFilterEnabled;
    private final Handler responseHandler = new Handler(Looper.getMainLooper());

    // 广播接收器用于接收AI响应
    private final BroadcastReceiver apiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (true) {
                Log.d(TAG, "------------------- Broadcast received -------------------");
                String response = intent.getStringExtra(ChatCompletionService.EXTRA_RESPONSE);
                int cardIndex = intent.getIntExtra("card_index", -1);
                Log.d(TAG, "card index: "+ cardIndex +"will be hide: " + response);
                handleModelResponse(response, cardIndex);
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // 初始化WebView
        myWebView = findViewById(R.id.webview);
        setupWebView();

        // 获取开关状态
        isFilterEnabled = getIntent().getBooleanExtra("switch_state", false);

        // 注册广播接收器
        registerReceiver(apiReceiver,
                new IntentFilter("com.example.ACTION_RESPONSE"), Context.RECEIVER_EXPORTED);
    }

    private void setupWebView() {
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new JavaScriptInterface(), "Android");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (isFilterEnabled) {
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        injectFilterLogic();
                        Toast.makeText(BaiduActivity.this,
                                "智能过滤已启用", Toast.LENGTH_SHORT).show();
                    }, 2000);
                }
            }
        });

        myWebView.loadUrl("https://www.baidu.com/");
    }

    private void injectFilterLogic() {
        String jsCode = "javascript:(function() {" +
                "   let pendingCards = [];" +
                "   function processCard(card, index) {" +
                "       const titleElement = card.querySelector('div.rn-h2, div.rn-h1');" +
                "       const title = titleElement?.textContent.trim() || '';" +
                "       Android.requestModelJudgment(title, index);" +
                "       card.dataset.pending = 'true';" +
                "       card.style.opacity = '1';" +
                "   }" +
                "   function checkNewCards() {" +
                "       const allCards = Array.from(document.querySelectorAll('a.rn-large-tpl, a.rn-tpl, a.rn-tpl1'));" +
                "       allCards.forEach((card, index) => {" +
                "           if (!card.dataset.processed) {" +
                "               card.dataset.processed = 'true';" +
                "               processCard(card, index);" + // 使用全局索引
                "           }" +
                "       });" +
                "   }" +
                "   window.hideCard = function(index) {" +
                "       const allCards = document.querySelectorAll('a.rn-large-tpl, a.rn-tpl2, a.rn-tpl1');" +
                "       if (index < allCards.length) {" +
                "           allCards[index].style.display = 'none';" +
                "       }" +
                "   };" +
                "   const observer = new MutationObserver(checkNewCards);" +
                "   observer.observe(document.body, { childList: true, subtree: true });" +
                "   checkNewCards();" + // 初始处理
                "})();";

        myWebView.evaluateJavascript(jsCode, null);
    }

    private void handleModelResponse(String response, int cardIndex) {
        boolean shouldHide = parseModelResponse(response);
        responseHandler.post(() -> {
            if (shouldHide) {
                myWebView.evaluateJavascript(
                        "hideCard(" + cardIndex + ")", null
                );
            } else {
                // 恢复卡片显示
                myWebView.evaluateJavascript(
                        "var cards = document.querySelectorAll('a.rn-large-tpl, a.rn-tpl2, a.rn-tpl1');" +
                                "if (cards[" + cardIndex + "]) {" +
                                "   cards[" + cardIndex + "].style.opacity = '0.75';" +
                                "   delete cards[" + cardIndex + "].dataset.pending;" +
                                "}",
                        null
                );
            }
        });
    }

    private boolean parseModelResponse(String response) {
        return  response.contains("YES");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(apiReceiver);
    }

    public class JavaScriptInterface {
        private final String[] localKeywords = {"剧", "测试", "样例"};

        @JavascriptInterface
        public void logMessage(String message) {
            Log.d(TAG, "JS Log: " + message);
        }

        @JavascriptInterface
        public void requestModelJudgment(String title, int index) {
            if(preCheck(title)) {
                responseHandler.post(() -> {
                    myWebView.evaluateJavascript("hideCard(" + index + ")", null);
                    Log.d(TAG, "本地过滤" + index + title);
                });
                return;
            }

            Intent serviceIntent = new Intent(BaiduActivity.this,
                    ChatCompletionService.class);
            serviceIntent.putExtra("user_input", buildPrompt(title));
            serviceIntent.putExtra("card_index", index);
            startService(serviceIntent);
        }

        private boolean preCheck(String title) {
            for (String keyword : localKeywords) {
                if (title.contains(keyword)) return true;
            }
            return false;
        }

        private String buildPrompt(String title) {
            return "请判断该标题的视频是否与戏剧有关（仅返回YES/NO）：\n" +
                    "标题：" + title + "\n";
        }
    }
}