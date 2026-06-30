package com.mtganalytics.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommanderStats {

    private String commander;
    private int totalGames;
    private int wins;
    private int losses;
    private double winRate;
    private double averageTurns;

    public CommanderStats(String commander, long totalGames, long wins, long losses, double winRate,
            double averageTurns) {
        this.commander = commander;
        this.totalGames = (int) totalGames;
        this.wins = (int) wins;
        this.losses = (int) losses;
        this.winRate = winRate;
        this.averageTurns = averageTurns;
    }
}