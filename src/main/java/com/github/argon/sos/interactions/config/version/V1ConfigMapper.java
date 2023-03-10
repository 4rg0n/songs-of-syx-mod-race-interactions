package com.github.argon.sos.interactions.config.version;

import com.github.argon.sos.interactions.config.ConfigStore;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RacePrefCategory;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import snake2d.Errors;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.*;

/**
 * For mapping {@link RaceInteractionsConfig} in and from json as also from and into the save file.
 * Takes care of the different storage formats for the config.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class V1ConfigMapper {
    private final static Logger log = Loggers.getLogger(V1ConfigMapper.class);

    @Getter(lazy = true)
    private final static V1ConfigMapper instance = new V1ConfigMapper();

    public FilePutter toSaveGame(FilePutter file, RaceInteractionsConfig config) {
        file.bool(config.isCustomOnly());
        file.bool(config.isHonorCustom());
        file.i(config.getRaceLookRange());

        double[] preferenceWeightsIndexed = new double[RacePrefCategory.values().length];
        config.getRacePreferenceWeights().forEach((category, weight) ->
            preferenceWeightsIndexed[category.getIndex()] = weight
        );
        file.ds(preferenceWeightsIndexed);

        double[] standingWeightsIndexed = new double[RaceStandingCategory.values().length];
        config.getRaceStandingWeights().forEach((category, weight) ->
            standingWeightsIndexed[category.getIndex()] = weight
        );
        file.ds(standingWeightsIndexed);

        String raceBoostToggles = fromRaceBoostToggles(config.getRaceBoostingToggles());
        file.chars(raceBoostToggles);

        return file;
    }

    public RaceInteractionsConfig fromSaveGame(FileGetter file) throws IOException {
        boolean customOnly = file.bool();
        boolean honorCustom = file.bool();
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


        String raceBoostTogglesString = file.chars();
        Map<String, List<String>> raceBoostToggles = toRaceBoostToggles(raceBoostTogglesString);

        RaceInteractionsConfig raceInteractionsConfig = RaceInteractionsConfig.builder()
            .customOnly(customOnly)
            .honorCustom(honorCustom)
            .raceLookRange(raceLookRange)
            .gameRaces(ConfigStore.getInstance().getCurrentConfig()
                .orElse(RaceInteractionsConfig.Default.getConfig())
                .getGameRaces())
            .racePreferenceWeights(preferenceWeightsMap)
            .raceStandingWeights(standingWeightsMap)
            .raceBoostingToggles(raceBoostToggles)
            .build();

        log.trace("From save file %s", raceInteractionsConfig.toString());
        return raceInteractionsConfig;
    }

    public RaceInteractionsConfig fromJson(Json configJson) {
        boolean customOnly = configJson.bool("CUSTOM_RACE_ONLY", true);
        boolean honorCustom = configJson.bool("HONOR_CUSTOM_RACE_LIKINGS", true);
        int raceLookRange = configJson.i("RACE_LOOK_RANGE", 0, 100, DEFAULT_RACE_LOOK_RANGE);

        // PREFERENCE_WEIGHTS
        Map<RacePrefCategory, Double> preferencesWeightMap = new HashMap<>(RacePrefCategory.values().length);
        if (!configJson.keys().contains("PREFERENCE_WEIGHTS")) {
            log.debug("No config for 'PREFERENCE_WEIGHTS' found. Using default.");
            preferencesWeightMap = RaceInteractionsConfig.Default.getPreferencesWeights();
            log.trace("DEFAULT: %s", preferencesWeightMap);
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
                        log.info("Could not read weight for %s using default value %s. Error: %s",
                                prefCategory.name(),
                                DEFAULT_PREFERENCE_WEIGHT,
                                e.getMessage()
                        );
                        log.debug("", e);
                        weight = DEFAULT_PREFERENCE_WEIGHT;
                    }
                }

                preferencesWeightMap.put(prefCategory, weight);
            }
        }

        // STANDING_WEIGHTS
        Map<RaceStandingCategory, Double> standingsWeightMap = new HashMap<>(RaceStandingCategory.values().length);
        if (!configJson.keys().contains("STANDING_WEIGHTS")) {
            log.debug("No config for 'STANDING_WEIGHTS' found. Using default.");
            standingsWeightMap = RaceInteractionsConfig.Default.getStandingsWeights();
            log.trace("DEFAULT: %s", standingsWeightMap);
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
                        log.info("Could not read weight for %s using default value %s. Error: %s",
                                standingCategory.name(),
                                DEFAULT_STANDING_WEIGHT,
                                e.getMessage()
                        );
                        log.debug("", e);
                        weight = DEFAULT_STANDING_WEIGHT;
                    }
                }

                standingsWeightMap.put(standingCategory, weight);
            }
        }

        // VANILLA_RACES
        List<String> gameRaces;
        if (!configJson.keys().contains("VANILLA_RACES")) {
            log.debug("No config for 'VANILLA_RACES' found. Using default.");
            gameRaces = RaceInteractionsConfig.Default.getGameRaces();
            log.trace("DEFAULT: %s", gameRaces);
        } else {
            gameRaces = Arrays.asList(configJson.texts("VANILLA_RACES"));
        }

        // RACE_BOOST_TOGGLES
        Map<String, List<String>> raceBoostToggles = new HashMap<>();
        if (!configJson.keys().contains("RACE_BOOST_TOGGLES")) {
            log.debug("No config for 'RACE_BOOST_TOGGLES' found. Using default.");
            raceBoostToggles = RaceInteractionsConfig.Default.getRaceBoostingToggles();
            log.trace("DEFAULT: %s", raceBoostToggles);
        } else {
            Json raceBoostTogglesJson = configJson.json("RACE_BOOST_TOGGLES");

            for (String raceName : raceBoostTogglesJson.keys()) {
                List<String> enabledRaces = Arrays.asList(raceBoostTogglesJson.texts(raceName));
                raceBoostToggles.put(raceName, enabledRaces);
            }
        }

        RaceInteractionsConfig raceInteractionsConfig = RaceInteractionsConfig.builder()
            .racePreferenceWeights(preferencesWeightMap)
            .raceStandingWeights(standingsWeightMap)
            .raceBoostingToggles(raceBoostToggles)
            .raceLookRange(raceLookRange)
            .gameRaces(gameRaces)
            .honorCustom(honorCustom)
            .customOnly(customOnly)
            .build();

        log.trace("From json %s", raceInteractionsConfig.toString());
        return raceInteractionsConfig;
    }

    public JsonE toJson(RaceInteractionsConfig config) {
        JsonE json = new JsonE();
        log.debug("Mapping config to json");
        log.trace("Config: %s", config.toString());

        // PREFERENCE_WEIGHTS
        JsonE preferenceWeights = new JsonE();
        config.getRacePreferenceWeights().forEach((category, weight) -> {
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

        // STANDING_WEIGHTS
        JsonE standingWeights = new JsonE();
        config.getRaceStandingWeights().forEach((category, weight) -> {
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

        // RACE_BOOST_TOGGLES
        JsonE raceBoostToggles = new JsonE();
        config.getRaceBoostingToggles().forEach((raceName, enabledRaces) -> {
            raceBoostToggles.add(raceName, enabledRaces.toArray(new String[0]));
        });

        json.add("PREFERENCE_WEIGHTS", preferenceWeights);
        json.add("STANDING_WEIGHTS", standingWeights);
        json.add("RACE_BOOST_TOGGLES", raceBoostToggles);

        json.add("CUSTOM_RACE_ONLY", config.isCustomOnly());
        json.add("HONOR_CUSTOM_RACE_LIKINGS", config.isHonorCustom());
        json.add("RACE_LOOK_RANGE", config.getRaceLookRange());
        json.addStrings("VANILLA_RACES", config.getGameRaces().toArray(new String[0]));

        return json;
    }

    private String fromRaceBoostToggles(Map<String, List<String>> raceBoostToggles) {
        return raceBoostToggles.entrySet().stream().map(entry -> {
            String raceName = entry.getKey();
            List<String> enabledRaces = entry.getValue();
            String enabledRacesString = String.join(",", enabledRaces);

            return raceName + ":" + enabledRacesString;
        }).collect(Collectors.joining(";"));
    }

    private Map<String, List<String>> toRaceBoostToggles(String raceBoostTogglesString) {
        if (raceBoostTogglesString == null || "".equals(raceBoostTogglesString)) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> raceBoostToggles = new HashMap<>();
        String[] lines = raceBoostTogglesString.split(";");

        for (String line : lines) {
            String[] lineSplit = line.split(":");

            if (lineSplit.length != 2) {
                continue;
            }

            String raceName = lineSplit[0];
            String enabledRacesString = lineSplit[1];

            String[] enabledRacesSplit = enabledRacesString.split(",");
            List<String> enabledRaces = Arrays.asList(enabledRacesSplit);

            raceBoostToggles.put(raceName, enabledRaces);
        }

        return raceBoostToggles;
    }
}
