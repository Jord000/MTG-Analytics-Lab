package com.mtganalytics.lab.exception;

public class GameEntryRecordFailureException extends RuntimeException {
    public GameEntryRecordFailureException(String message) {
        super("Error creating record for game: " + message);
    }
}