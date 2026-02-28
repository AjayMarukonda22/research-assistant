package com.example.smart_research.controller;

import com.example.smart_research.dto.ContentRequest;
import com.example.smart_research.dto.ContentResponse;
import com.example.smart_research.service.ContentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping
    public ResponseEntity<?> createContent(@RequestBody ContentRequest request) {
        try {
            ContentResponse response = contentService.saveContent(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContent(@PathVariable Long id) {
        try {
            ContentResponse response = contentService.getContent(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/ask")
    public ResponseEntity<?> askQuestion(@PathVariable Long id, @RequestBody QuestionRequest request) {
        try {
            String answer = contentService.askQuestion(id, request.getQuestion());
            return ResponseEntity.ok(new AnswerResponse(answer));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Inner class for question request
    public static class QuestionRequest {
        private String question;
        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
    }

    // Inner class for answer response
    public static class AnswerResponse {
        private String answer;
        public AnswerResponse(String answer) { this.answer = answer; }
        public String getAnswer() { return answer; }
    }
}
