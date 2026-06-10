package com.mtganalytics.lab.svc;

import org.springframework.stereotype.Service;

import com.mtganalytics.lab.model.GameEntry;

@Service
public class GameService {
    public GameEntry getGameEntryById(Integer id) {
        // Placeholder implementation
        GameEntry gameEntry = new GameEntry();
        gameEntry.setPlayer("John Doe");
        gameEntry.setCommander("Atraxa, Praetors' Voice");
        gameEntry.setColorIdentity("Green, White, Blue, Black");
        gameEntry.setResult("Win");
        gameEntry.setNumberOfTurnsPlayed(10);
        return gameEntry;
    }
}
