package com.example.smart_research.dto;

import java.time.LocalDateTime;

public class ContentResponse {
    private Long id;
    private String url;
    private String title;
    private String summary;
    private String keywords;
    private String status;
    private LocalDateTime createdAt;

    // Constructors
    public ContentResponse() {}

    public ContentResponse(Long id, String url, String title, String status, LocalDateTime createdAt) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
