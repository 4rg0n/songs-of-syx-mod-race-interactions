package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.util.Indexed;
import lombok.Getter;

/**
 * Represents races preference category.
 * Each category can have a certain weight assigned to it.
 * See {@link RaceInteractionsConfig#getPreferenceWeights()}
 */
public enum RacePrefCategory implements Indexed {
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
