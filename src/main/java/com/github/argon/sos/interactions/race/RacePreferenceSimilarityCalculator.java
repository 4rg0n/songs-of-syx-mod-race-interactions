package com.github.argon.sos.interactions.race;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.*;
import settlement.stats.STATS;

import java.util.Collection;

/**
 * Calculates the similarity between the preferences of two races.
 * Will produce a {@link SimilarityResult} with percentages.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RacePreferenceSimilarityCalculator {
    private final static Logger log = Loggers.getLogger(RacePreferenceSimilarityCalculator.class);

    private final int allFoodCount = STATS.FOOD().all.size();

    @Getter(lazy = true)
    private final static RacePreferenceSimilarityCalculator instance = new RacePreferenceSimilarityCalculator();

    public SimilarityResult calculate(RaceComparator.Result compareResult) {
        log.debug("Calculating preference similarity between %s and %s",
                compareResult.getRace(), compareResult.getOtherRace());

        double foodPercent =  (double) compareResult.getFoodMatches() / (double) allFoodCount;
        double climatePercent = 1 - sum(compareResult.getClimatePrefDiff().values());
        double buildingPercent = 1 - sum(compareResult.getBuildingPrefDiff().values());
        double religionPercent = 1 - sum(compareResult.getReligionPrefDiff().values());
        double workPercent = 1 - sum(compareResult.getWorkPrefDiff().values());

        SimilarityResult result = SimilarityResult.builder()
                .race(compareResult.getRace())
                .otherRace(compareResult.getOtherRace())
                .foodPercent(foodPercent)
                .climatePercent(climatePercent)
                .buildingPercent(buildingPercent)
                .religionPercent(religionPercent)
                .workPercent(workPercent)
                .build();

        log.trace("Result: %s", result.toString());

        return result;
    }

    private double sum(Collection<Double> doubles) {
        // divide each difference value with 2; because they range from 0 to 2 (higher is worse)
        return doubles.stream().reduce(0d, (aDouble, bDouble) -> (aDouble / 2d) + (bDouble / 2d));
    }

    @Getter
    @Builder
    @ToString
    @RequiredArgsConstructor
    public static class SimilarityResult {
        private final String race;
        private final String otherRace;
        private final double foodPercent;
        private final double climatePercent;
        private final double buildingPercent;
        private final double religionPercent;
        private final double workPercent;
    }
}
