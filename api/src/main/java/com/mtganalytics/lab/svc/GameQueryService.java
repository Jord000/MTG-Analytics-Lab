package com.mtganalytics.lab.svc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
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

    public List<GameEntryDocument> getGameEntries(
            String playerName,
            Boolean win,
            String commander) throws IOException {

        try {
            BoolQuery boolQuery = buildBoolQuery(playerName, win, commander);

            SearchResponse<GameEntryDocument> response = openSearchClient.search(
                    s -> s.index(mtgGameEntriesIndexName)
                            .query(q -> q.bool(boolQuery)),
                    GameEntryDocument.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

        } catch (IOException e) {
            throw new GameServiceException(
                    "Error retrieving game entries with filters: playerName="
                            + playerName + ", win=" + win + ", commander=" + commander,
                    e);
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

    private BoolQuery buildBoolQuery(String playerName, Boolean win, String commander) {

        List<Query> mustClauses = new ArrayList<>();

        if (playerName != null && !playerName.isBlank()) {
            mustClauses.add(match("player", playerName));
        }

        if (win != null) {
            mustClauses.add(term("win", win));
        }

        if (commander != null && !commander.isBlank()) {
            mustClauses.add(match("commander", commander));
        }

        return BoolQuery.of(b -> b.must(mustClauses));
    }

    private Query match(String field, String value) {
        return Query.of(q -> q.match(m -> m
                .field(field)
                .query(FieldValue.of(value))));
    }

    private Query term(String field, Boolean value) {
        return Query.of(q -> q.term(t -> t
                .field(field)
                .value(FieldValue.of(value))));
    }

}