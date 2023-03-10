package com.github.argon.sos.interactions.config;

import lombok.Getter;

/**
 * Represents races preference category.
 * Each category can have a certain weight assigned to it.
 * See {@link RaceInteractionsConfig#getRacePreferenceWeights()}
 */
public enum RacePrefCategory {
    FOOD(0),
    CLIMATE(1),
    BUILDING(2),
    RELIGION(3),
    WORK(4);

    @Getter
    private final int index;

    RacePrefCategory(int index) {
        this.index = index;
    }

    public static RacePrefCategory valueOfIndex(int index) {
        for (RacePrefCategory e : values()) {
            if (e.index == index) {
                return e;
            }
        }
        return null;
    }
}
