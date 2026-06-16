package com.mtganalytics.lab.model;

import java.time.Instant;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class StoredGameEntryReference {
    private String id;
    private Instant createdAt;
}
