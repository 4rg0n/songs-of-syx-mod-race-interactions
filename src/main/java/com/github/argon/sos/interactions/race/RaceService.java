package com.github.argon.sos.interactions.race;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.util.RaceUtil;
import com.github.argon.sos.interactions.util.ReflectionUtil;
import init.race.Race;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * For accessing and manipulating the games {@link Race}s.
 */
public class RaceService {
    private final static Logger log = Loggers.getLogger(RaceService.class);
    private final Map<String, Integer> raceIndexMap = new HashMap<>();

    private final List<String> gameRaces;

    public RaceService(List<String> gameRaces) {
        this.gameRaces = gameRaces;
        for (Race race : RaceUtil.getAll()) {
            raceIndexMap.put(race.key, race.index);
        }
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
                            log.trace("Set %s liking %s to %s", name, otherName, liking);
                        });
        } catch (Exception e) {
            log.error("Could not set %s liking %s to %s", name, otherName, liking, e);
        }
    }

    public List<RaceInfo> getAllRaceInfo() {
        return RaceUtil.getAll().stream().map(race ->
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

        return Optional.of(RaceUtil.getAll().get(raceIdx));
    }


    public List<Race> getGameRaces() {
        return RaceUtil.getAll().stream()
                .filter(race -> gameRaces.contains(race.key))
                .collect(Collectors.toList());
    }

    public List<Race> getCustomRaces() {
        return RaceUtil.getAll().stream()
                .filter(race -> !gameRaces.contains(race.key))
                .collect(Collectors.toList());
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
