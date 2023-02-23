package com.github.argon.sos.interactions.race;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import settlement.stats.STATS;

import java.util.Collection;

public class RacePrefCalculator {

    private final int allFoodCount = STATS.FOOD().all.size();

    public Result calculate(RaceComparator.Result compareResult) {
        double foodPercent =  (double) compareResult.getFoodMatches() / (double) allFoodCount;
        double climatePercent = 1 - sum(compareResult.getClimatePrefDiff().values());
        double buildingPercent = 1 - sum(compareResult.getBuildingPrefDiff().values());
        double religionPercent = 1 - sum(compareResult.getReligionPrefDiff().values());
        double workPercent = 1 - sum(compareResult.getWorkPrefDiff().values());

        return Result.builder()
                .race(compareResult.getRace())
                .otherRace(compareResult.getOtherRace())
                .foodPercent(foodPercent)
                .climatePercent(climatePercent)
                .buildingPercent(buildingPercent)
                .religionPercent(religionPercent)
                .workPercent(workPercent)
                .build();
    }

    private double sum(Collection<Double> doubles) {
        // divide each difference value with 2; because they range from 0 to 2 (higher is worse)
        return doubles.stream().reduce(0d, (aDouble, bDouble) -> (aDouble / 2d) + (bDouble / 2d));
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Result {
        private final String race;
        private final String otherRace;
        private final double foodPercent;
        private final double climatePercent;
        private final double buildingPercent;
        private final double religionPercent;
        private final double workPercent;
    }
}
