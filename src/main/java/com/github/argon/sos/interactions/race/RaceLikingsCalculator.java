package com.github.argon.sos.interactions.race;

import com.github.argon.sos.interactions.config.RacePrefCategory;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.Map;

import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.DEFAULT_PREFERENCE_WEIGHT;

/**
 * Calculates the liking between two races based on a {@link RacePreferenceSimilarityCalculator.SimilarityResult}
 */
@AllArgsConstructor
public class RaceLikingsCalculator {

    private final static Logger log = Loggers.getLogger(RaceService.class);

    @Setter
    private Map<RacePrefCategory, Double> categoryWeightMap;

    public double calculate(RacePreferenceSimilarityCalculator.SimilarityResult prefCalcResult) {
        double buildingPercent = calculateLiking(prefCalcResult.getBuildingPercent(), RacePrefCategory.BUILDING);
        double climatePercent = calculateLiking(prefCalcResult.getClimatePercent(), RacePrefCategory.CLIMATE);
        double foodPercent = calculateLiking(prefCalcResult.getFoodPercent(), RacePrefCategory.FOOD);
        double religionPercent = calculateLiking(prefCalcResult.getReligionPercent(), RacePrefCategory.RELIGION);
        double workPercent = calculateLiking(prefCalcResult.getWorkPercent(), RacePrefCategory.WORK);

        double liking = (buildingPercent + climatePercent + foodPercent + religionPercent + workPercent) / RacePrefCategory.values().length;
        return liking;
    }

    public double getCategoryWeight(RacePrefCategory category) {
        if (!categoryWeightMap.containsKey(category)) {
            log.debug("Weight for category %s not found in categoryWeightMap. Using default: %s",
                    category.name(), DEFAULT_PREFERENCE_WEIGHT);
            return DEFAULT_PREFERENCE_WEIGHT;
        }

        return categoryWeightMap.get(category);
    }

    private double calculateLiking(double similarityPercent, RacePrefCategory category) {
        // everything below 50% is considered negative
        if (similarityPercent < 0.5) {
            similarityPercent = -similarityPercent;
        }

        return similarityPercent * getCategoryWeight(category);
    }
}
