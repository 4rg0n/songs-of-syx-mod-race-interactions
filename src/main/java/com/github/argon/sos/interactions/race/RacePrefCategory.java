package com.github.argon.sos.interactions.race;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;

/**
 * Represents races preference category.
 * Each category can have a certain weight assigned to it.
 * See {@link RaceInteractionsConfig#getRacePreferenceWeightMap()}
 */
public enum RacePrefCategory {
    FOOD,
    CLIMATE,
    BUILDING,
    RELIGION,
    WORK
}
