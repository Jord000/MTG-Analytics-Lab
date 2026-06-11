package com.mtganalytics.lab.svc;

import java.io.IOException;
import java.time.Instant;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.mtganalytics.lab.model.CreatedGameEntry;
import com.mtganalytics.lab.model.GameEntry;
import com.mtganalytics.lab.model.GameEntryRecord;

import lombok.Data;

@Service
@Data
@ConfigurationProperties(prefix = "gameServiece")
public class GameService {

    private final OpenSearchClient openSearchClient;

    private String mtgGameEntriesIndexName;

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

    public CreatedGameEntry createGameEntry(GameEntry gameEntryRequest) throws IOException {

        GameEntryRecord record = new GameEntryRecord();

        BeanUtils.copyProperties(gameEntryRequest, record);

        record.setCreatedAt(Instant.now());

        try {
            IndexResponse response = openSearchClient.index(i -> i
                    .index(mtgGameEntriesIndexName)
                    // .id(record.getId()) // Let OpenSearch generate the ID
                    .document(record));

            if (response.result() != Result.Created) {
                throw new RuntimeException(
                        "Unexpected index result: " + response.result());
            }

            return new CreatedGameEntry(
                    response.id(),
                    record.getCreatedAt());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
