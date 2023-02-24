package com.github.argon.sos.interactions.race;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.util.ReflectionUtil;
import init.race.RACES;
import init.race.Race;
import snake2d.util.sets.LIST;

import java.util.*;
import java.util.stream.Collectors;

/**
 * For accessing and manipulating vanilla and custom races
 */
public class RaceService {
    private final static Logger log = Loggers.getLogger(RaceService.class);
    private final Map<String, Integer> raceIndexMap = new HashMap<>();

    private final List<String> gameRaces;

    public RaceService(List<String> gameRaces) {
        this.gameRaces = gameRaces;
        for (Race race : getAll()) {
            raceIndexMap.put(race.key, race.index);
        }
    }

    public void setLiking(Race race, Race otherRace, double liking) {
        setLiking(race.key, otherRace.key, liking);
    }

    public void setLiking(String name, String otherName, double liking) {
        try {
            getRace(name).flatMap(race ->
                getRace(otherName).flatMap(otherRace ->
                    ReflectionUtil.getField("others", race.pref())
                        .map(o -> (double[]) o)))
                        .ifPresent(racePrefs -> {
                            Integer otherRaceIdx = raceIndexMap.get(otherName);
                            racePrefs[otherRaceIdx] = liking;
                            log.debug("Set %s liking %s to %s", name, otherName, Double.toString(liking));
                        });
        } catch (Exception e) {
            log.error("Could not set %s liking %s to %s", name, otherName, Double.toString(liking), e);
        }
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

    public List<Race> getAll() {
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

    private List<Race> toJavaList(LIST<Race> raceLIST) {
        List<Race> races = new ArrayList<>();

        for (Race race : raceLIST) {
            races.add(race);
        }

        return races;
    }
}
