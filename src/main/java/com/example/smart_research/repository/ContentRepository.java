package com.example.smart_research.repository;

import com.example.smart_research.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByStatus(String status);
    boolean existsByUrl(String url);
}
