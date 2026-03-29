package com.trading.depthcharts.model;

import java.util.Set;

public enum Sport {
    NFL(53, 5, Set.of("QB", "RB", "WR", "TE", "LT", "LG", "C", "RG", "RT", "LWR", "RWR", "K", "P")), 
    NBA(15, 3, Set.of("PG", "SG", "SF", "PF", "C")), 
    MLB(26, 3, Set.of("SP", "RP", "C", "1B", "2B", "3B", "SS", "LF", "CF", "RF", "DH"));

    private final int maxRosterSize;
    private final int maxDepthPerPosition;
    private final Set<String> validPositions;

    Sport(int maxRosterSize, int maxDepthPerPosition, Set<String> validPositions) {
        this.maxRosterSize = maxRosterSize;
        this.maxDepthPerPosition = maxDepthPerPosition;
        this.validPositions = validPositions;
    }

    public int getMaxRosterSize() {
        return maxRosterSize;
    }

    public int getMaxDepthPerPosition() {
        return maxDepthPerPosition;
    }

    public boolean isValidPosition(String position) {
        return validPositions.contains(position);
    }
}
