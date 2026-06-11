package com.mtganalytics.lab.model;

import java.time.Instant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GameEntryRecord extends GameEntry {
    Instant createdAt;
}
