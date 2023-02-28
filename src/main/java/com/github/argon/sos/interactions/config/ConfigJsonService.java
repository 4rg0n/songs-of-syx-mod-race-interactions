package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import init.paths.PATH;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import snake2d.Errors;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.nio.file.Path;
import java.util.Optional;

import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.FILE_NAME;

/**
 * For saving, loading and building {@link RaceInteractionsConfig} as json
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigJsonService {
    private final static Logger log = Loggers.getLogger(ConfigJsonService.class);

    private static RaceInteractionsConfig MOD_CONFIG;

    @Getter(lazy = true)
    private final static ConfigJsonService instance = new ConfigJsonService();

    /**
     * Loads mod configuration once.
     *
     * @return already loaded config
     */
    public RaceInteractionsConfig loadModConfig() {
        if (MOD_CONFIG == null) {
            PATH configPath = PATHS.INIT().getFolder("config");
            MOD_CONFIG = load(configPath).orElseGet(RaceInteractionsConfig.Default::getConfig);
        }

        return MOD_CONFIG;
    }

    /**
     * Loads mod configuration from profile once.
     *
     * @return already loaded config
     */
    public Optional<RaceInteractionsConfig> loadProfileConfig() {
        PATH profiePath = PATHS.local().PROFILE;
        return load(profiePath);
    }

    public void saveProfileConfig(RaceInteractionsConfig config) {
        PATH profiePath = PATHS.local().PROFILE;

        log.debug("Saving configuration into profile %s", profiePath.get().toString());
        try {
            JsonE configJson = ConfigMapper.toJson(config);

            // blueprint save file exists?
            Path path;
            if (!profiePath.exists(FILE_NAME)) {
                path = profiePath.create(FILE_NAME);
                log.debug("Created new configuration profile file %s", path.toString());
            } else {
                path = profiePath.get(FILE_NAME);
            }

            boolean success = configJson.save(path);

            log.debug("Saving to %s was successful? %s", path.toString(), success);
        } catch (Errors.DataError e) {
            log.warn("Could not save configuration into profile: %s", e.getMessage());
        } catch (Exception e) {
            log.error("Could not save configuration into profile", e);
        }
    }

    public Optional<RaceInteractionsConfig> load(PATH path) {
        if (!path.exists(FILE_NAME)) {
            // do not load what's not there
            log.debug("Configuration %s/%s not present using defaults.", path.get().toString(), FILE_NAME);
            return Optional.empty();
        }

        Json json;
        Path loadPath = path.get(FILE_NAME);

        try {
            log.debug("Loading config from %s", FILE_NAME, loadPath.toString());
            json = new Json(loadPath);
        }  catch (Exception e) {
            log.error("Could not load %s config from %s", FILE_NAME, loadPath.toString(), e);
            return Optional.empty();
        }

        return Optional.of(ConfigMapper.map(json));
    }
}
