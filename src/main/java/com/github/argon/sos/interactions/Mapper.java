package com.github.argon.sos.interactions;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import snake2d.util.file.JsonE;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.argon.sos.interactions.config.ConfigUtil.Default.*;

public class Mapper {
    private final static Logger log = Loggers.getLogger(Mapper.class);
    public static double fromSliderToWeight(int sliderValue) {
        return (sliderValue / 100d);
    }

    public static int fromWeightToSlider(double weight) {
        return (int) (weight * 100);
    }

    public static int toSliderRange(double weight) {
        return (int) (weight * 100);
    }

    public static int toSliderWidth(double minWeight, double maxWeight) {
        return Math.abs(toSliderRange(minWeight)) + Math.abs(toSliderRange(maxWeight));
    }

    public static JsonE toJson(RaceInteractionsConfig config) {
        JsonE json = new JsonE();
        JsonE weights = new JsonE();
        log.debug("Mapping config to json");
        log.trace("Config: %s", config.toString());

        config.getRacePreferenceWeightMap().forEach((category, weight) -> {
            if (weight < MIN_WEIGHT || weight > MAX_WEIGHT) {
                log.warn("Weight %s for %s is out of range from %s to %s. Using default %s",
                    Double.toString(weight),
                    category.name(),
                    Double.toString(MIN_WEIGHT),
                    Double.toString(MAX_WEIGHT),
                    Double.toString(DEFAULT_WEIGHT));
                weight = DEFAULT_WEIGHT;
            }
            weights.add(category.name(), weight);
        });

        json.add("CUSTOM_RACE_ONLY", config.isCustomOnly());
        json.add("HONOR_CUSTOM_RACE_LIKINGS", config.isHonorCustom());
        json.add("PREFERENCE_WEIGHTS", weights);
        json.addStrings("VANILLA_RACES", config.getGameRaces().toArray(new String[0]));

        return json;
    }

    public static <K, V> LinkedHashMap<K, V> toOrderedMap(Map<K, V> map) {
        return new LinkedHashMap<>(map);
    }
}
