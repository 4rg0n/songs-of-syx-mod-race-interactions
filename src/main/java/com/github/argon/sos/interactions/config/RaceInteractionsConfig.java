package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.race.RacePrefCategory;
import init.paths.PATH;
import init.paths.PATHS;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import snake2d.util.file.Json;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.argon.sos.interactions.race.RaceLikingsCalculator.DEFAULT_WEIGHT;

@ToString
@Getter
@Builder
@RequiredArgsConstructor
public class RaceInteractionsConfig {

    public final static String NAME = "RaceInteractions";

    public final static double MIN_WEIGHT = -2d;
    public final static double MAX_WEIGHT = 2d;

    private final static Logger log = Loggers.getLogger(RaceInteractionsConfig.class);

    private final boolean customOnly;

    private final boolean honorCustom;

    private final Map<RacePrefCategory, Double> racePreferenceWeightMap;

    private final List<String> gameRaces;

    public static RaceInteractionsConfig load() {
        PATH configPath = PATHS.INIT().getFolder("config");

        if (!configPath.exists(NAME)) {
            // do not load what's not there
            log.debug("Configuration %s/%s not present using defaults.", configPath.get().toString(), NAME);
            return getDefault();
        }

        Json json;

        try {
            Path loadPath = configPath.get(NAME);
            log.debug("Loading config from %s", NAME, loadPath.toString());
            json = new Json(loadPath);
        }  catch (Exception e) {
            log.error("Could not load %s config", NAME, e);
            return getDefault();
        }

        boolean customOnly = json.bool("CUSTOM_RACE_ONLY", true);
        boolean honorCustom = json.bool("HONOR_CUSTOM_RACE_LIKINGS", true);

        Map<RacePrefCategory, Double> weightMap = new HashMap<>(RacePrefCategory.values().length);
        if (!json.keys().contains("PREFERENCE_WEIGHTS")) {
            weightMap = getDefaultWeights();
        } else {
            Json preferenceWeights = json.json("PREFERENCE_WEIGHTS");

            for (RacePrefCategory prefCategory : RacePrefCategory.values()) {
                double weight;
                if (!preferenceWeights.keys().contains(prefCategory.name())) {
                    weight = DEFAULT_WEIGHT;
                } else {
                    weight = preferenceWeights.d(prefCategory.name(), MIN_WEIGHT, MAX_WEIGHT);
                }

                weightMap.put(prefCategory, weight);
            }
        }

        List<String> gameRaces;
        if (!json.keys().contains("VANILLA_RACES")) {
            gameRaces = getDefaultGameRaces();
        } else {
            gameRaces = Arrays.asList(json.texts("VANILLA_RACES"));
        }

        RaceInteractionsConfig raceInteractionsConfig = RaceInteractionsConfig.builder()
                .racePreferenceWeightMap(weightMap)
                .gameRaces(gameRaces)
                .honorCustom(honorCustom)
                .customOnly(customOnly)
                .build();

        log.debug("Loaded configuration %s from file system", NAME);
        log.trace("Configuration %s", raceInteractionsConfig.toString());

        return raceInteractionsConfig;
    }

    public static RaceInteractionsConfig getDefault() {
        return RaceInteractionsConfig.builder()
                .racePreferenceWeightMap(getDefaultWeights())
                .gameRaces(getDefaultGameRaces())
                .honorCustom(true)
                .customOnly(true)
                .build();
    }

    public static Map<RacePrefCategory, Double> getDefaultWeights() {
        Map<RacePrefCategory, Double> categoryWeightMap = new HashMap<>();
        categoryWeightMap.put(RacePrefCategory.BUILDING, DEFAULT_WEIGHT);
        categoryWeightMap.put(RacePrefCategory.CLIMATE, DEFAULT_WEIGHT);
        categoryWeightMap.put(RacePrefCategory.FOOD, DEFAULT_WEIGHT);
        categoryWeightMap.put(RacePrefCategory.RELIGION, DEFAULT_WEIGHT);
        categoryWeightMap.put(RacePrefCategory.WORK, DEFAULT_WEIGHT);

        return categoryWeightMap;
    }

    public static List<String> getDefaultGameRaces() {
        return Arrays.asList("ARGONOSH", "CANTOR", "CRETONIAN", "DONDORIAN", "GARTHIMI", "HUMAN", "TILAPI");
    }
}
