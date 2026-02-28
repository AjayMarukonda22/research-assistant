package com.example.smart_research.ai;

public interface AIService {
    String summarize(String text, int maxLength);
    String[] extractKeywords(String text, int maxKeywords);
    String ask(String question, String context);
}