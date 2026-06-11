package com.mtganalytics.lab.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatedGameEntry {
    String id;
    Instant createdAt;
}
