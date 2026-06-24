package com.mtganalytics.common.utils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Query;

public class GameUtils {

    private static final List<Character> MTG_COLOR_ORDER = List.of('W', 'U', 'B', 'R', 'G');

    public static Query match(String field, String value) {
        return Query.of(q -> q.match(m -> m
                .field(field)
                .query(FieldValue.of(value))));
    }

    public static Query term(String field, String value) {
        return Query.of(q -> q.term(t -> t
                .field(field)
                .value(FieldValue.of(value))));
    }

    public static Query term(String field, Boolean value) {
        return Query.of(q -> q.term(t -> t
                .field(field)
                .value(FieldValue.of(value))));
    }

    public static Query wildcard(String field, String value) {
        return Query.of(q -> q.wildcard(w -> w
                .field(field)
                .value(value)));
    }

    public static String standardiseColorIdentity(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        return input.chars()
                .mapToObj(c -> (char) c)
                .sorted(Comparator.comparingInt(MTG_COLOR_ORDER::indexOf))
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static Query range(String field, Integer min, Integer max) {
        return Query.of(q -> q.range(range -> {
            range.field(field);

            if (min != null) {
                range.gte(JsonData.of(min));
            }

            if (max != null) {
                range.lte(JsonData.of(max));
            }

            return range;
        }));
    }
}
