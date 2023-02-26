package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.race.RacePrefCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
@Getter
@Builder
@RequiredArgsConstructor
public class RaceInteractionsConfig {

    private final boolean customOnly;

    private final boolean honorCustom;

    private final Map<RacePrefCategory, Double> racePreferenceWeightMap;

    private final List<String> gameRaces;
}
