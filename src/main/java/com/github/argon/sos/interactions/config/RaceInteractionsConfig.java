package com.github.argon.sos.interactions.config;

import lombok.*;

import java.util.*;

/**
 * Represents the mods configuration
 */
@ToString
@Getter
@Builder
@RequiredArgsConstructor
public class RaceInteractionsConfig {

    /**
     * Name of the config file
     */
    public final static String FILE_NAME = "RaceInteractions";

    /**
     * Holds the current set configuration.
     * Used e.g. when the game saves.
     */
    private static RaceInteractionsConfig CURRENT_CONFIG;

    /**
     * @return currently set up configuration
     */
    public static Optional<RaceInteractionsConfig> getCurrent() {
        return Optional.ofNullable(CURRENT_CONFIG);
    }

    public static void setCurrent(RaceInteractionsConfig currentConfig) {
        CURRENT_CONFIG = currentConfig;
    }

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
     * 0 means disabled
     */
    private final Map<RaceStandingCategory, Double> raceStandingWeightMap;

    /**
     * A list of the vanilla game race names.
     * This tells the mod which races are the custom modded ones for
     * {@link RaceInteractionsConfig#customOnly} and {@link RaceInteractionsConfig#honorCustom}
     */
    private final List<String> gameRaces;

    public static class Default {
        public final static double MIN_WEIGHT = -2d;
        public final static double MAX_WEIGHT = 2d;
        public final static double DEFAULT_PREFERENCE_WEIGHT = 1d;
        public final static double DEFAULT_STANDING_WEIGHT = 0d;

        public static RaceInteractionsConfig getConfig() {
            return RaceInteractionsConfig.builder()
                    .racePreferenceWeightMap(getPreferencesWeights())
                    .gameRaces(getGameRaces())
                    .raceStandingWeightMap(getStandingsWeights())
                    .honorCustom(true)
                    .customOnly(true)
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
