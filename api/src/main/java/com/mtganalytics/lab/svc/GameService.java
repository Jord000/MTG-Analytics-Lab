package com.mtganalytics.lab.svc;

import java.io.IOException;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.IndexResponse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.mtganalytics.lab.model.StoredGameEntryReference;
import com.mtganalytics.lab.exception.GameEntryNotFoundException;
import com.mtganalytics.lab.exception.GameEntryRecordFailureException;
import com.mtganalytics.lab.exception.GameServiceException;
import com.mtganalytics.lab.model.GameEntryDocument;
import com.mtganalytics.lab.model.GameEntryRequest;
import com.mtganalytics.lab.model.GameEntryRecord;

import lombok.Data;

@Service
@Data
@ConfigurationProperties(prefix = "game-service")
public class GameService {

    private final OpenSearchClient openSearchClient;

    private String mtgGameEntriesIndexName;

    public GameEntryRecord getGameEntryById(String id) {
        try {
            GetResponse<GameEntryDocument> response = openSearchClient.get(
                    g -> g.index(mtgGameEntriesIndexName).id(id),
                    GameEntryDocument.class);
            if (!response.found()) {
                throw new GameEntryNotFoundException(id);
            }
            // stored game entry with Id from opensearch
            GameEntryRecord result = new GameEntryRecord(response.id(), response.source());

            return result;

        } catch (IOException e) {
            throw new GameServiceException("Error retrieving game entry by ID: " + id, e);

        }
    }

    public StoredGameEntryReference createGameEntry(GameEntryRequest gameEntryRequest) throws IOException {

        GameEntryDocument record = new GameEntryDocument(gameEntryRequest);

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
