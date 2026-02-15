package com.example.smart_research.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> healthCheck() {
        Map<String, String > responseMap = new LinkedHashMap<>();

        responseMap.put("status", "OK");
        responseMap.put("timeStamp", LocalDate.now().toString());
        responseMap.put("messsage", "Smart Research API is running!");
        return  responseMap;
    }
}
