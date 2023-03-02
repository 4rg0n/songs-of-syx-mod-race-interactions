package com.github.argon.sos.interactions;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import init.race.Race;
import snake2d.util.sets.LIST;

import java.util.*;
import java.util.stream.Collectors;

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

    public static <K extends Enum<K>, V> LinkedHashMap<K, V> toOrderedMap(Map<K, V> map) {
        return map.entrySet().stream()
            .sorted(Comparator.comparing(entry -> entry.getKey().ordinal()))
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue,
                LinkedHashMap::new));
    }

    public static List<Race> toJavaList(LIST<Race> raceLIST) {
        List<Race> races = new ArrayList<>();

        for (Race race : raceLIST) {
            races.add(race);
        }

        return races;
    }
}
