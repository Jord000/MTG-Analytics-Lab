package com.mtganalytics.lab.model;

import lombok.Data;

@Data
public class GameEntry {
    String player;
    String commander;
    String colorIdentity;
    String result;
    Integer numberOfTurnsPlayed;
}
