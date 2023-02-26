package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.Mapper;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.race.RacePrefCategory;
import init.paths.PATH;
import init.paths.PATHS;
import snake2d.Errors;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.nio.file.Path;
import java.util.*;

import static com.github.argon.sos.interactions.race.RaceLikingsCalculator.DEFAULT_WEIGHT;

public class ConfigUtil {
    private final static Logger log = Loggers.getLogger(ConfigUtil.class);

    public final static String NAME = "RaceInteractions";

    public final static double MIN_WEIGHT = -2d;
    public final static double MAX_WEIGHT = 2d;

    private static RaceInteractionsConfig MOD_CONFIG;

    /**
     * Loads mod configuration once.
     *
     * @return already loaded config
     */
    public static RaceInteractionsConfig loadModConfig() {
        if (MOD_CONFIG == null) {
            PATH configPath = PATHS.INIT().getFolder("config");
            MOD_CONFIG = load(configPath).orElseGet(ConfigUtil::getDefault);
        }

        return MOD_CONFIG;
    }

    /**
     * Loads mod configuration from profile once.
     *
     * @return already loaded config
     */
    public static Optional<RaceInteractionsConfig> loadProfileConfig() {
        PATH profiePath = PATHS.local().PROFILE;
        return load(profiePath);
    }

    public static void saveProfileConfig(RaceInteractionsConfig config) {
        PATH profiePath = PATHS.local().PROFILE;

        log.debug("Saving configuration into profile %s", profiePath.get().toString());

        try {
            JsonE configJson = Mapper.toJson(config);

            // blueprint save file exists?
            Path path;
            if (!profiePath.exists(NAME)) {
                path = profiePath.create(NAME);
                log.debug("Created new configuration profile file %s", path.toString());
            } else {
                path = profiePath.get(NAME);
            }

            boolean success = configJson.save(path);

            log.debug("Saving to %s was successful? %s", path.toString(), success);
        } catch (Errors.DataError e) {
            log.warn("Could not save configuration into profile: %s", e.getMessage());
        } catch (Exception e) {
            log.error("Could not save configuration into profile", e);
        }
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

    private static Optional<RaceInteractionsConfig> load(PATH path) {
        if (!path.exists(NAME)) {
            // do not load what's not there
            log.debug("Configuration %s/%s not present using defaults.", path.get().toString(), NAME);
            return Optional.empty();
        }

        Json json;

        try {
            Path loadPath = path.get(NAME);
            log.debug("Loading config from %s", NAME, loadPath.toString());
            json = new Json(loadPath);
        }  catch (Exception e) {
            log.error("Could not load %s config", NAME, e);
            return Optional.empty();
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

        MOD_CONFIG = raceInteractionsConfig;
        return Optional.of(MOD_CONFIG);
    }
}
