package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.Mapper;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.race.RaceService;
import init.race.RACES;
import init.race.Race;
import settlement.entity.humanoid.HCLASS;
import settlement.stats.STATS;
import settlement.stats.standing.STANDINGS;
import settlement.stats.standing.StandingCitizen;
import world.World;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RaceUtil {
    private final static Logger log = Loggers.getLogger(RaceUtil.class);
    private static List<RaceService.RaceLiking> vanillaLikings;
    public static void increaseHappiness(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().happiness, race, inc);
    }

    public static void increaseExpectation(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().expectation, race, inc);
    }

    public static void increaseFulfillment(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().fullfillment, race, inc);
    }

    public static void increaseLoyalty(Race race, double inc) {
        increaseStanding(STANDINGS.CITIZEN().main, race, inc);
    }

    private static void increaseStanding(StandingCitizen.CitizenThing standing, Race race, double inc) {
        double current = standing.getD(race);
        double newStanding = current + inc;

        if (newStanding > 1) {
            newStanding = 1d;
        } else if (newStanding < 0) {
            newStanding = 0d;
        }

        try {
            Method declaredMethod = StandingCitizen.CitizenThing.class
                    .getDeclaredMethod("set", Race.class, double.class);
            ReflectionUtil.invokeMethod(declaredMethod, standing, race, newStanding);
            log.trace("Increased %s of %s by %s to now %s",
                    standing.info().name, race.key, inc, newStanding);
        } catch (Exception e) {
            log.warn("Could not increase %s for %s", standing.info().name, race.key, e);
        }
    }

    public static void initVanillaLikings() {
        if (vanillaLikings == null) {
            vanillaLikings = getAllLikings();
        }
    }

    public static List<RaceService.RaceLiking> getVanillaLikings() {
        return vanillaLikings;
    }

    /**
     * @return flat list of likings of each race to another race
     */
    public static List<RaceService.RaceLiking> getAllLikings() {
        List<RaceService.RaceLiking> likings = new ArrayList<>();

        for (Race race : RaceUtil.getAll()) {
            for (Race otherRace : RaceUtil.getAll()) {
                if (race.key.equals(otherRace.key)) {
                    continue;
                }

                double liking = getLiking(race, otherRace);
                likings.add(RaceService.RaceLiking.builder()
                        .race(race.key)
                        .otherRace(otherRace.key)
                        .liking(liking)
                        .build());
            }
        }

        return likings;
    }

    public static double getLiking(Race race, Race otherRace) {
        return race.pref().other(otherRace);
    }

    public static List<Race> getAll() {
        return Mapper.toJavaList(RACES.all());
    }

    public static int citizenCount(Race race) {
        HCLASS cl = HCLASS.CITIZEN;
        return STATS.POP().POP.data(cl).get(race, 0) + World.ARMIES().cityDivs().total(race);
    }

    public static double getAvgHappiness() {
        List<Race> races = getAll();
        double allHappiness = races.stream()
                .map(RaceUtil::getHappiness)
                .mapToDouble(Double::doubleValue)
                .sum();

        return allHappiness / races.size();
    }

    public static double getAvgLoyalty() {
        List<Race> races = getAll();
        double allLoyalty = races.stream()
                .map(RaceUtil::getLoyalty)
                .mapToDouble(Double::doubleValue)
                .sum();

        return allLoyalty / races.size();
    }

    public static double getHappiness(Race race) {
        StandingCitizen standings = STANDINGS.CITIZEN();
        return standings.happiness.getD(race);
    }

    public static double getLoyalty(Race race) {
        StandingCitizen standings = STANDINGS.CITIZEN();
        return standings.main.getD(race);
    }
}
