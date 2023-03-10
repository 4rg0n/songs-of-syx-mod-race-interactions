package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.config.version.V1ConfigMapper;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.util.Optional;

/**
 * Takes care of saving and loading {@link RaceInteractionsConfig} into and from the save game
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigSaveService {
    private final static Logger log = Loggers.getLogger(ConfigSaveService.class);

    private final V1ConfigMapper configMapper;

    @Getter(lazy = true)
    private final static ConfigSaveService instance = new ConfigSaveService(V1ConfigMapper.getInstance());

    public void save(FilePutter file, RaceInteractionsConfig config) {
        log.debug("Saving RaceInteractionsConfig into save file");
        configMapper.toSaveGame(file, config);
    }
    public Optional<RaceInteractionsConfig> load(FileGetter file)  {
        log.debug("Loading RaceInteractionsConfig from save file");
        try {
            RaceInteractionsConfig config = configMapper.fromSaveGame(file);
            return Optional.of(config);
        } catch (Exception e) {
            log.warn("Could load config from save file", e);
            return Optional.empty();
        }
    }
}
