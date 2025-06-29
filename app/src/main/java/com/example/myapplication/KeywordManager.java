package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class KeywordManager {
    private static KeywordManager instance;
    private List<String> keywords;

    private KeywordManager() {
        keywords = new ArrayList<>();
        // 添加一些默认关键词
        keywords.add("游戏内容");
        keywords.add("娱乐明星");
        keywords.add("广告");
    }

    public static synchronized KeywordManager getInstance() {
        if (instance == null) {
            instance = new KeywordManager();
        }
        return instance;
    }

    public List<String> getKeywords() {
        return new ArrayList<>(keywords);
    }

    public void setKeywords(List<String> newKeywords) {
        this.keywords = new ArrayList<>(newKeywords);
    }

    public String getPromptPrefix() {
        if (keywords.isEmpty()) {
            return "请判断该标题的视频是否与以下关键词相关（仅返回YES 关键词 或 NO）：\n关键词：无";
        }
        return "请判断该标题的视频是否与以下关键词相关（仅返回YES 关键词 或 NO）：\n关键词：" + String.join("、", keywords);
    }
} 