package com.example.smart_research.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class ContentFetcherService {

    public ContentFetchResult fetchContent(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            String title = doc.title();
            String description = doc.select("meta[name=description]").attr("content");
            String bodyText = doc.body().text();

            return new ContentFetchResult(title, description, bodyText, true, null);

        } catch (IOException e) {
            return new ContentFetchResult(null, null, null, false, e.getMessage());
        }
    }

    // Inner class for fetch results
    public static class ContentFetchResult {
        private final String title;
        private final String description;
        private final String content;
        private final boolean success;
        private final String errorMessage;

        public ContentFetchResult(String title, String description, String content,
                                  boolean success, String errorMessage) {
            this.title = title;
            this.description = description;
            this.content = content;
            this.success = success;
            this.errorMessage = errorMessage;
        }

        // Getters
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getContent() { return content; }
        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
    }
}