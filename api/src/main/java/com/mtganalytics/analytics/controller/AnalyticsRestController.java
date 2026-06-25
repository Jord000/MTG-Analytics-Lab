package com.mtganalytics.analytics.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mtganalytics.analytics.model.CommanderStats;
import com.mtganalytics.analytics.svc.AnalyticsService;

import lombok.Data;

@RestController
@Data
public class AnalyticsRestController {

    private final AnalyticsService analyticsService;

    @GetMapping("/analytics/commanders")
    public ResponseEntity<List<CommanderStats>> getCommanderAnalytics() throws IOException {
        return ResponseEntity.ok(analyticsService.getCommanderAnalytics());
    }

}
