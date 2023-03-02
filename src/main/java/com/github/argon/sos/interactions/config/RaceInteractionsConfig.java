package com.github.argon.sos.interactions.config;

import lombok.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the mods configuration
 *
 * TODO split into multiple objects for cleaner method APIs?
 */
@ToString
@EqualsAndHashCode
@Getter
@Builder
@RequiredArgsConstructor
public class RaceInteractionsConfig {

    /**
     * Name of the config file
     */
    public final static String FILE_NAME = "RaceInteractions";


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
     * The range in tiles where citizens look for other races nearby
     */
    private final int raceLookRange;

    /**
     * Whether races should get boosted {@link RaceStandingCategory}s by their own race
     */
    private final boolean raceBoostSelf;

    /**
     * For boosting different {@link RaceStandingCategory}s
     * when races are near {@link RaceInteractionsConfig#raceLookRange} other liked races.
     * 0 means disabled.
     */
    private final Map<RaceStandingCategory, Double> raceStandingWeightMap;


    /**
     * A list of the vanilla game race names.
     * This tells the mod which races are the custom modded ones for
     * {@link RaceInteractionsConfig#customOnly} and {@link RaceInteractionsConfig#honorCustom}
     */
    private final List<String> gameRaces;

    public static class Default {
        public final static double DEFAULT_MIN_WEIGHT = -2d;
        public final static double DEFAULT_MAX_WEIGHT = 2d;
        public final static double DEFAULT_PREFERENCE_WEIGHT = 1d;
        public final static double DEFAULT_STANDING_WEIGHT = 0d;
        public final static int DEFAULT_RACE_LOOK_RANGE = 20;
        public final static int DEFAULT_RACE_LOOK_MAX_RANGE = 100;

        public static RaceInteractionsConfig getConfig() {
            return RaceInteractionsConfig.builder()
                    .racePreferenceWeightMap(getPreferencesWeights())
                    .gameRaces(getGameRaces())
                    .raceStandingWeightMap(getStandingsWeights())
                    .raceLookRange(DEFAULT_RACE_LOOK_RANGE)
                    .honorCustom(true)
                    .customOnly(true)
                    .raceBoostSelf(false)
                    .build();
        }

        public static Map<RacePrefCategory, Double> getPreferencesWeights() {
            Map<RacePrefCategory, Double> preferencesWeightMap = new HashMap<>();
            preferencesWeightMap.put(RacePrefCategory.BUILDING, DEFAULT_PREFERENCE_WEIGHT);
            preferencesWeightMap.put(RacePrefCategory.CLIMATE, DEFAULT_PREFERENCE_WEIGHT);
            preferencesWeightMap.put(RacePrefCategory.FOOD, DEFAULT_PREFERENCE_WEIGHT);
            preferencesWeightMap.put(RacePrefCategory.RELIGION, DEFAULT_PREFERENCE_WEIGHT);
            preferencesWeightMap.put(RacePrefCategory.WORK, DEFAULT_PREFERENCE_WEIGHT);

            return preferencesWeightMap;
        }

        public static Map<RaceStandingCategory, Double> getStandingsWeights() {
            Map<RaceStandingCategory, Double> standingsWeightMap = new HashMap<>();
            standingsWeightMap.put(RaceStandingCategory.EXPECTATION, DEFAULT_STANDING_WEIGHT);
            standingsWeightMap.put(RaceStandingCategory.FULFILLMENT, DEFAULT_STANDING_WEIGHT);
            standingsWeightMap.put(RaceStandingCategory.HAPPINESS, DEFAULT_STANDING_WEIGHT);
            standingsWeightMap.put(RaceStandingCategory.LOYALTY, DEFAULT_STANDING_WEIGHT);

            return standingsWeightMap;
        }

        public static List<String> getGameRaces() {
            return Arrays.asList("ARGONOSH", "CANTOR", "CRETONIAN", "DONDORIAN", "GARTHIMI", "HUMAN", "TILAPI");
        }
    }
}
