package com.example.smart_research.repository;

import com.example.smart_research.model.SearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SearchDocumentRepository extends ElasticsearchRepository<SearchDocument, String> {

    // Custom search methods
    List<SearchDocument> findByTitleContaining(String keyword);
    List<SearchDocument> findBySummaryContaining(String keyword);
    List<SearchDocument> findByKeywordsContaining(String keyword);

    // Delete by content ID
    void deleteByContentId(Long contentId);
}
