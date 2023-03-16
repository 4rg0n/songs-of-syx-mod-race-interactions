package com.github.argon.sos.interactions.config;

import lombok.*;

import java.nio.file.Path;

/**
 * Used as information for loading {@link RaceInteractionsConfig} from other mods
 */
@ToString
@EqualsAndHashCode
@Getter
@Builder
@RequiredArgsConstructor
public class OtherModInfo {
    private final Path absolutePath;
    private final String name;
    private final String description;
    private final Path configPath;
}
