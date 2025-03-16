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
import java.util.concurrent.atomic.AtomicInteger;

public class ChatCompletionService extends Service {

    public static final String ACTION_RESPONSE = "com.example.ACTION_RESPONSE";
    public static final String EXTRA_RESPONSE = "response";
    private static final String TAG = "ChatCompletionService";

    private static final AtomicInteger requestIdCounter = new AtomicInteger(0);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int cardIndex = intent.getIntExtra("card_index", -1); // 获取卡片索引

        // 从 Intent 中获取用户输入
        String userInput = intent.getStringExtra("user_input");

        // 记录日志：接收到用户输入
        Log.d(TAG, "Received user input (card_index: " + cardIndex + "): " + userInput);

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

        // 记录日志：发送给大模型的请求内容
        Log.d(TAG, "Sending request to model (cardIndex: " + cardIndex + "): " + userInput);

        // 创建聊天完成请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("ep-20250217155941-2p855") // 替换为你的推理接入点ID
                .messages(chatMessages) // 设置消息列表
                .build();

        new Thread(() -> {
            try {
                arkService.createChatCompletion(chatCompletionRequest)
                        .getChoices()
                        .forEach(choice -> {
                            String response = (String) choice.getMessage().getContent();

                            // 记录日志：接收到大模型的回复
                            Log.d(TAG, "Received model response (card index: " + cardIndex + "): " + response);

                            // 发送广播
                            Intent broadcastIntent = new Intent(ACTION_RESPONSE);
                            broadcastIntent.putExtra(EXTRA_RESPONSE, response);
                            broadcastIntent.putExtra("card_index",cardIndex);
                            sendBroadcast(broadcastIntent);
                        });
            } catch (Exception e) {
                // 记录日志：请求失败
                Log.e(TAG, "Request failed (card index: " + cardIndex + "): " + e.getMessage(), e);

                // 发送错误广播
                Intent errorIntent = new Intent(ACTION_RESPONSE);
                errorIntent.putExtra("is_error", true);
                errorIntent.putExtra("card_index", cardIndex);
                sendBroadcast(errorIntent);
            } finally {
                // 记录日志：关闭服务执行器
                Log.d(TAG, "Shutting down executor for card index: " + cardIndex);
                arkService.shutdownExecutor();
            }
        }).start();

        return START_NOT_STICKY;
    }
}