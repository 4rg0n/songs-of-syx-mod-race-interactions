package com.github.argon.sos.interactions.config;

import lombok.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the mods configuration
 *
 * TODO split into multiple objects for cleaner method APIs?
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaceInteractionsConfig {

    /**
     * Version identification of the config
     * Used for migrating older configs into the current version
     */
    @Builder.Default
    private int version = Default.VERSION;


    /**
     * When true, will not affect any vanilla races, but only custom modded ones
     */
    private boolean customRaceOnly;

    /**
     * When true, will not change any settings already present in custom modded races
     */
    private boolean honorCustomRaceLikings;

    /**
     * Contains the weight of each preference category.
     * The weight influences how much similarity in a certain {@link RacePrefCategory}
     * should affect the liking between races.
     */
    private Map<RacePrefCategory, Double> preferenceWeights;

    /**
     * The range in tiles where citizens look for other races nearby
     */
    private int raceLookRange;


    /**
     * For boosting different {@link RaceStandingCategory}s
     * when races are near {@link RaceInteractionsConfig#raceLookRange} other liked races.
     * 0 means disabled.
     */
    private Map<RaceStandingCategory, Double> standingWeights;

    /**
     * Which race should get boosted likings when nearby another race
     */
    private Map<String, List<String>> raceBoostToggles;

    /**
     * A list of the vanilla game race names.
     * This tells the mod which races are the custom modded ones for
     * {@link RaceInteractionsConfig#customRaceOnly} and {@link RaceInteractionsConfig#honorCustomRaceLikings}
     */
    private List<String> vanillaRaces;

    public static class Default {
        public final static int VERSION = 1;

        /**
         * Name of the config file
         */
        public final static String FILE_NAME = "RaceInteractions";

        public final static String FILE_RELATIVE_PATH = "assets/init/config";

        public final static String MOD_FILE_PATH = FILE_RELATIVE_PATH + "/" + FILE_NAME + ".txt";

        public final static double DEFAULT_MIN_WEIGHT = -2d;
        public final static double DEFAULT_MAX_WEIGHT = 2d;
        public final static double DEFAULT_PREFERENCE_WEIGHT = 1d;
        public final static double DEFAULT_STANDING_WEIGHT = 1d;
        public final static int DEFAULT_RACE_LOOK_RANGE = 20;
        public final static int DEFAULT_RACE_LOOK_MAX_RANGE = 100;

        public static RaceInteractionsConfig getConfig() {
            return RaceInteractionsConfig.builder()
                .version(VERSION)
                .preferenceWeights(getPreferencesWeights())
                .vanillaRaces(getVanillaRaces())
                .standingWeights(getStandingsWeights())
                .raceLookRange(DEFAULT_RACE_LOOK_RANGE)
                .raceBoostToggles(getRaceBoostToggles())
                .honorCustomRaceLikings(true)
                .customRaceOnly(true)
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

        public static Map<String, List<String>> getRaceBoostToggles() {
            Map<String, List<String>> raceBoostingToggles = new HashMap<>();

            getVanillaRaces().forEach(raceName -> {
                raceBoostingToggles.put(raceName, getVanillaRaces().stream()
                    .filter(s -> !s.equals(raceName)) // don't use own race
                    .collect(Collectors.toList()));
            });

            return raceBoostingToggles;
        }

        public static List<String> getVanillaRaces() {
            return Arrays.asList("ARGONOSH", "CANTOR", "CRETONIAN", "DONDORIAN", "GARTHIMI", "HUMAN", "TILAPI");
        }
    }
}
