package com.github.argon.sos.interactions.race;

import init.race.Race;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Just a container for future additions.
 * No real usage yet.
 */
@ToString
@Getter
@Builder
@RequiredArgsConstructor
public class RaceInfo {
    private final Race race;
}
