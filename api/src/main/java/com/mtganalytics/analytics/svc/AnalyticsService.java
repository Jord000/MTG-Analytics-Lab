package com.mtganalytics.analytics.svc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.Hit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.mtganalytics.analytics.model.ColorIdentityStats;
import com.mtganalytics.analytics.model.CommanderStats;
import com.mtganalytics.analytics.model.GameStats;
import com.mtganalytics.analytics.model.PlayerStats;
import com.mtganalytics.common.exception.AnalyticsException;
import com.mtganalytics.common.utils.GameUtils;
import com.mtganalytics.game.model.GameEntryDocument;
import com.mtganalytics.game.model.GameEntryRecord;

import lombok.Data;

@Service
@Data
@ConfigurationProperties(prefix = "game-service")
public class AnalyticsService {

    private String mtgGameEntriesIndexName;

    private final OpenSearchClient openSearchClient;

    public List<CommanderStats> getCommanderAnalytics(String commanderName) throws AnalyticsException {

        try {

            if (commanderName != null && !commanderName.isBlank()) {

                // Aggregate stats for fuzzy-matched commanders in a single query
                SearchResponse<Void> response = openSearchClient.search(s -> s
                        .index(mtgGameEntriesIndexName)
                        .size(0)
                        .query(q -> q
                                .multiMatch(m -> m
                                        .fields("commander")
                                        .query(commanderName)
                                        .fuzziness("AUTO")))
                        .aggregations("commanders", a -> a
                                .terms(t -> t
                                        .field("commander.keyword")
                                        .size(5))
                                .aggregations("wins", agg -> agg
                                        .filter(f -> f
                                                .term(t -> t
                                                        .field("win")
                                                        .value(FieldValue
                                                                .of(true)))))
                                .aggregations("avg_turns", agg -> agg
                                        .avg(avg -> avg
                                                .field("numberOfTurnsPlayed")))),
                        Void.class);

                List<StringTermsBucket> commanders = response.aggregations()
                        .get("commanders")
                        .sterms()
                        .buckets()
                        .array();

                List<CommanderStats> statsList = new ArrayList<>();

                for (StringTermsBucket bucket : commanders) {

                    statsList.add(buildCommanderStats(
                            bucket.key(),
                            bucket.docCount(),
                            bucket.aggregations().get("wins").filter().docCount(),
                            bucket.aggregations().get("avg_turns").avg().value()));
                }

                return statsList;
            } else {

                // All commanders stats query returns 100 values
                SearchResponse<Void> response = openSearchClient.search(s -> s
                        .index(mtgGameEntriesIndexName)
                        .size(0)
                        .aggregations("commanders", a -> a
                                .terms(t -> t
                                        .field("commander.keyword")
                                        .size(100))
                                .aggregations("wins", agg -> agg
                                        .filter(f -> f
                                                .term(t -> t
                                                        .field("win")
                                                        .value(FieldValue
                                                                .of(true)))))
                                .aggregations("avg_turns", agg -> agg
                                        .avg(avg -> avg
                                                .field("numberOfTurnsPlayed")))),
                        Void.class);

                List<StringTermsBucket> commanders = response.aggregations()
                        .get("commanders")
                        .sterms()
                        .buckets()
                        .array();

                List<CommanderStats> statsList = new ArrayList<>();

                for (StringTermsBucket bucket : commanders) {

                    statsList.add(buildCommanderStats(
                            bucket.key(),
                            bucket.docCount(),
                            bucket.aggregations().get("wins").filter().docCount(),
                            bucket.aggregations().get("avg_turns").avg().value()));
                }

                return statsList;
            }

        } catch (OpenSearchException | IOException e) {
            throw new AnalyticsException("Error occurred while fetching commander analytics data" + e.getMessage());
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public List<PlayerStats> getPlayerAnalytics(String playerName) throws AnalyticsException {
        try {

            if (playerName != null && !playerName.isBlank()) {

                // Aggregate stats for fuzzy-matched players in a single query
                SearchResponse<Void> response = openSearchClient.search(s -> s
                        .index(mtgGameEntriesIndexName)
                        .size(0)
                        .query(q -> q
                                .multiMatch(m -> m
                                        .fields("player")
                                        .query(playerName)
                                        .fuzziness("AUTO")))
                        .aggregations("players", a -> a
                                .terms(t -> t
                                        .field("player.keyword")
                                        .size(5))
                                .aggregations("wins", agg -> agg
                                        .filter(f -> f
                                                .term(t -> t
                                                        .field("win")
                                                        .value(FieldValue
                                                                .of(true)))))
                                .aggregations("favourite_commander", agg -> agg
                                        .terms(t -> t
                                                .field("commander.keyword")
                                                .size(1)))),
                        Void.class);

                List<StringTermsBucket> players = response.aggregations()
                        .get("players")
                        .sterms()
                        .buckets()
                        .array();

                List<PlayerStats> statsList = new ArrayList<>();

                for (StringTermsBucket bucket : players) {

                    List<StringTermsBucket> commanderBuckets = bucket.aggregations()
                            .get("favourite_commander")
                            .sterms()
                            .buckets()
                            .array();

                    String mostUsedCommander = commanderBuckets.isEmpty() ? "Unknown"
                            : commanderBuckets.get(0).key();

                    statsList.add(buildPlayerStats(
                            bucket.key(),
                            bucket.docCount(),
                            bucket.aggregations().get("wins").filter().docCount(),
                            mostUsedCommander));
                }

                return statsList;
            } else {

                // All players stats query returns 100 values
                SearchResponse<Void> response = openSearchClient.search(s -> s
                        .index(mtgGameEntriesIndexName)
                        .size(0)
                        .aggregations("players", a -> a
                                .terms(t -> t
                                        .field("player.keyword")
                                        .size(100))
                                .aggregations("wins", agg -> agg
                                        .filter(f -> f
                                                .term(t -> t
                                                        .field("win")
                                                        .value(FieldValue
                                                                .of(true)))))
                                .aggregations("favourite_commander", agg -> agg
                                        .terms(t -> t
                                                .field("commander.keyword")
                                                .size(1)))),
                        Void.class);

                List<StringTermsBucket> players = response.aggregations()
                        .get("players")
                        .sterms()
                        .buckets()
                        .array();

                List<PlayerStats> statsList = new ArrayList<>();

                for (StringTermsBucket bucket : players) {

                    List<StringTermsBucket> commanderBuckets = bucket.aggregations()
                            .get("favourite_commander")
                            .sterms()
                            .buckets()
                            .array();

                    String mostUsedCommander = commanderBuckets.isEmpty() ? "Unknown"
                            : commanderBuckets.get(0).key();

                    statsList.add(buildPlayerStats(
                            bucket.key(),
                            bucket.docCount(),
                            bucket.aggregations().get("wins").filter().docCount(),
                            mostUsedCommander));
                }

                return statsList;
            }

        } catch (OpenSearchException | IOException e) {
            throw new AnalyticsException("Error occurred while fetching player analytics data" + e.getMessage());
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public List<ColorIdentityStats> getColorIdentityAnalytics() throws AnalyticsException {
        try {

            SearchResponse<Void> response = openSearchClient.search(s -> s
                    .index(mtgGameEntriesIndexName)
                    .size(0)
                    .aggregations("color_identity", a -> a
                            .terms(t -> t
                                    .field("colorIdentity")
                                    .size(50))),
                    Void.class);

            List<StringTermsBucket> colorIdentities = response.aggregations()
                    .get("color_identity")
                    .sterms()
                    .buckets()
                    .array();

            List<ColorIdentityStats> statsList = new ArrayList<>();

            for (StringTermsBucket bucket : colorIdentities) {
                statsList.add(new ColorIdentityStats(
                        GameUtils.standardiseColorIdentity(bucket.key()),
                        (int) bucket.docCount()));
            }

            return statsList;

        } catch (OpenSearchException | IOException e) {
            throw new AnalyticsException(
                    "Error occurred while fetching color identity analytics data" + e.getMessage());
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public List<GameStats> getGameStats() throws AnalyticsException {
        try {

            // average turns played
            SearchResponse<Void> avgResponse = openSearchClient.search(s -> s
                    .index(mtgGameEntriesIndexName)
                    .size(0)
                    .aggregations("avg_turns", a -> a
                            .avg(avg -> avg
                                    .field("numberOfTurnsPlayed"))),
                    Void.class);

            Double avgTurnsValue = avgResponse.aggregations()
                    .get("avg_turns")
                    .avg()
                    .value();

            Integer averageTurns = (avgTurnsValue != null && !Double.isNaN(avgTurnsValue))
                    ? (int) Math.round(avgTurnsValue)
                    : 0;

            // game with the shortest number of turns
            SearchResponse<GameEntryDocument> shortestResponse = openSearchClient.search(s -> s
                    .index(mtgGameEntriesIndexName)
                    .size(1)
                    .sort(so -> so
                            .field(f -> f
                                    .field("numberOfTurnsPlayed")
                                    .order(SortOrder.Asc)))
                    .query(q -> q
                            .exists(e -> e
                                    .field("numberOfTurnsPlayed"))),
                    GameEntryDocument.class);

            GameEntryRecord shortestGame = null;
            if (!shortestResponse.hits().hits().isEmpty()) {
                Hit<GameEntryDocument> hit = shortestResponse.hits().hits().get(0);
                shortestGame = new GameEntryRecord(hit.id(), hit.source());
            }

            // game with the longest number of turns
            SearchResponse<GameEntryDocument> longestResponse = openSearchClient.search(s -> s
                    .index(mtgGameEntriesIndexName)
                    .size(1)
                    .sort(so -> so
                            .field(f -> f
                                    .field("numberOfTurnsPlayed")
                                    .order(SortOrder.Desc)))
                    .query(q -> q
                            .exists(e -> e
                                    .field("numberOfTurnsPlayed"))),
                    GameEntryDocument.class);

            GameEntryRecord longestGame = null;
            if (!longestResponse.hits().hits().isEmpty()) {
                Hit<GameEntryDocument> hit = longestResponse.hits().hits().get(0);
                longestGame = new GameEntryRecord(hit.id(), hit.source());
            }

            GameStats gameStats = new GameStats();
            gameStats.setAverageTurns(averageTurns);
            gameStats.setShortestTurnGame(shortestGame);
            gameStats.setLongestTurnGame(longestGame);

            return List.of(gameStats);

        } catch (OpenSearchException | IOException e) {
            throw new AnalyticsException(
                    "Error occurred while fetching game analytics data" + e.getMessage());
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private CommanderStats buildCommanderStats(
            String commander,
            long totalGames,
            long wins,
            Double avgTurnsValue) {

        Double averageTurns = (avgTurnsValue != null && !Double.isNaN(avgTurnsValue)) ? avgTurnsValue : 0.0;
        Integer losses = (int) (totalGames - wins);
        double winRate = totalGames > 0 ? (double) wins / totalGames : 0.0;

        return new CommanderStats(
                commander,
                totalGames,
                wins,
                losses,
                winRate,
                averageTurns);
    }

    private PlayerStats buildPlayerStats(
            String player,
            long totalGames,
            long wins,
            String mostUsedCommander) {

        return new PlayerStats(
                player,
                totalGames,
                wins,
                mostUsedCommander);
    }

}
