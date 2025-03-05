package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;

import java.util.ArrayList;
import java.util.List;

public class ChatCompletionService extends Service {

    private static final String TAG = "ChatCompletionService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 从Intent中获取用户输入
        String userInput = intent.getStringExtra("user_input");

        // 创建ArkService实例
        ArkService arkService = ArkService.builder()
                .apiKey("2f3e7709-ddf3-45ea-90f3-712f14b2f6eb") // 替换为你的OpenAI API密钥
                .build();

        // 初始化消息列表
        List<ChatMessage> chatMessages = new ArrayList<>();

        // 创建用户消息
        ChatMessage userMessage = ChatMessage.builder()
                .role(ChatMessageRole.USER) // 设置消息角色为用户
                .content(userInput) // 设置消息内容
                .build();

        // 将用户消息添加到消息列表
        chatMessages.add(userMessage);

        // 创建聊天完成请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("ep-20250217155941-2p855") // 替换为你的推理接入点ID
                .messages(chatMessages) // 设置消息列表
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    arkService.createChatCompletion(chatCompletionRequest)
                            .getChoices()
                            .forEach(choice -> {
                                String response = (String) choice.getMessage().getContent();
                                Log.d(TAG, "Response: " + response);
                                // 在这里处理响应，例如发送广播或更新UI
                            });
                } catch (Exception e) {
                    Log.e(TAG, "请求失败: " + e.getMessage());
                } finally {
                    // 关闭服务执行器
                    arkService.shutdownExecutor();
                }
            }
        }).start();

        return START_NOT_STICKY;
    }
}