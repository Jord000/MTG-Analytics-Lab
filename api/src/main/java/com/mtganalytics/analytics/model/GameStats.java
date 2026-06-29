package com.mtganalytics.analytics.model;

import com.mtganalytics.game.model.GameEntryRecord;

import lombok.Data;

@Data
public class GameStats {
    Integer averageTurns;
    GameEntryRecord shortestTurnGame;
    GameEntryRecord longestTurnGame;
}
