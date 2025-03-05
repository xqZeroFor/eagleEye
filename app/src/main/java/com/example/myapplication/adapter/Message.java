package com.example.myapplication.adapter;

public class Message {
    public static final int TYPE_USER = 0;
    public static final int TYPE_MODEL = 1;

    private String content;
    private int senderType;

    public Message(String content, int senderType) {
        this.content = content;
        this.senderType = senderType;
    }

    public String getContent() {
        return content;
    }

    public int getSenderType() {
        return senderType;
    }
}