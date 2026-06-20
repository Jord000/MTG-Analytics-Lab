package com.mtganalytics.lab.svc;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch.core.GetResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.mtganalytics.lab.exception.GameEntryNotFoundException;
import com.mtganalytics.lab.exception.GameServiceException;
import com.mtganalytics.lab.model.GameEntryDocument;
import com.mtganalytics.lab.model.GameEntryRecord;

import lombok.Data;

@Service
@Data
@ConfigurationProperties(prefix = "game-service")
public class GameQueryService {

    private final OpenSearchClient openSearchClient;

    private String mtgGameEntriesIndexName;
    private Integer mostRecentEntryAmount;

    public GameEntryRecord getGameEntryById(String id) throws GameEntryNotFoundException, GameServiceException {
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

    public List<GameEntryDocument> getGameEntryByPlayerName(String playerName)
            throws IOException, GameEntryNotFoundException {
        try {
            SearchResponse<GameEntryDocument> response = openSearchClient.search(
                    s -> s
                            .index(mtgGameEntriesIndexName)
                            .query(q -> q
                                    .match(m -> m
                                            .field("player")
                                            .query(v -> v.stringValue(playerName)))),
                    GameEntryDocument.class);

            List<GameEntryDocument> results = response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

            return results;

        } catch (IOException e) {
            throw new GameServiceException("Error retrieving game entry by ID: " + playerName, e);

        }
    }

    public List<GameEntryRecord> getMostRecentGameEntries() throws IOException, GameEntryNotFoundException {
        try {
            SearchResponse<GameEntryDocument> response = openSearchClient.search(s -> s
                    .index(mtgGameEntriesIndexName)
                    .query(q -> q.matchAll(m -> m))
                    .sort(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc)))
                    .size(mostRecentEntryAmount),
                    GameEntryDocument.class);

            if (response.hits().hits().isEmpty()) {
                throw new GameEntryNotFoundException("No results found for most recent game entries");
            }

            return response.hits().hits().stream()
                    .map(hit -> new GameEntryRecord(hit.id(), hit.source()))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new GameServiceException("Error retrieving most recent game entries", e);
        }
    }

}