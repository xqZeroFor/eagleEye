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
        // 添加用户消息
        messageList.add(new Message(content, Message.TYPE_USER));

        // 添加加载状态
        messageList.add(new Message("", Message.TYPE_LOADING));
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
            String modelResponse = "没问题，希望下面这些笑话能逗你开怀：\n" +
                    "1. 许仙给老婆买了一顶帽子，白娘子戴上之后就死了，因为那是顶鸭（压）舌（蛇）帽。\n" +
                    "2. 海绵宝宝被蟹老板开除了，海绵宝宝含着眼泪说：“蟹老板……” 蟹老板：“不用谢！”\n" +
                    "3. 有一天，麋鹿在森林里迷路了，于是给它的好朋友长颈鹿打电话：“喂？我迷路（麋鹿）啦！” 长颈鹿：“嗯，我长颈鹿啦！”\n" +
                    "4. 一只北极熊闲得无聊，就开始拔自己的毛。一根，两根，三根…… 最后拔得一根不剩，然后它说了句：“好冷啊！”\n" +
                    "5. 有一天，小蚂蚁迷路找不到蚁窝，可着急了，恰好看到它的朋友经过，于是冲过去大喊一声：“姐们儿！你…… 你都如何回蚁窝（回忆我）？” 那朋友一愣，然后反问道：“带…… 带…… 带着笑或是很沉默？”\n" + userInput;
            Message modelMessage = new Message(modelResponse, Message.TYPE_MODEL);
            messageList.add(modelMessage);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
        }, 3000);
    }
}