package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import snake2d.Errors;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.*;

public class ConfigMapper {
    private final static Logger log = Loggers.getLogger(ConfigMapper.class);

    public static FilePutter toSaveGame(FilePutter file, RaceInteractionsConfig config) {
        file.bool(config.isCustomOnly());
        file.bool(config.isHonorCustom());
        file.bool(config.isRaceBoostSelf());
        file.i(config.getRaceLookRange());


        double[] preferenceWeightsIndexed = new double[RacePrefCategory.values().length];
        config.getRacePreferenceWeightMap().forEach((category, weight) ->
            preferenceWeightsIndexed[category.getIndex()] = weight
        );
        file.ds(preferenceWeightsIndexed);

        double[] standingWeightsIndexed = new double[RaceStandingCategory.values().length];
        config.getRaceStandingWeightMap().forEach((category, weight) ->
            standingWeightsIndexed[category.getIndex()] = weight
        );
        file.ds(standingWeightsIndexed);

        return file;
    }

    public static RaceInteractionsConfig fromSaveGame(FileGetter file) throws IOException {
        boolean customOnly = file.bool();
        boolean honorCustom = file.bool();
        boolean raceBoostSelf = file.bool();
        int raceLookRange = file.i();

        double[] preferenceWeightsIndexed = new double[RacePrefCategory.values().length];
        double[] standingWeightsIndexed = new double[RaceStandingCategory.values().length];
        file.ds(preferenceWeightsIndexed);
        file.ds(standingWeightsIndexed);

        Map<RacePrefCategory, Double> preferenceWeightsMap = new HashMap<>(RacePrefCategory.values().length);
        for (int i = 0, weightsIndexedLength = preferenceWeightsIndexed.length; i < weightsIndexedLength; i++) {
            double weight = preferenceWeightsIndexed[i];
            RacePrefCategory category = RacePrefCategory.valueOfIndex(i);
            preferenceWeightsMap.put(category, weight);
        }

        Map<RaceStandingCategory, Double> standingWeightsMap = new HashMap<>(RaceStandingCategory.values().length);
        for (int i = 0, weightsIndexedLength = standingWeightsIndexed.length; i < weightsIndexedLength; i++) {
            double weight = standingWeightsIndexed[i];
            RaceStandingCategory category = RaceStandingCategory.valueOfIndex(i);
            standingWeightsMap.put(category, weight);
        }

        RaceInteractionsConfig raceInteractionsConfig = RaceInteractionsConfig.builder()
                .customOnly(customOnly)
                .honorCustom(honorCustom)
                .raceBoostSelf(raceBoostSelf)
                .raceLookRange(raceLookRange)
                .gameRaces(RaceInteractionsConfig.getCurrent()
                        .orElse(RaceInteractionsConfig.Default.getConfig())
                        .getGameRaces())
                .racePreferenceWeightMap(preferenceWeightsMap)
                .raceStandingWeightMap(standingWeightsMap)
                .build();

        log.trace("From save file %s", raceInteractionsConfig.toString());
        return raceInteractionsConfig;
    }

    public static RaceInteractionsConfig fromJson(Json configJson) {
        boolean customOnly = configJson.bool("CUSTOM_RACE_ONLY", true);
        boolean honorCustom = configJson.bool("HONOR_CUSTOM_RACE_LIKINGS", true);
        int raceLookRange = configJson.i("RACE_LOOK_RANGE", 0, 100, DEFAULT_RACE_LOOK_RANGE);
        boolean raceBoostSelf = configJson.bool("RACE_BOOST_SELF", false);

        // PREFERENCE_WEIGHTS
        Map<RacePrefCategory, Double> preferencesWeightMap = new HashMap<>(RacePrefCategory.values().length);
        if (!configJson.keys().contains("PREFERENCE_WEIGHTS")) {
            preferencesWeightMap = RaceInteractionsConfig.Default.getPreferencesWeights();
        } else {
            Json preferenceWeights = configJson.json("PREFERENCE_WEIGHTS");

            for (RacePrefCategory prefCategory : RacePrefCategory.values()) {
                double weight;
                if (!preferenceWeights.keys().contains(prefCategory.name())) {
                    weight = DEFAULT_PREFERENCE_WEIGHT;
                } else {
                    try {
                        weight = preferenceWeights.d(prefCategory.name(), DEFAULT_MIN_WEIGHT, DEFAULT_MAX_WEIGHT);
                    } catch (Errors.DataError e) {
                        // validation error
                        log.warn("Could not read weight for %s using default value %s. Error: %s",
                                prefCategory.name(),
                                DEFAULT_PREFERENCE_WEIGHT,
                                e.getMessage()
                        );
                        weight = DEFAULT_PREFERENCE_WEIGHT;
                    }
                }

                preferencesWeightMap.put(prefCategory, weight);
            }
        }

        // STANDING_WEIGHTS
        Map<RaceStandingCategory, Double> standingsWeightMap = new HashMap<>(RaceStandingCategory.values().length);
        if (!configJson.keys().contains("STANDING_WEIGHTS")) {
            standingsWeightMap = RaceInteractionsConfig.Default.getStandingsWeights();
        } else {
            Json standingWeights = configJson.json("STANDING_WEIGHTS");

            for (RaceStandingCategory standingCategory : RaceStandingCategory.values()) {
                double weight;
                if (!standingWeights.keys().contains(standingCategory.name())) {
                    weight = DEFAULT_STANDING_WEIGHT;
                } else {
                    try {
                        weight = standingWeights.d(standingCategory.name(), DEFAULT_MIN_WEIGHT, DEFAULT_MAX_WEIGHT);
                    } catch (Errors.DataError e) {
                        // validation error
                        log.warn("Could not read weight for %s using default value %s. Error: %s",
                                standingCategory.name(),
                                DEFAULT_STANDING_WEIGHT,
                                e.getMessage()
                        );
                        weight = DEFAULT_STANDING_WEIGHT;
                    }
                }

                standingsWeightMap.put(standingCategory, weight);
            }
        }

        // VANILLA_RACES
        List<String> gameRaces;
        if (!configJson.keys().contains("VANILLA_RACES")) {
            gameRaces = RaceInteractionsConfig.Default.getGameRaces();
        } else {
            gameRaces = Arrays.asList(configJson.texts("VANILLA_RACES"));
        }

        RaceInteractionsConfig raceInteractionsConfig = RaceInteractionsConfig.builder()
                .racePreferenceWeightMap(preferencesWeightMap)
                .raceStandingWeightMap(standingsWeightMap)
                .raceLookRange(raceLookRange)
                .raceBoostSelf(raceBoostSelf)
                .gameRaces(gameRaces)
                .honorCustom(honorCustom)
                .customOnly(customOnly)
                .build();

        log.trace("From json %s", raceInteractionsConfig.toString());
        return raceInteractionsConfig;
    }

    public static JsonE toJson(RaceInteractionsConfig config) {
        JsonE json = new JsonE();
        log.debug("Mapping config to json");
        log.trace("Config: %s", config.toString());

        JsonE preferenceWeights = new JsonE();
        config.getRacePreferenceWeightMap().forEach((category, weight) -> {
            if (weight < DEFAULT_MIN_WEIGHT || weight > DEFAULT_MAX_WEIGHT) {
                log.warn("Preference weight %s for %s is out of range from %s to %s. Using default %s",
                    weight,
                    category.name(),
                    DEFAULT_MIN_WEIGHT,
                    DEFAULT_MAX_WEIGHT,
                    DEFAULT_PREFERENCE_WEIGHT);
                weight = DEFAULT_PREFERENCE_WEIGHT;
            }
            preferenceWeights.add(category.name(), weight);
        });

        JsonE standingWeights = new JsonE();
        config.getRaceStandingWeightMap().forEach((category, weight) -> {
            if (weight < DEFAULT_MIN_WEIGHT || weight > DEFAULT_MAX_WEIGHT) {
                log.warn("Standing weight %s for %s is out of range from %s to %s. Using default %s",
                        weight,
                        category.name(),
                        DEFAULT_MIN_WEIGHT,
                        DEFAULT_MAX_WEIGHT,
                        DEFAULT_STANDING_WEIGHT);
                weight = DEFAULT_STANDING_WEIGHT;
            }
            standingWeights.add(category.name(), weight);
        });

        json.add("CUSTOM_RACE_ONLY", config.isCustomOnly());
        json.add("HONOR_CUSTOM_RACE_LIKINGS", config.isHonorCustom());
        json.add("RACE_BOOST_SELF", config.isRaceBoostSelf());
        json.add("RACE_LOOK_RANGE", config.getRaceLookRange());
        json.add("PREFERENCE_WEIGHTS", preferenceWeights);
        json.add("STANDING_WEIGHTS", standingWeights);
        json.addStrings("VANILLA_RACES", config.getGameRaces().toArray(new String[0]));

        return json;
    }
}
