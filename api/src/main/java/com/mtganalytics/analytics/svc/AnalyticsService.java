package com.mtganalytics.analytics.svc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.StringTermsAggregate;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import com.mtganalytics.analytics.model.CommanderStats;
import com.mtganalytics.common.exception.AnalyticsException;

import lombok.Data;

@Service
@Data
@ConfigurationProperties(prefix = "game-service")
public class AnalyticsService {

    private String mtgGameEntriesIndexName;

    private final OpenSearchClient openSearchClient;

    public List<CommanderStats> getCommanderAnalytics() throws AnalyticsException {

        try {
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

            StringTermsAggregate commanders = response.aggregations()
                    .get("commanders")
                    .sterms();

            List<CommanderStats> statsList = new ArrayList<>();

            for (StringTermsBucket bucket : commanders.buckets().array()) {
                String commander = bucket.key();
                long totalGames = bucket.docCount();

                Aggregate winsAgg = bucket.aggregations().get("wins");
                long wins = winsAgg.filter().docCount();

                Aggregate avgTurnsAgg = bucket.aggregations().get("avg_turns");
                Double avgTurnsValue = avgTurnsAgg.avg().value();
                double averageTurns = avgTurnsValue != null ? avgTurnsValue : 0.0;

                int losses = (int) (totalGames - wins);
                double winRate = totalGames > 0 ? (double) wins / totalGames : 0.0;

                CommanderStats stats = new CommanderStats(commander, totalGames, wins, losses, winRate, averageTurns);

                statsList.add(stats);
            }

            return statsList;

        } catch (OpenSearchException | IOException e) {
            throw new AnalyticsException("Error occurred while fetching analytics data" + e.getMessage());
        } catch (RuntimeException e) {
            // rethrow runtime exceptions without wrapping twice
            throw e;
        }

    }
}