package com.mtganalytics.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ColorIdentityStats {
    String colorIdentity;
    Integer entries;
}
