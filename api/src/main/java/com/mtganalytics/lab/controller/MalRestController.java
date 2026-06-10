package com.mtganalytics.lab.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mtganalytics.lab.model.GameEntry;
import com.mtganalytics.lab.svc.GameService;

@RestController
public class MalRestController {

  GameService gameService;

  @GetMapping("/")
  ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("API is up and running!");
  }

  @GetMapping("/game_entry/{id}")
  ResponseEntity<GameEntry> getGameEntryById(Integer id) {
    GameEntry gameEntry = gameService.getGameEntryById(id);
    return ResponseEntity.ok(gameEntry);
  }

}
