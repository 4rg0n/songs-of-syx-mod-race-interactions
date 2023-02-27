package com.github.argon.sos.interactions.race;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.util.ReflectionUtil;
import init.race.RACES;
import init.race.Race;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.sets.LIST;

import java.util.*;
import java.util.stream.Collectors;

/**
 * For accessing and manipulating the games {@link Race}s.
 */
public class RaceService {
    private final static Logger log = Loggers.getLogger(RaceService.class);
    private final Map<String, Integer> raceIndexMap = new HashMap<>();

    private final List<String> gameRaces;

    private static List<RaceLiking> vanillaLikings;

    public RaceService(List<String> gameRaces) {
        this.gameRaces = gameRaces;
        for (Race race : getAll()) {
            raceIndexMap.put(race.key, race.index);
        }
    }

    public static void initVanillaLikings() {
        if (vanillaLikings == null) {
            vanillaLikings = getAllLikings();
        }
    }

    public static List<RaceLiking> getVanillaLikings() {
        return vanillaLikings;
    }

    /**
     * @return flat list of likings of each race to another race
     */
    public static List<RaceLiking> getAllLikings() {
        List<RaceLiking> likings = new ArrayList<>();

        for (Race race : getAll()) {
            for (Race otherRace : getAll()) {
                if (race.key.equals(otherRace.key)) {
                    continue;
                }

                double liking = getLiking(race, otherRace);
                likings.add(RaceLiking.builder()
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

    public void setLiking(Race race, Race otherRace, double liking) {
        setLiking(race.key, otherRace.key, liking);
    }

    /**
     * Injects liking into the games races
     */
    public void setLiking(String name, String otherName, double liking) {
        try {
            // set likings in "others"
            getRace(name).flatMap(race ->
                getRace(otherName).flatMap(otherRace ->
                    ReflectionUtil.getField("others", race.pref())
                        .map(o -> (double[]) o)))
                        .ifPresent(racePrefs -> {
                            Integer otherRaceIdx = raceIndexMap.get(otherName);
                            racePrefs[otherRaceIdx] = liking;
                            log.trace("Set %s liking %s to %s", name, otherName, Double.toString(liking));
                        });
        } catch (Exception e) {
            log.error("Could not set %s liking %s to %s", name, otherName, Double.toString(liking), e);
        }
    }

    public List<RaceInfo> getAllRaceInfo() {
        return getAll().stream().map(race ->
            getRaceInfo(race.key).orElse(null)
        ).collect(Collectors.toList());
    }

    public Optional<RaceInfo> getRaceInfo(String name) {
        return getRace(name).map(race ->
            RaceInfo.builder().race(race).build()
        );
    }

    public boolean isCustom(String name) {
        return !gameRaces.contains(name);
    }

    public Optional<Race> getRace(String name) {
        Integer raceIdx = raceIndexMap.get(name);
        if (raceIdx == null) {
            return Optional.empty();
        }

        return Optional.of(getAll().get(raceIdx));
    }

    public static List<Race> getAll() {
        return toJavaList(RACES.all());
    }

    public List<Race> getGameRaces() {
        return getAll().stream()
                .filter(race -> gameRaces.contains(race.key))
                .collect(Collectors.toList());
    }

    public List<Race> getCustomRaces() {
        return getAll().stream()
                .filter(race -> !gameRaces.contains(race.key))
                .collect(Collectors.toList());
    }

    private static List<Race> toJavaList(LIST<Race> raceLIST) {
        List<Race> races = new ArrayList<>();

        for (Race race : raceLIST) {
            races.add(race);
        }

        return races;
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    public static class RaceLiking {
        private final String race;
        private final String otherRace;

        private final double liking;
    }
}
