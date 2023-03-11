package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.config.mapper.ConfigMappers;
import com.github.argon.sos.interactions.config.mapper.version.ConfigMapper;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import init.paths.PATH;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.Errors;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.nio.file.Path;
import java.util.Optional;

import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.FILE_NAME;

/**
 * For saving and loading {@link RaceInteractionsConfig} as json
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigJsonService {
    private final static Logger log = Loggers.getLogger(ConfigJsonService.class);

    private final ConfigMapper configMapper;

    @Getter(lazy = true)
    private final static ConfigJsonService instance = new ConfigJsonService(ConfigMappers.getInstance());

    /**
     * Configuration provided by the mod-files
     */
    public Optional<RaceInteractionsConfig> loadModConfig() {
        PATH configPath = PATHS.INIT().getFolder("config");
        return load(configPath);
    }

    /**
     * Configuration from games user profile
     */
    public Optional<RaceInteractionsConfig> loadProfileConfig() {
        PATH profiePath = PATHS.local().PROFILE;
        return load(profiePath);
    }

    public boolean saveProfileConfig(RaceInteractionsConfig config) {
        PATH profiePath = PATHS.local().PROFILE;

        log.debug("Saving configuration into profile %s", profiePath.get().toString());
        log.trace("CONFIG: %s", config);
        try {
            JsonE configJson = configMapper.toJson(config);

            // blueprint save file exists?
            Path path;
            if (!profiePath.exists(FILE_NAME)) {
                path = profiePath.create(FILE_NAME);
                log.debug("Created new configuration profile file %s", path);
            } else {
                path = profiePath.get(FILE_NAME);
            }

            boolean success = configJson.save(path);

            log.debug("Saving to %s was successful? %s", path, success);

            return success;
        } catch (Errors.DataError e) {
            log.warn("Could not save configuration into profile: %s", e.getMessage());
        } catch (Exception e) {
            log.error("Could not save configuration into profile", e);
        }

        return false;
    }

    public Optional<RaceInteractionsConfig> load(PATH path) {
        log.debug("Loading json config from %s", path.get());
        if (!path.exists(FILE_NAME)) {
            // do not load what's not there
            log.debug("Configuration %s/%s not present using defaults.", path.get(), FILE_NAME);
            return Optional.empty();
        }

        Path loadPath = path.get(FILE_NAME);
        return load(loadPath);
    }

    public Optional<RaceInteractionsConfig> load(Path path) {
        Json json;

        try {
            json = new Json(path);
        }  catch (Exception e) {
            log.info("Could not load json config from %s", path.toString(), e);
            return Optional.empty();
        }

        try {
            RaceInteractionsConfig config = configMapper.fromJson(json);
            log.trace("CONFIG: %s", config);
            return Optional.of(config);
        } catch (Errors.DataError e) {
            log.info("Could not load json config from %s", path.toString(), e);
            return Optional.empty();
        }
    }
}
