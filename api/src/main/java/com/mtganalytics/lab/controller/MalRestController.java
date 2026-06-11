package com.mtganalytics.lab.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mtganalytics.lab.model.CreatedGameEntry;
import com.mtganalytics.lab.model.GameEntry;
import com.mtganalytics.lab.svc.GameService;

import lombok.Data;

@RestController
@Data
public class MalRestController {

  private final GameService gameService;

  @GetMapping("/")
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("API is up and running!");
  }

  @GetMapping("/game_entry/{id}")
  public ResponseEntity<GameEntry> getGameEntryById(Integer id) {
    GameEntry gameEntry = gameService.getGameEntryById(id);
    return ResponseEntity.ok(gameEntry);
  }

  @PostMapping("/game_entry")
  public ResponseEntity<CreatedGameEntry> postGameEntry(@RequestBody GameEntry gameEntryRequest) throws IOException {
    CreatedGameEntry createdGameEntry = gameService.createGameEntry(gameEntryRequest);
    return ResponseEntity.status(201).body(createdGameEntry);
  }

}
