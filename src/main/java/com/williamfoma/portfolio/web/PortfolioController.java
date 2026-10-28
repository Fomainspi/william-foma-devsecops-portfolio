package com.williamfoma.portfolio.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class PortfolioController {
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name", "William Foma");
        response.put("headline", "DevOps / DevSecOps Engineer");
        response.put("purpose", "Professional portfolio and reproducible DevSecOps delivery lab");
        response.put("message", "Welcome to William Foma's DevSecOps portfolio API.");
        return response;
    }

    @GetMapping("/actuator-health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
