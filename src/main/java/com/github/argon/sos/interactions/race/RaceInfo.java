package com.github.argon.sos.interactions.race;

import init.race.Race;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder
@RequiredArgsConstructor
public class RaceInfo {
    private final Race race;
}
