package com.example.smart_research.service;

import com.example.smart_research.ai.AIService;
import com.example.smart_research.dto.ContentRequest;
import com.example.smart_research.dto.ContentResponse;
import com.example.smart_research.model.Content;
import com.example.smart_research.repository.ContentRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentFetcherService contentFetcherService;
    private final AIService aiService;
    private final SearchService searchService;

    public ContentService(ContentRepository contentRepository,
                          ContentFetcherService contentFetcherService,
                          AIService aiService,
                          SearchService searchService) {
        this.contentRepository = contentRepository;
        this.contentFetcherService = contentFetcherService;
        this.aiService = aiService;
        this.searchService = searchService;
    }

    @Transactional
    public ContentResponse saveContent(ContentRequest request) {
        // Check if URL already exists
        if (contentRepository.existsByUrl(request.getUrl())) {
            throw new RuntimeException("Content with this URL already exists");
        }

        // Create new content entity
        Content content = new Content(request.getUrl());
        content.setStatus("FETCHING");
        content = contentRepository.save(content);

        // Fetch content from URL
        ContentFetcherService.ContentFetchResult result =
                contentFetcherService.fetchContent(request.getUrl());

        if (result.isSuccess()) {
            content.setTitle(result.getTitle());
            content.setDescription(result.getDescription());
            content.setRawContent(result.getContent());
            content.setStatus("FETCHED");
            content = contentRepository.save(content);

            // Index in Elasticsearch (even without AI results yet)
            searchService.indexContent(content);

            // Process AI in background
            processWithAIBackground(content);

        } else {
            content.setStatus("FAILED");
            System.out.println("error : " + result.getErrorMessage());
            content = contentRepository.save(content);
        }

        return mapToResponse(content);
    }

    @Async
    @Transactional
    public CompletableFuture<Void> processWithAIBackground(Content content) {
        try {
            // Re-fetch the content to get latest state
            Content freshContent = contentRepository.findById(content.getId())
                    .orElseThrow(() -> new RuntimeException("Content not found"));

            String textToProcess = freshContent.getRawContent().substring(0,
                    Math.min(freshContent.getRawContent().length(), 2000));

            // Generate summary
            String summary = aiService.summarize(textToProcess, 500);
            freshContent.setSummary(summary);

            // Extract keywords
            String[] keywords = aiService.extractKeywords(textToProcess, 5);
            String keywordsString = String.join(", ", keywords);

            // Ensure it fits in database (leave margin)
            if (keywordsString.length() > 4500) {
                keywordsString = keywordsString.substring(0, 4500) + "...";
            }
            freshContent.setKeywords(keywordsString);

            freshContent.setStatus("PROCESSED_WITH_AI");
            contentRepository.save(freshContent);

            // Update Elasticsearch with AI results
            searchService.updateContent(freshContent);

            System.out.println("AI processing completed for content ID: " + content.getId());

        } catch (Exception e) {
            System.err.println("AI processing failed for content ID: " + content.getId() + " - " + e.getMessage());
            try {
                Content failedContent = contentRepository.findById(content.getId())
                        .orElse(content);
                failedContent.setStatus("AI_FAILED");
                contentRepository.save(failedContent);
            } catch (Exception ex) {
                System.err.println("Could not update content status: " + ex.getMessage());
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Transactional(readOnly = true)
    public ContentResponse getContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        return mapToResponse(content);
    }

    @Transactional(readOnly = true)
    public List<ContentResponse> getAllContents() {
        List<Content> contents = contentRepository.findAll();
        return contents.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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