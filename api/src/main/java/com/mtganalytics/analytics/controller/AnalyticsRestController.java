package com.mtganalytics.analytics.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mtganalytics.analytics.model.ColorIdentityStats;
import com.mtganalytics.analytics.model.CommanderStats;
import com.mtganalytics.analytics.model.PlayerStats;
import com.mtganalytics.analytics.svc.AnalyticsService;

import lombok.Data;

@RestController
@Data
public class AnalyticsRestController {

    private final AnalyticsService analyticsService;

    @GetMapping("/analytics/commanders")
    public ResponseEntity<List<CommanderStats>> getCommanderAnalytics(@RequestParam(required = false) String commander)
            throws IOException {
        return ResponseEntity.ok(analyticsService.getCommanderAnalytics(commander));
    }

    @GetMapping("/analytics/players")
    public ResponseEntity<List<PlayerStats>> getPlayerAnalytics(@RequestParam(required = false) String playerName)
            throws IOException {
        return ResponseEntity.ok(analyticsService.getPlayerAnalytics(playerName));
    }

    @GetMapping("/analytics/color_identity")
    public ResponseEntity<List<ColorIdentityStats>> getColorIdentityAnalytics()
            throws IOException {
        return ResponseEntity.ok(analyticsService.getColorIdentityAnalytics());
    }

}
