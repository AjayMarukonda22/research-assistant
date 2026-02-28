package com.example.smart_research.service;

import com.example.smart_research.ai.AIService;
import com.example.smart_research.dto.ContentRequest;
import com.example.smart_research.dto.ContentResponse;
import com.example.smart_research.model.Content;
import com.example.smart_research.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentFetcherService contentFetcherService;
    private final AIService aiService;

    public ContentService(ContentRepository contentRepository,
                          ContentFetcherService contentFetcherService,
                          AIService aiService) {
        this.contentRepository = contentRepository;
        this.contentFetcherService = contentFetcherService;
        this.aiService = aiService;
    }

    @Transactional
    public ContentResponse saveContent(ContentRequest request) {
        // Check if URL already exists
        if (contentRepository.existsByUrl(request.getUrl())) {
            throw new RuntimeException("Content with this URL already exists");
        }

        // Create new content entity
        Content content = new Content(request.getUrl());
        content = contentRepository.save(content);

        // Fetch content from URL
        ContentFetcherService.ContentFetchResult result =
                contentFetcherService.fetchContent(request.getUrl());

        if (result.isSuccess()) {
            content.setTitle(result.getTitle());
            content.setDescription(result.getDescription());
            content.setRawContent(result.getContent());

            // Process with AI
            try {
                // Generate summary (first 2000 chars to save tokens)
                String textToSummarize = result.getContent().substring(0,
                        Math.min(result.getContent().length(), 2000));
                String summary = aiService.summarize(textToSummarize, 500);
                content.setSummary(summary);

                // Extract keywords
                String[] keywords = aiService.extractKeywords(textToSummarize, 5);
                String keywordsString = String.join(", ", keywords);

                // Ensure it fits in database (leave margin)
                if (keywordsString.length() > 4500) {
                    keywordsString = keywordsString.substring(0, 4500) + "...";
                }
                content.setKeywords(keywordsString);

                content.setStatus("PROCESSED_WITH_AI");
            } catch (Exception e) {
                // If AI fails, still mark as processed but without AI
                content.setStatus("PROCESSED_NO_AI");
                content.setSummary("AI processing failed: " + e.getMessage());
            }
        } else {
            content.setStatus("FAILED");
            System.out.println("error : " + result.getErrorMessage());
        }

        content = contentRepository.save(content);

        return mapToResponse(content);
    }

    @Transactional(readOnly = true)
    public ContentResponse getContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        return mapToResponse(content);
    }

    @Transactional(readOnly = true)
    public String askQuestion(Long contentId, String question) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));

        if (content.getRawContent() == null) {
            throw new RuntimeException("No content available for this item");
        }

        // Use first 3000 chars for context
        String context = content.getRawContent().substring(0,
                Math.min(content.getRawContent().length(), 3000));

        return aiService.ask(question, context);
    }

    private ContentResponse mapToResponse(Content content) {
        ContentResponse response = new ContentResponse(
                content.getId(),
                content.getUrl(),
                content.getTitle(),
                content.getStatus(),
                content.getCreatedAt()
        );
        response.setSummary(content.getSummary());
        response.setKeywords(content.getKeywords());
        return response;
    }
}
