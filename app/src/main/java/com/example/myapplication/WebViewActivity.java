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

public class WebViewActivity extends AppCompatActivity {

    private static final String TAG = "WebViewLog";
    private WebView myWebView;
    private boolean isFilterEnabled;
    private final Handler responseHandler = new Handler(Looper.getMainLooper());

    // 广播接收器用于接收AI响应
    private final BroadcastReceiver apiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ChatCompletionService.ACTION_RESPONSE.equals(intent.getAction())) {
                String response = intent.getStringExtra(ChatCompletionService.EXTRA_RESPONSE);
                int cardIndex = intent.getIntExtra("card_index", -1);
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
                new IntentFilter(ChatCompletionService.ACTION_RESPONSE), Context.RECEIVER_NOT_EXPORTED);
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
                        Toast.makeText(WebViewActivity.this,
                                "智能过滤已启用", Toast.LENGTH_SHORT).show();
                    }, 2000);
                }
            }
        });

        myWebView.loadUrl("https://m.bilibili.com/");
    }

    private void injectFilterLogic() {
        String jsCode = "javascript:(function() {" +
                "   let pendingCards = [];" +
                "   function processCard(card, index) {" +
                "       const titleElement = card.querySelector('p.v-card__title');" +
                "       const title = titleElement?.textContent.trim() || '';" +
                "       Android.requestModelJudgment(title, index);" +
                "       card.dataset.pending = 'true';" +
                "       card.style.opacity = '0.5';" +
                "   }" +
                "   function checkNewCards() {" +
                "       const cards = document.querySelectorAll('div.v-card:not([data-processed])');" +
                "       cards.forEach((card, index) => {" +
                "           card.dataset.processed = 'true';" +
                "           processCard(card, index);" +
                "       });" +
                "   }" +
                "   window.hideCard = function(index) {" +
                "       const card = document.querySelector(`div.v-card:nth-of-type(${index + 1})`);" +
                "       if (card) card.style.display = 'none';" +
                "   };" +
                "   const observer = new MutationObserver(checkNewCards);" +
                "   observer.observe(document.body, { childList: true, subtree: true });" +
                "   checkNewCards();" + // 初始检查
                "})();";

        myWebView.evaluateJavascript(jsCode, null);
    }

    private void handleModelResponse(String response, int cardIndex) {
        boolean shouldHide = parseModelResponse(response);
        responseHandler.post(() -> {
            if (shouldHide && cardIndex != -1) {
                myWebView.evaluateJavascript(
                        "hideCard(" + cardIndex + ")", null
                );
            } else {
                // 恢复卡片显示
                myWebView.evaluateJavascript(
                        "var card=document.querySelector('div.v-card:nth-of-type(" +
                                (cardIndex + 1) + ")');" +
                                "if(card){card.style.opacity='1';delete card.dataset.pending;}",
                        null
                );
            }
        });
    }

    private boolean parseModelResponse(String response) {
        // 实际使用时需要根据API返回格式解析
        return response != null && response.contains("YES");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(apiReceiver);
    }

    public class JavaScriptInterface {
        private final String[] localKeywords = {"中国", "违规", "样例"};

        @JavascriptInterface
        public void logMessage(String message) {
            Log.d(TAG, "JS Log: " + message);
        }

        @JavascriptInterface
        public void requestModelJudgment(String title, int index) {
            if (preCheck(title)) {
                handleModelResponse("YES", index);
                return;
            }

            Intent serviceIntent = new Intent(WebViewActivity.this,
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
            return "请判断该标题的视频是否需要过滤（仅返回YES/NO）：\n" +
                    "标题：" + title + "\n" +
                    "规则：包含低俗、敏感或违规内容，或与游戏相关";
        }
    }
}