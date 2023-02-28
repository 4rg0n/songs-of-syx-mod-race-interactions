package com.github.argon.sos.interactions.config;

import lombok.Getter;

public enum RaceStandingCategory {
    EXPECTATION(0),
    FULFILLMENT(1),
    HAPPINESS(2),
    LOYALTY(3);

    @Getter
    private final int index;

    RaceStandingCategory(int index) {
        this.index = index;
    }

    public static RaceStandingCategory valueOfIndex(int index) {
        for (RaceStandingCategory e : values()) {
            if (e.index == index) {
                return e;
            }
        }
        return null;
    }
}
