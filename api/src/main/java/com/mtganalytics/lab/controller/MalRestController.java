package com.mtganalytics.lab.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mtganalytics.lab.exception.GameEntryNotFoundException;
import com.mtganalytics.lab.exception.GameEntryRecordFailureException;
import com.mtganalytics.lab.model.GameEntryRecord;
import com.mtganalytics.lab.model.GameEntryRequest;
import com.mtganalytics.lab.model.StoredGameEntryReference;
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
  public ResponseEntity<GameEntryRecord> getGameEntryById(@PathVariable String id)
      throws IOException, GameEntryNotFoundException {
    GameEntryRecord gameEntry = gameService.getGameEntryById(id);
    return ResponseEntity.ok(gameEntry);
  }

  @GetMapping("/game_entry_most_recent")
  public ResponseEntity<List<GameEntryRecord>> getMostRecentGameEntries()
      throws IOException, GameEntryNotFoundException {
    List<GameEntryRecord> gameEntries = gameService.getMostRecentGameEntries();
    return ResponseEntity.ok(gameEntries);
  }

  @PostMapping("/game_entry")
  public ResponseEntity<StoredGameEntryReference> postGameEntry(@RequestBody GameEntryRequest gameEntryRequest)
      throws IOException, GameEntryRecordFailureException {
    StoredGameEntryReference createdGameEntry = gameService.createGameEntry(gameEntryRequest);
    return ResponseEntity.status(201).body(createdGameEntry);
  }

}
