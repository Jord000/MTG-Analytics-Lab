package com.mtganalytics.game.model;

import lombok.Data;

@Data
public class GameEntryRequest {
    String player;
    String commander;
    String colorIdentity;
    Boolean win;
    Integer numberOfTurnsPlayed;
}
