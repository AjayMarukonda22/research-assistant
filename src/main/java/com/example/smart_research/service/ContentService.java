package com.example.smart_research.service;

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

    public ContentService(ContentRepository contentRepository,
                          ContentFetcherService contentFetcherService) {
        this.contentRepository = contentRepository;
        this.contentFetcherService = contentFetcherService;
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

        // Fetch content from URL (in real app, do this asynchronously)
        ContentFetcherService.ContentFetchResult result =
                contentFetcherService.fetchContent(request.getUrl());

        if (result.isSuccess()) {
            content.setTitle(result.getTitle());
            content.setDescription(result.getDescription());
            content.setRawContent(result.getContent());
            content.setStatus("PROCESSED");
        } else {
            content.setStatus("FAILED");
        }

        content = contentRepository.save(content);

        return mapToResponse(content);
    }

    private ContentResponse mapToResponse(Content content) {
        return new ContentResponse(
                content.getId(),
                content.getUrl(),
                content.getTitle(),
                content.getStatus(),
                content.getCreatedAt()
        );
    }
}
