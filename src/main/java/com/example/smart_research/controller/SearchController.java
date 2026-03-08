package com.example.smart_research.controller;


import com.example.smart_research.model.SearchDocument;
import com.example.smart_research.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<SearchDocument> search(@RequestParam String q) {
        return searchService.searchAll(q);
    }

    @GetMapping("/advanced")
    public List<SearchDocument> advancedSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "all") String field) {
        return searchService.advancedSearch(q, field);
    }
}
