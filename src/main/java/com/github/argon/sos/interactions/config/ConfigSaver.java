package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.race.RacePrefCategory;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigSaver {
    private final static Logger log = Loggers.getLogger(ConfigSaver.class);
    public void save(FilePutter file, RaceInteractionsConfig config) {
        log.debug("Saving Race Interactions Config into save game.");
        file.bool(config.isCustomOnly());
        file.bool(config.isHonorCustom());

        double[] weightsIndexed = new double[RacePrefCategory.values().length];
        config.getRacePreferenceWeightMap().forEach((category, weight) -> {
            weightsIndexed[category.getIndex()] = weight;
        });

        file.ds(weightsIndexed);
    }
    public RaceInteractionsConfig load(FileGetter file) throws IOException {
        log.debug("Loading Race Interactions Config from save game.");
        boolean customOnly = file.bool();
        boolean honorCustom = file.bool();
        double[] weightsIndexed = new double[RacePrefCategory.values().length];
        file.ds(weightsIndexed);

        Map<RacePrefCategory, Double> weightsMap = new HashMap<>(RacePrefCategory.values().length);

        for (int i = 0, weightsIndexedLength = weightsIndexed.length; i < weightsIndexedLength; i++) {
            double weight = weightsIndexed[i];
            RacePrefCategory category = RacePrefCategory.valueOfIndex(i);
            weightsMap.put(category, weight);
        }

        return RaceInteractionsConfig.builder()
                .customOnly(customOnly)
                .honorCustom(honorCustom)
                .gameRaces(ConfigUtil.loadModConfig().getGameRaces())
                .racePreferenceWeightMap(weightsMap)
                .build();
    }
    public void clear() {

    }
}
