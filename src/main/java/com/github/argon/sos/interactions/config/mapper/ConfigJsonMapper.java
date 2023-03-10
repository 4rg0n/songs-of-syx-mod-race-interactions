package com.github.argon.sos.interactions.config.mapper;

import com.github.argon.sos.interactions.config.RacePrefCategory;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.Errors;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.*;

/**
 * For mapping configuration data from and into the games json format
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigJsonMapper {
    private final static Logger log = Loggers.getLogger(ConfigJsonMapper.class);

    @Getter(lazy = true)
    private final static ConfigJsonMapper instance = new ConfigJsonMapper();

    public final static String KEY_CUSTOM_RACE_ONLY = "CUSTOM_RACE_ONLY";
    public final static String KEY_HONOR_CUSTOM_RACE_LIKINGS = "HONOR_CUSTOM_RACE_LIKINGS";
    public final static String KEY_RACE_LOOK_RANGE = "RACE_LOOK_RANGE";
    public final static String KEY_PREFERENCE_WEIGHTS = "PREFERENCE_WEIGHTS";
    public final static String KEY_STANDING_WEIGHTS = "STANDING_WEIGHTS";
    public final static String KEY_VANILLA_RACES = "VANILLA_RACES";
    public final static String KEY_RACE_BOOST_TOGGLES = "RACE_BOOST_TOGGLES";

    public Map<RacePrefCategory, Double> toPreferenceWeights(Json json, Map<RacePrefCategory, Double> defaultWeights) {
        if (!json.keys().contains(KEY_PREFERENCE_WEIGHTS)) {
            log.debug("No config for '%s' found. Using default.", KEY_PREFERENCE_WEIGHTS);
            log.trace("DEFAULT: %s", defaultWeights);

            return defaultWeights;
        }

        Json preferenceWeights = json.json(KEY_PREFERENCE_WEIGHTS);
        Map<RacePrefCategory, Double> preferencesWeighs = new HashMap<>(RacePrefCategory.values().length);
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

            preferencesWeighs.put(prefCategory, weight);
        }

        return preferencesWeighs;
    }

    public JsonE toPreferenceWeights(Map<RacePrefCategory, Double> preferenceWeights) {
        JsonE json = new JsonE();
        preferenceWeights.forEach((category, weight) -> {
            if (weight < DEFAULT_MIN_WEIGHT || weight > DEFAULT_MAX_WEIGHT) {
                log.warn("Preference weight %s for %s is out of range from %s to %s. Using default %s",
                    weight,
                    category.name(),
                    DEFAULT_MIN_WEIGHT,
                    DEFAULT_MAX_WEIGHT,
                    DEFAULT_PREFERENCE_WEIGHT);
                weight = DEFAULT_PREFERENCE_WEIGHT;
            }
            json.add(category.name(), weight);
        });

        return json;
    }

    public Map<RaceStandingCategory, Double> toStandingsWeights(Json json, Map<RaceStandingCategory, Double> defaultWeights) {
        if (!json.keys().contains(KEY_STANDING_WEIGHTS)) {
            log.debug("No config for '%s' found. Using default.", KEY_STANDING_WEIGHTS);
            log.trace("DEFAULT: %s", defaultWeights);

            return defaultWeights;
        }

        Map<RaceStandingCategory, Double> standingsWeights = new HashMap<>(RaceStandingCategory.values().length);
        Json standingWeights = json.json(KEY_STANDING_WEIGHTS);

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

            standingsWeights.put(standingCategory, weight);
        }

        return standingsWeights;
    }

    public JsonE toStandingWeights(Map<RaceStandingCategory, Double> standingWeights) {
        JsonE json = new JsonE();
        standingWeights.forEach((category, weight) -> {
            if (weight < DEFAULT_MIN_WEIGHT || weight > DEFAULT_MAX_WEIGHT) {
                log.warn("Standing weight %s for %s is out of range from %s to %s. Using default %s",
                    weight,
                    category.name(),
                    DEFAULT_MIN_WEIGHT,
                    DEFAULT_MAX_WEIGHT,
                    DEFAULT_STANDING_WEIGHT);
                weight = DEFAULT_STANDING_WEIGHT;
            }

            json.add(category.name(), weight);
        });

        return json;
    }

    public List<String> toGameRaces(Json json, List<String> defaultRaces) {
        if (!json.keys().contains(KEY_VANILLA_RACES)) {
            log.debug("No config for '%s' found. Using default.", KEY_VANILLA_RACES);
            log.trace("DEFAULT: %s", defaultRaces);
            return defaultRaces;
        }

        return Arrays.asList(json.texts(KEY_VANILLA_RACES));
    }

    public Map<String, List<String>> toRaceBoostToggles(Json json, Map<String, List<String>> defaultToggles) {
        if (!json.keys().contains(KEY_RACE_BOOST_TOGGLES)) {
            log.debug("No config for '%s' found. Using default.", KEY_RACE_BOOST_TOGGLES);
            log.trace("DEFAULT: %s", defaultToggles);

            return defaultToggles;
        }

        Json raceBoostTogglesJson = json.json(KEY_RACE_BOOST_TOGGLES);
        Map<String, List<String>> raceBoostToggles = new HashMap<>();
        for (String raceName : raceBoostTogglesJson.keys()) {
            List<String> enabledRaces = Arrays.asList(raceBoostTogglesJson.texts(raceName));
            raceBoostToggles.put(raceName, enabledRaces);
        }

        return raceBoostToggles;
    }

    public JsonE toRaceBoostToggles(Map<String, List<String>> raceBoostToggles) {
        JsonE json = new JsonE();
        raceBoostToggles.forEach((raceName, enabledRaces) -> {
            json.add(raceName, enabledRaces.toArray(new String[0]));
        });

        return json;
    }
}
