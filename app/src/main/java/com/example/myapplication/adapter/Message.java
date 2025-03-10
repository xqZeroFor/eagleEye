package com.example.myapplication.adapter;

public class Message {
    public static final int TYPE_USER = 0; // 用户消息
    public static final int TYPE_MODEL = 1; // 模型消息
    public static final int TYPE_LOADING = 2; // 加载状态

    private String content; // 消息内容
    private int type; // 消息类型

    // 构造函数
    public Message(String content, int type) {
        this.content = content;
        this.type = type;
    }

    // 获取消息内容
    public String getContent() {
        return content;
    }

    // 获取消息类型
    public int getType() {
        return type;
    }
}