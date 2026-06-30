package com.mtganalytics.game.model;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GameEntryRecord {

    private String id;
    private String player;
    private String commander;
    private String colorIdentity;
    private Boolean win;
    private Integer numberOfTurnsPlayed;
    private Instant createdAt;

    public GameEntryRecord(String id, GameEntryDocument source) {
        this.setId(id);
        this.setPlayer(source.getPlayer());
        this.setCommander(source.getCommander());
        this.setColorIdentity(source.getColorIdentity());
        this.setWin(source.getWin());
        this.setNumberOfTurnsPlayed(source.getNumberOfTurnsPlayed());
        this.setCreatedAt(source.getCreatedAt());
    }
}
