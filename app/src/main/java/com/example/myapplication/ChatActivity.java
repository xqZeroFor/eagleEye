package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.Message;
import com.example.myapplication.adapter.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etMessage;
    private Button btnSend;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

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
        btnSend = findViewById(R.id.btn_send);

        // 设置 RecyclerView
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(messageAdapter);

        // 发送按钮点击事件
        btnSend.setOnClickListener(v -> {
            String content = etMessage.getText().toString().trim();
            if (!content.isEmpty()) {
                sendMessage(content);
                etMessage.setText("");
            }
        });
    }

    // 发送消息
    private void sendMessage(String content) {
        // 添加用户消息到列表
        Message userMessage = new Message(content, Message.TYPE_USER);
        messageList.add(userMessage);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size() - 1);

        // 模拟接收大模型回复（需要在此处调用实际的大模型接口）
        receiveModelResponse(content);
    }

    // 接收大模型的回复
    private void receiveModelResponse(String userInput) {
        // 这里可以调用大模型的接口，将 userInput 发送给大模型，得到回复
        // 为了演示，这里使用固定的回复

        // 模拟网络请求的延迟
        new Handler().postDelayed(() -> {
            String modelResponse = "大模型的回复：" + userInput; // 替换为实际的模型回复
            Message modelMessage = new Message(modelResponse, Message.TYPE_MODEL);
            messageList.add(modelMessage);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
        }, 1000);
    }
}