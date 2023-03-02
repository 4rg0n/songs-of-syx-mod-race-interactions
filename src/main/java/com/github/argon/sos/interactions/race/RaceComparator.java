package com.github.argon.sos.interactions.race;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import init.biomes.BUILDING_PREFS;
import init.biomes.CLIMATES;
import init.race.Race;
import init.resources.Edible;
import lombok.*;
import settlement.room.infra.elderly.ROOM_RESTHOME;
import settlement.room.main.ROOMS;
import settlement.room.main.RoomEmploymentSimple;
import settlement.stats.STATS;
import settlement.stats.StatsReligion;
import snake2d.util.sets.LIST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static settlement.main.SETT.ROOMS;

/**
 * Compares two races and spits out a {@link Result}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RaceComparator {
    private final static Logger log = Loggers.getLogger(RaceComparator.class);

    @Getter(lazy = true)
    private final static RaceComparator instance = new RaceComparator();

    public List<Result> compare(List<Race> races) {
        log.debug("Comparing %s races", races.size());
        List<Result> comparisonResults = new ArrayList<>();

        for (Race race : races) {
            for (Race otherRace : races) {
                // do not compare the same race
                if (race.key.equals(otherRace.key)) {
                    continue;
                }

                comparisonResults.add(compare(race, otherRace));
            }
        }

        log.trace("Result: %s", comparisonResults.toString());
        return comparisonResults;
    }

    public Result compare(Race race, Race otherRace) {
        int foodMatches = foodMatches(race, otherRace);
        int homeMatches = homeMatches(race, otherRace);

        Map<String, Double> buildingPrefDifferenceMap = buildingPrefDiff(race, otherRace);
        Map<String, Double> workPrefDifferenceMap = workPrefDiff(race, otherRace);
        Map<String, Double> religionPrefDifferenceMap = religionPrefDiff(race, otherRace);
        Map<String, Double> climatePrefDifferenceMap = climatePrefDiff(race, otherRace);

        return Result.builder()
                .race(race.key)
                .otherRace(otherRace.key)
                .foodMatches(foodMatches)
                // FIXME: 23.02.2023 always 1? useless?
                .homeMatches(homeMatches)
                .buildingPrefDiff(buildingPrefDifferenceMap)
                .workPrefDiff(workPrefDifferenceMap)
                .religionPrefDiff(religionPrefDifferenceMap)
                .climatePrefDiff(climatePrefDifferenceMap)
                .build();
    }

    private Map<String, Double> climatePrefDiff(Race race, Race otherRace) {
        return diff(CLIMATES.ALL(), climateStat -> {
            double climate = race.population().climate(climateStat);
            double otherClimate = otherRace.population().climate(climateStat);

            return Math.abs(climate - otherClimate);
        }, climateStat -> climateStat.key);
    }


    private Map<String, Double> religionPrefDiff(Race race, Race otherRace) {
        return diff(STATS.RELIGION().ALL, religionStat -> {
            double religion = race.stats().religion(religionStat);
            double otherReligion = otherRace.stats().religion(religionStat);

            return Math.abs(religion - otherReligion);
        }, religionStat -> religionStat.info.name.toString());
    }

    private Map<String, Double> workPrefDiff(Race race, Race otherRace) {
        return diff(ROOMS().all(), roomBlueprint -> {
            RoomEmploymentSimple employment = roomBlueprint.employment();
            if (employment == null) {
                return null;
            }

            double work = race.pref().getWork(employment);
            double otherWork = otherRace.pref().getWork(employment);

            return Math.abs(work - otherWork);
        }, roomBlueprint -> roomBlueprint.employment().title.toString());
    }

    private Map<String, Double> buildingPrefDiff(Race race, Race otherRace) {
        return diff(BUILDING_PREFS.ALL(), buildingPref -> {
            double structure = race.pref().structure(buildingPref);
            double otherStructure = otherRace.pref().structure(buildingPref);

            return Math.abs(structure - otherStructure);
        }, buildingPref -> buildingPref.key);
    }

    private int homeMatches(Race race, Race otherRace) {
        int matches = 0;
        LIST<ROOM_RESTHOME> homes = race.pref().resthomes;
        LIST<ROOM_RESTHOME> otherHomes = otherRace.pref().resthomes;

        if (homes.size() > otherHomes.size()) {
            for (ROOM_RESTHOME home : homes) {
                if (otherHomes.contains(home)) {
                    matches++;
                }
            }
        } else {
            for (ROOM_RESTHOME home : otherHomes) {
                if (homes.contains(home)) {
                    matches++;
                }
            }
        }

        return matches;
    }

    private int foodMatches(Race race, Race otherRace) {
        int matches = 0;
        LIST<Edible> food = race.pref().food;
        LIST<Edible> otherFood = otherRace.pref().food;

        if (food.size() > otherFood.size()) {
            for (Edible edible : food) {
                if (otherFood.contains(edible)) {
                    matches++;
                }
            }
        } else {
            for (Edible edible : otherFood) {
                if (food.contains(edible)) {
                    matches++;
                }
            }
        }

        return matches;
    }

    private <T> Map<String, Double> diff(
            LIST<T> sourceList,
            Function<T, Double> differenceFunc,
            Function<T, String> nameFunc

    ) {
        Map<String, Double> prefDifferenceMap = new HashMap<>();

        for (T item : sourceList) {
            Double diff = differenceFunc.apply(item);

            if (diff == null) {
                continue; // skip
            }

            String name = nameFunc.apply(item);

            prefDifferenceMap.put(name, diff);
        }

        return prefDifferenceMap;
    }

    @ToString
    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class Result {

        private final String race;
        private final String otherRace;
        /**
         * Not used right now
         */
        private final int homeMatches;
        /**
         * How much equal food they prefer
         */
        private final int foodMatches;

        /**
         * List of the games building structures like wood and stone from {@link BUILDING_PREFS#ALL()} with
         * the difference in liking between the compared races
         */
        private final Map<String, Double> buildingPrefDiff;
        /**
         * List of the games workplaces like mines and fields from {@link ROOMS#all()} with
         * the difference in liking between the compared races
         */
        private final Map<String, Double> workPrefDiff;
        /**
         * List of the games religions from {@link StatsReligion#ALL} with
         * the difference in liking between the compared races
         */
        private final Map<String, Double> religionPrefDiff;

        /**
         * List of the games climates from {@link CLIMATES#ALL()} with
         * the difference in liking between the compared races
         */
        private final Map<String, Double> climatePrefDiff;
    }
}
