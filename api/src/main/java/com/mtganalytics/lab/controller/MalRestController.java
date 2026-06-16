package com.mtganalytics.lab.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mtganalytics.lab.model.StoredGameEntryReference;
import com.mtganalytics.lab.model.GameEntryRecord;
import com.mtganalytics.lab.model.GameEntryRequest;
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
  public ResponseEntity<GameEntryRecord> getGameEntryById(@PathVariable String id) {
    GameEntryRecord gameEntry = gameService.getGameEntryById(id);
    return ResponseEntity.ok(gameEntry);
  }

  @PostMapping("/game_entry")
  public ResponseEntity<StoredGameEntryReference> postGameEntry(@RequestBody GameEntryRequest gameEntryRequest)
      throws IOException {
    StoredGameEntryReference createdGameEntry = gameService.createGameEntry(gameEntryRequest);
    return ResponseEntity.status(201).body(createdGameEntry);
  }

}
