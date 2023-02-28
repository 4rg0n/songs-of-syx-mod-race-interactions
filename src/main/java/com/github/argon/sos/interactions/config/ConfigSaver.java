package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.util.Optional;

/**
 * Takes care of saving and loading {@link RaceInteractionsConfig} into and from the save game
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigSaver {
    private final static Logger log = Loggers.getLogger(ConfigSaver.class);

    @Getter(lazy = true)
    private final static ConfigSaver instance = new ConfigSaver();

    public void save(FilePutter file, RaceInteractionsConfig config) {
        log.debug("Saving Race Interactions Config into save game.");
        file.bool(config.isCustomOnly());
        file.bool(config.isHonorCustom());

        double[] preferenceWeightsIndexed = new double[RacePrefCategory.values().length];
        config.getRacePreferenceWeightMap().forEach((category, weight) -> {
            preferenceWeightsIndexed[category.getIndex()] = weight;
        });
        file.ds(preferenceWeightsIndexed);

        double[] standingWeightsIndexed = new double[RaceStandingCategory.values().length];
        config.getRaceStandingWeightMap().forEach((category, weight) -> {
            standingWeightsIndexed[category.getIndex()] = weight;
        });
        file.ds(standingWeightsIndexed);
    }
    public Optional<RaceInteractionsConfig> load(FileGetter file)  {
        try {
            return Optional.of(ConfigMapper.map(file));
        } catch (Exception e) {
            log.warn("Could load config from save game", e);
            return Optional.empty();
        }
    }
}
