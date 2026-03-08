package com.example.smart_research.service;

import com.example.smart_research.model.Content;
import com.example.smart_research.model.SearchDocument;
import com.example.smart_research.repository.SearchDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final SearchDocumentRepository searchRepository;
    private final ElasticsearchOperations elasticsearchOperations;  // Fixed variable name and type

    @Autowired
    public SearchService(SearchDocumentRepository searchRepository,
                         ElasticsearchOperations elasticsearchOperations) {  // Fixed parameter type and name
        this.searchRepository = searchRepository;
        this.elasticsearchOperations = elasticsearchOperations;  // Fixed assignment
    }

    // Index a new content item
    public void indexContent(Content content) {
        SearchDocument doc = new SearchDocument(content);
        searchRepository.save(doc);
    }

    // Update existing content in index
    public void updateContent(Content content) {
        SearchDocument doc = new SearchDocument(content);
        searchRepository.save(doc);
    }

    // Remove content from index
    public void removeContent(Long contentId) {
        searchRepository.deleteByContentId(contentId);
    }

    // Simple search across all fields
    public List<SearchDocument> searchAll(String keyword) {
        Criteria criteria = new Criteria("title").contains(keyword)
                .or(new Criteria("summary").contains(keyword))
                .or(new Criteria("keywords").contains(keyword))
                .or(new Criteria("rawContent").contains(keyword));

        CriteriaQuery query = new CriteriaQuery(criteria);
        SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(query, SearchDocument.class);  // Fixed variable name

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    // Advanced search with field targeting
    public List<SearchDocument> advancedSearch(String keyword, String field) {
        switch(field.toLowerCase()) {
            case "title":
                return searchRepository.findByTitleContaining(keyword);
            case "summary":
                return searchRepository.findBySummaryContaining(keyword);
            case "keywords":
                return searchRepository.findByKeywordsContaining(keyword);
            default:
                return searchAll(keyword);
        }
    }
}