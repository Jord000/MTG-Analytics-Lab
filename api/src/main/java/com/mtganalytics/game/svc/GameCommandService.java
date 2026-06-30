package com.mtganalytics.game.svc;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.mtganalytics.common.exception.GameEntryRecordFailureException;
import com.mtganalytics.common.utils.GameUtils;
import com.mtganalytics.game.model.GameEntryDocument;
import com.mtganalytics.game.model.GameEntryRequest;
import com.mtganalytics.game.model.StoredGameEntryReference;

import lombok.Data;

@Service
@Data
@ConfigurationProperties(prefix = "game-service")
public class GameCommandService {

    private final OpenSearchClient openSearchClient;

    private String mtgGameEntriesIndexName;
    private Integer mostRecentEntryAmount;

    public StoredGameEntryReference createGameEntry(GameEntryRequest gameEntryRequest)
            throws IOException, GameEntryRecordFailureException {

        GameEntryDocument record = new GameEntryDocument(gameEntryRequest);
        record.setColorIdentity(GameUtils.standardiseColorIdentity(record.getColorIdentity()));

        try {
            IndexResponse response = openSearchClient.index(i -> i
                    .index(mtgGameEntriesIndexName)
                    // .id(record.getId()) // Let OpenSearch generate the ID
                    .document(record));

            if (response.result() != Result.Created) {
                throw new GameEntryRecordFailureException(
                        "Unexpected index result: " + response.result());
            }

            return new StoredGameEntryReference(
                    response.id(),
                    record.getCreatedAt());

        } catch (IOException e) {
            throw new GameEntryRecordFailureException(e.getMessage());
        }
    }

}