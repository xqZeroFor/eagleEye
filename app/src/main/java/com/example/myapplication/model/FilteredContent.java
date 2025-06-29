package com.example.myapplication.model;

public class FilteredContent {
    private long id;
    private String title;
    private String source;
    private String filterType;
    private String timestamp;

    public FilteredContent(long id, String title, String source, String filterType, String timestamp) {
        this.id = id;
        this.title = title;
        this.source = source;
        this.filterType = filterType;
        this.timestamp = timestamp;
    }

    // Getters
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getSource() { return source; }
    public String getFilterType() { return filterType; }
    public String getTimestamp() { return timestamp; }

    // Setters
    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setSource(String source) { this.source = source; }
    public void setFilterType(String filterType) { this.filterType = filterType; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
} 