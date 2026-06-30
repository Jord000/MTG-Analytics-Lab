package com.mtganalytics.common.exception;

public class GameEntryNotFoundException extends RuntimeException {
    public GameEntryNotFoundException(String id) {
        super("Game entry not found: " + id);
    }
}