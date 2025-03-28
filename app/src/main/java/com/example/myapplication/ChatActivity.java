package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.Message;
import com.example.myapplication.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
// 你好 介绍一下北京邮电大学 空气的成分
    private RecyclerView recyclerView;
    private EditText etMessage;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private BroadcastReceiver responseReceiver;
    private LocalBroadcastManager localBroadcastManager;

    private static final String TAG = "ChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化组件
        recyclerView = findViewById(R.id.rv_chat);
        etMessage = findViewById(R.id.et_message);
        ImageButton btnSend = findViewById(R.id.btn_send);

        // 设置 RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);

        // 初始化广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        setupResponseReceiver();

        // 发送按钮点击事件
        btnSend.setOnClickListener(v -> {
            String content = etMessage.getText().toString().trim();
            if (!content.isEmpty()) {
                sendMessage(content);
                etMessage.setText("");
            }
        });
    }

    private void setupResponseReceiver() {
        responseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG,"onReceive");
                // 移除加载状态
                if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).getType() == Message.TYPE_LOADING) {
                    messageList.remove(messageList.size() - 1);
                    messageAdapter.notifyItemRemoved(messageList.size());
                }

                if (intent.getBooleanExtra("is_error", false)) {
                    // 处理错误情况
                    String errorMsg = "请求失败，请稍后再试";
                    Log.e(TAG, errorMsg);
                    messageList.add(new Message(errorMsg, Message.TYPE_MODEL));
                } else {
                    Log.i(TAG, "开始接受回复");
                    // 添加AI回复
                    String response = intent.getStringExtra(ChatCompletionService.EXTRA_RESPONSE);
                    Log.i(TAG,"接收到回复：" + response);
                    messageList.add(new Message(response, Message.TYPE_MODEL));
                }

                messageAdapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        };

        // 注册广播接收器
        localBroadcastManager.registerReceiver(
                responseReceiver,
                new IntentFilter(ChatCompletionService.ACTION_RESPONSE)
        );
    }

    // 发送消息
    private void sendMessage(String content) {
        Log.i(TAG, "发送消息：" + content);

        // 添加用户消息
        messageList.add(new Message(content, Message.TYPE_USER));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);

        // 添加加载状态
        messageList.add(new Message("", Message.TYPE_LOADING));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);

        // 启动服务获取AI回复
        Intent serviceIntent = new Intent(this, ChatCompletionService.class);
        serviceIntent.putExtra("user_input", content);
        startService(serviceIntent);
    }

    private String buildPrompt(String content) {
        return "请尽量简短地对下面的消息做出回复，且不要使用加黑加粗、斜体等字体，如果需要列点，使用“1、”或“-”\n" +
                "消息" + content;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册广播接收器
        if (responseReceiver != null) {
            localBroadcastManager.unregisterReceiver(responseReceiver);
        }
    }
}