package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.race.RacePrefCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Represents the mods configuration
 */
@ToString
@Getter
@Builder
@RequiredArgsConstructor
public class RaceInteractionsConfig {

    /**
     * When true, will not affect any vanilla races, but only custom modded ones
     */
    private final boolean customOnly;

    /**
     * When true, will not change any settings already present in custom modded races.
     */
    private final boolean honorCustom;

    /**
     * Contains the weight of each category.
     * The weight influences how much similarity in a certain {@link RacePrefCategory}
     * should affect the liking between races.
     */
    private final Map<RacePrefCategory, Double> racePreferenceWeightMap;

    /**
     * A list of the vanilla game race names.
     * This tells the mod which races are the custom modded ones for
     * {@link RaceInteractionsConfig#customOnly} and {@link RaceInteractionsConfig#honorCustom}
     */
    private final List<String> gameRaces;


}
