package com.mtganalytics.game.model;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameEntryDocument {
    String player;
    String commander;
    String colorIdentity;
    Boolean win;
    Integer numberOfTurnsPlayed;
    Instant createdAt;

    public GameEntryDocument(GameEntryRequest request) {
        this.player = request.getPlayer();
        this.commander = request.getCommander();
        this.colorIdentity = request.getColorIdentity();
        this.win = request.getWin();
        this.numberOfTurnsPlayed = request.getNumberOfTurnsPlayed();
        this.createdAt = Instant.now();
    }
}
