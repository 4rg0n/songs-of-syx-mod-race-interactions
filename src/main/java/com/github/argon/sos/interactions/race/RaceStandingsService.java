package com.github.argon.sos.interactions.race;

import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.game.api.GameRaceApi;
import init.race.Race;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RaceStandingsService {

    private final static Logger log = Loggers.getLogger(RaceStandingsService.class);

    private final GameRaceApi gameRaceApi = GameRaceApi.getInstance();

    @Getter(lazy = true)
    private final static RaceStandingsService instance = new RaceStandingsService();

    public void boostStanding(
            RaceStandingCategory category,
            Race race,
            double inc,
            double weight
    ) {
        int racePopulation = gameRaceApi.citizenCount(race);
        int racePopulationThreshold = 1000;
         double boost = (inc * weight) / (double) (racePopulationThreshold / racePopulation);
        log.debug("Boosting standing %s of %s by (%s * %s) (%s / %s) = %s",
                category.name(), race.key, inc, weight, racePopulationThreshold, racePopulation, boost);
        incStanding(category, race, boost);
    }

    public void incStanding(RaceStandingCategory category, Race race, double inc) {
        switch (category) {
            case LOYALTY:
                gameRaceApi.increaseLoyalty(race, inc);
                break;
            case EXPECTATION:
                gameRaceApi.increaseExpectation(race, inc);
                break;
            case HAPPINESS:
                gameRaceApi.increaseHappiness(race, inc);
                break;
            case FULFILLMENT:
                gameRaceApi.increaseFulfillment(race, inc);
                break;
        }
    }
}
