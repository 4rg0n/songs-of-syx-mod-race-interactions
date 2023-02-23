package com.github.argon.sos.interactions;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.race.RaceComparator;
import com.github.argon.sos.interactions.race.RaceLikingsCalculator;
import com.github.argon.sos.interactions.race.RacePrefCalculator;
import com.github.argon.sos.interactions.race.RaceService;
import init.race.Race;

import java.util.List;

public class RaceInteractions {
    private final RaceService raceService;
    private final RaceComparator raceComparator;
    private final RacePrefCalculator racePrefCalculator;
    private final RaceLikingsCalculator raceLikingsCalculator;

    /**
     * Will only manipulate modded custom race likings
     */
    private final boolean customOnly;
    /**
     * Will not manipulate custom race likings to vanilla game races
     */
    private final boolean honorCustom;

    public RaceInteractions(RaceInteractionsConfig config) {
        this.customOnly = config.isCustomOnly();
        this.honorCustom = config.isHonorCustom();
        raceService = new RaceService(config.getGameRaces());
        raceComparator = new RaceComparator();
        racePrefCalculator = new RacePrefCalculator();
        raceLikingsCalculator = new RaceLikingsCalculator(config.getRacePreferenceWeightMap());
    }

    public void manipulateRaceLikings() {
        List<Race> races = raceService.getAll();
        List<RaceComparator.Result> compareResults = raceComparator.compare(races);

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
