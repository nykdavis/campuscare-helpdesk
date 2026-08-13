package com.campuscare.helpdesk.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/api/welcome")
    public Map<String, String> welcome() {
        return Map.of(
                "application", "CampusCare Helpdesk API",
                "message", "Spring Boot backend is running",
                "status", "UP");
    }
}
