package com.example.smart_research.dto;

public class ContentRequest {
    private String url;

    public ContentRequest() {}

    public ContentRequest(String url) {
        this.url = url;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
