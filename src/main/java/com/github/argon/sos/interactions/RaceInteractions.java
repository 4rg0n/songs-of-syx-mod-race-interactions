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

    private final RaceService raceService;

    public RaceInteractions(
        RaceComparator raceComparator,
        RacePrefCalculator racePrefCalculator,
        RaceService raceService
    ) {
        this.raceComparator = raceComparator;
        this.racePrefCalculator = racePrefCalculator;
        this.raceService = raceService;
    }


    public void manipulateRaceLikings(RaceInteractionsConfig config) {
        boolean customOnly = config.isCustomOnly();
        boolean honorCustom = config.isHonorCustom();
        List<Race> races = RaceService.getAll();
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

    public void applyRaceLikings(List<RaceService.RaceLiking> likings) {
        for (RaceService.RaceLiking liking : likings) {
            raceService.setLiking(liking.getRace(), liking.getOtherRace(), liking.getLiking());
        }
    }
}
