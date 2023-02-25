package com.github.argon.sos.interactions;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.race.RaceComparator;
import com.github.argon.sos.interactions.race.RaceLikingsCalculator;
import com.github.argon.sos.interactions.race.RacePrefCalculator;
import com.github.argon.sos.interactions.race.RaceService;
import init.race.Race;

import java.util.List;

public class RaceInteractions {
    private final RaceComparator raceComparator;
    private final RacePrefCalculator racePrefCalculator;

    public RaceInteractions(
        RaceComparator raceComparator,
        RacePrefCalculator racePrefCalculator
    ) {
        this.raceComparator = raceComparator;
        this.racePrefCalculator = racePrefCalculator;
    }


    public void manipulateRaceLikings(RaceInteractionsConfig config) {
        boolean customOnly = config.isCustomOnly();
        boolean honorCustom = config.isHonorCustom();
        RaceService raceService = new RaceService(config.getGameRaces());
        List<Race> races = raceService.getAll();
        List<RaceComparator.Result> compareResults = raceComparator.compare(races);
        RaceLikingsCalculator raceLikingsCalculator = new RaceLikingsCalculator(config.getRacePreferenceWeightMap());

        for (RaceComparator.Result compareResult : compareResults) {
            String race = compareResult.getRace();
            String otherRace = compareResult.getOtherRace();
            RacePrefCalculator.Result calcResult = racePrefCalculator.calculate(compareResult);
            double liking = raceLikingsCalculator.calculate(calcResult);

            if (customOnly && honorCustom) {
                if (!raceService.isCustom(race) && raceService.isCustom(otherRace)) {
                    raceService.setLiking(race, otherRace, liking);
                }
            } else if (customOnly) {
                if ((raceService.isCustom(race) && raceService.isCustom(otherRace))
                || (raceService.isCustom(race) && !raceService.isCustom(otherRace))
                || (!raceService.isCustom(race) && raceService.isCustom(otherRace))
                ) {
                    raceService.setLiking(race, otherRace, liking);
                }
            } else {
                raceService.setLiking(race, otherRace, liking);
            }
        }
    }
}
