package com.harshit.rideshare.controller;

import com.harshit.rideshare.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/driver/{username}/earnings")
    public ResponseEntity<Double> getDriverEarnings(@PathVariable String username) {
        return ResponseEntity.ok(analyticsService.getDriverEarnings(username));
    }

    // Helper endpoint to check aggregation
    @GetMapping("/status-summary")
    public ResponseEntity<Object> getStatusSummary() {
        return ResponseEntity.ok(analyticsService.getRideStatusSummary());
    }
}