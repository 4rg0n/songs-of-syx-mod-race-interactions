package com.github.argon.sos.interactions;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import snake2d.util.file.JsonE;

import java.util.LinkedHashMap;
import java.util.Map;

public class Mapper {
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

        config.getRacePreferenceWeightMap().forEach((category, weight) -> {
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
