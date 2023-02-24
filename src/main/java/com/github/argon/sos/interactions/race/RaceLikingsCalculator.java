package com.github.argon.sos.interactions.race;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 *
 */
@RequiredArgsConstructor
public class RaceLikingsCalculator {

    private final static Logger log = Loggers.getLogger(RaceService.class);

    private final Map<RacePrefCategory, Double> categoryWeightMap;

    public final static double DEFAULT_WEIGHT = 1d;

    public double calculate(RacePrefCalculator.Result prefCalcResult) {
        double buildingPercent = prefCalcResult.getBuildingPercent() * getCategoryWeight(RacePrefCategory.BUILDING);
        double climatePercent = prefCalcResult.getClimatePercent() * getCategoryWeight(RacePrefCategory.CLIMATE);
        double foodPercent = prefCalcResult.getFoodPercent() * getCategoryWeight(RacePrefCategory.FOOD);
        double religionPercent = prefCalcResult.getReligionPercent() * getCategoryWeight(RacePrefCategory.RELIGION);
        double workPercent = prefCalcResult.getWorkPercent() * getCategoryWeight(RacePrefCategory.WORK);

        double liking = (buildingPercent + climatePercent + foodPercent + religionPercent + workPercent) / RacePrefCategory.values().length;

        return liking;
    }

    public double getCategoryWeight(RacePrefCategory category) {
        if (!categoryWeightMap.containsKey(category)) {
            log.debug("Weight for category %s not found in categoryWeightMap. Using default: %s",
                    category.name(), Double.toString(DEFAULT_WEIGHT));
            return DEFAULT_WEIGHT;
        }

        return categoryWeightMap.get(category);
    }
}
