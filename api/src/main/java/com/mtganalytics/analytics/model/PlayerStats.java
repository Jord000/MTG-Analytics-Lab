package com.mtganalytics.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerStats {
    String playerName;
    Long gamesPlayed;
    Long wins;
    String mostUsedCommander;

}
