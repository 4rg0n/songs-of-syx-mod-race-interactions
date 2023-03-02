package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore();

    private RaceInteractionsConfig currentConfig;
    @Getter
    private final RaceInteractionsConfig defaultConfig = RaceInteractionsConfig.Default.getConfig();
    private RaceInteractionsConfig modConfig;
    private RaceInteractionsConfig profileConfig;

    @Setter
    private RaceInteractionsConfig saveConfig;

    private final ConfigJsonService configJsonService = ConfigJsonService.getInstance();
    private final ConfigSaveService configSaveService = ConfigSaveService.getInstance();

    public void setCurrentConfig(RaceInteractionsConfig currentConfig) {
        log.debug("Set current config to: %s", currentConfig);
        this.currentConfig = currentConfig;
    }

    /**
     * Load config from profile or mod or use default
     */
    public RaceInteractionsConfig loadJsonOrDefault() {
        return getProfileConfig()
            .orElseGet(() -> getModConfig()
                .orElseGet(this::getDefaultConfig));
    }

    public Optional<RaceInteractionsConfig> getCurrentConfig() {
        return Optional.ofNullable(currentConfig);
    }

    public Optional<RaceInteractionsConfig> getModConfig() {
        if (modConfig != null) {
            return Optional.of(modConfig);
        }

        return configJsonService.loadModConfig().map(config -> {
            modConfig = config;
            return config;
        });
    }

    public Optional<RaceInteractionsConfig> getProfileConfig() {
        if (profileConfig != null) {
            return Optional.of(profileConfig);
        }

        return configJsonService.loadProfileConfig().map(config -> {
            profileConfig = config;
            return config;
        });
    }

    public Optional<RaceInteractionsConfig> getSaveConfig() {
        return Optional.ofNullable(saveConfig);
    }

    public void saveProfileConfig(RaceInteractionsConfig config) {
        configJsonService.saveProfileConfig(config);
    }

    public void saveGame(FilePutter filePutter) {
        getCurrentConfig().ifPresent(config -> {
            configSaveService.save(filePutter, config);
            setSaveConfig(config);
        });
    }

    public Optional<RaceInteractionsConfig> loadSave(FileGetter fileGetter) {
        Optional<RaceInteractionsConfig> raceInteractionsConfig = configSaveService.load(fileGetter).map(config -> {
            setSaveConfig(config);
            return config;
        });

        return raceInteractionsConfig;
    }
}
