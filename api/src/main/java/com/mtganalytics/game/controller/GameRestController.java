package com.mtganalytics.game.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.opensearch.client.opensearch._types.mapping.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mtganalytics.common.exception.GameEntryNotFoundException;
import com.mtganalytics.common.exception.GameEntryRecordFailureException;
import com.mtganalytics.game.model.GameEntryDocument;
import com.mtganalytics.game.model.GameEntryRecord;
import com.mtganalytics.game.model.GameEntryRequest;
import com.mtganalytics.game.model.StoredGameEntryReference;
import com.mtganalytics.game.svc.GameCommandService;
import com.mtganalytics.game.svc.GameQueryService;
import com.mtganalytics.game.svc.IndexService;

import lombok.Data;

@RestController
@Data
public class GameRestController {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameRestController.class);

  private final GameQueryService gameQueryService;
  private final GameCommandService gameCommandService;
  private final IndexService indexService;

  @GetMapping("/")
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("API is up and running!");
  }

  @GetMapping("/index_mapping")
  public ResponseEntity<Map<String, Property>> getIndexMapping() throws IOException {
    return ResponseEntity.ok(indexService.getIndexMapping());
  }

  @GetMapping("/game_entry/id/{id}")
  public ResponseEntity<GameEntryRecord> getGameEntryById(@PathVariable String id)
      throws IOException, GameEntryNotFoundException {
    GameEntryRecord gameEntry = gameQueryService.getGameEntryById(id);
    return ResponseEntity.ok(gameEntry);
  }

  @GetMapping("/game_entry")
  public ResponseEntity<List<GameEntryDocument>> getGameEntries(
      @RequestParam(required = false) String player,
      @RequestParam(required = false) Boolean win,
      @RequestParam(required = false) String commander,
      @RequestParam(required = false) String colorIdentity,
      @RequestParam(required = false) String colorContains,
      @RequestParam(required = false) Integer minTurns,
      @RequestParam(required = false) Integer maxTurns) throws IOException {

    List<GameEntryDocument> gameEntries = gameQueryService.getGameEntries(player, win, commander, colorIdentity,
        colorContains, minTurns, maxTurns);

    return ResponseEntity.ok(gameEntries);
  }

  @GetMapping("/game_entry_most_recent")
  public ResponseEntity<List<GameEntryRecord>> getMostRecentGameEntries()
      throws IOException, GameEntryNotFoundException {
    List<GameEntryRecord> gameEntries = gameQueryService.getMostRecentGameEntries();
    return ResponseEntity.ok(gameEntries);
  }

  @PostMapping("/game_entry")
  public ResponseEntity<StoredGameEntryReference> postGameEntry(@RequestBody GameEntryRequest gameEntryRequest)
      throws IOException, GameEntryRecordFailureException {
    LOGGER.info("POST /game_entry  player={} commander={} colors={} win={} turns={}",
        gameEntryRequest.getPlayer(),
        gameEntryRequest.getCommander(),
        gameEntryRequest.getColorIdentity(),
        gameEntryRequest.getWin(),
        gameEntryRequest.getNumberOfTurnsPlayed());
    StoredGameEntryReference createdGameEntry = gameCommandService.createGameEntry(gameEntryRequest);
    return ResponseEntity.status(201).body(createdGameEntry);
  }

}
