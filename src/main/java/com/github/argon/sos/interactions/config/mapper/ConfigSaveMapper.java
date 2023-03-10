package com.github.argon.sos.interactions.config.mapper;

import com.github.argon.sos.interactions.config.RacePrefCategory;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.util.Indexed;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * For mapping configuration data from and into the games save file
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigSaveMapper {
    private final static Logger log = Loggers.getLogger(ConfigSaveMapper.class);

    @Getter(lazy = true)
    private final static ConfigSaveMapper instance = new ConfigSaveMapper();

    public double[] toDoubleIndexed(Map<? extends Indexed, Double> doubleMap) {
        double[] doubles = new double[doubleMap.values().size()];
        doubleMap.forEach((indexed, weight) ->
            doubles[indexed.getIndex()] = weight
        );

        return doubles;
    }

    public String fromRaceBoostToggles(Map<String, List<String>> raceBoostToggles) {
        return raceBoostToggles.entrySet().stream().map(entry -> {
            String raceName = entry.getKey();
            List<String> enabledRaces = entry.getValue();
            String enabledRacesString = String.join(",", enabledRaces);

            return raceName + ":" + enabledRacesString;
        }).collect(Collectors.joining(";"));
    }

    public Map<String, List<String>> toRaceBoostToggles(String raceBoostTogglesString) {
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

    public Map<RacePrefCategory, Double> toPreferenceWeights(double[] preferenceWeightsIndexed) {
        Map<RacePrefCategory, Double> preferenceWeights = new HashMap<>(RacePrefCategory.values().length);
        for (int i = 0, weightsIndexedLength = preferenceWeightsIndexed.length; i < weightsIndexedLength; i++) {
            double weight = preferenceWeightsIndexed[i];
            RacePrefCategory category = RacePrefCategory.valueOfIndex(i);
            preferenceWeights.put(category, weight);
        }

        return preferenceWeights;
    }
    public Map<RaceStandingCategory, Double> toStandingWeights(double[] standingWeightsIndexed) {
        Map<RaceStandingCategory, Double> standingWeights = new HashMap<>(RaceStandingCategory.values().length);
        for (int i = 0, weightsIndexedLength = standingWeightsIndexed.length; i < weightsIndexedLength; i++) {
            double weight = standingWeightsIndexed[i];
            RaceStandingCategory category = RaceStandingCategory.valueOfIndex(i);
            standingWeights.put(category, weight);
        }

        return standingWeights;
    }
}
