package com.example.smart_research.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class OllamaAIService implements AIService {

    private final ChatClient chatClient;

    public OllamaAIService(OllamaChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    @Override
    public String summarize(String text, int maxLength) {
        return chatClient.prompt()
                .user("Summarize the following text in a concise way. Keep the summary under " + maxLength + " characters.\n\nText: " + text + "\n\nSummary:")
                .call()
                .content();
    }

    @Override
    public String[] extractKeywords(String text, int maxKeywords) {
        String prompt = String.format("""
        Extract %d keywords from the text below.
        RULES:
        - Return ONLY the keywords separated by commas
        - NO explanations, NO sentences, NO numbering
        - Just words or short phrases separated by commas
        - Example: "artificial intelligence, machine learning, neural networks"
        
        Text: %s
        
        Keywords:""", maxKeywords, text);

        String result = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // Clean up the result
        result = result.replaceAll("(?i)^(keywords?|top|the|are)[:\\s-]*", "") // Remove leading text
                .replaceAll("\n", " ")                                   // Remove newlines
                .trim();

        return result.split(",");
    }


    @Override
    public String ask(String question, String context) {
        return chatClient.prompt()
                .user("Answer the question based ONLY on the following context. If the context doesn't contain the answer, say 'I cannot find this information in the provided content.'\n\nContext: " + context + "\n\nQuestion: " + question + "\n\nAnswer:")
                .call()
                .content();
    }
}