package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.ai.PlanRaceInteract;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.ui.race.RaceInteractionsConfigPanel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * For handling json and save game config.
 * Stores different configs from different sources e.g. {@link ConfigStore#saveConfig}.
 * Holds the {@link ConfigStore#currentConfig}, which is used by {@link RaceInteractionsConfigPanel}
 * and the {@link PlanRaceInteract} classes. It serves as storage of the current used configuration.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore();

    private RaceInteractionsConfig currentConfig;
    private RaceInteractionsConfig modConfig;

    @Getter
    private final List<RaceInteractionsConfig> otherModConfigs = new ArrayList<>();

    @Setter
    private RaceInteractionsConfig saveConfig;

    private final ConfigJsonService configJsonService = ConfigJsonService.getInstance();
    private final ConfigSaveService configSaveService = ConfigSaveService.getInstance();

    /**
     * Used by the mod as current configuration to apply and use
     */
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
                .orElse(RaceInteractionsConfig.Default.getConfig()));
    }

    public Optional<RaceInteractionsConfig> getCurrentConfig() {
        return Optional.ofNullable(currentConfig);
    }

    /**
     * @return configuration provided by the mod-files
     */
    public Optional<RaceInteractionsConfig> getModConfig() {
        if (modConfig != null) {
            return Optional.of(modConfig);
        }

        return configJsonService.loadModConfig().map(config -> {
            log.trace("Setting mod config to: %s", config);
            modConfig = config;
            return config;
        });
    }

    /**
     * @return configuration from the games user profile
     */
    public Optional<RaceInteractionsConfig> getProfileConfig() {
        return configJsonService.loadProfileConfig();
    }

    /**
     * @return configuration, which was loaded from the save game
     */
    public Optional<RaceInteractionsConfig> getSaveConfig() {
        return Optional.ofNullable(saveConfig);
    }

    /**
     * Save into games user profile
     */
    public void saveProfileConfig(RaceInteractionsConfig config) {
        configJsonService.saveProfileConfig(config);
    }

    /**
     * Save into save game
     */
    public void saveGame(FilePutter filePutter) {
        getCurrentConfig().ifPresent(config -> {
            configSaveService.save(filePutter, config);
            setSaveConfig(config);
        });
    }

    /**
     * @return configuration read from save game
     */
    public Optional<RaceInteractionsConfig> loadSave(FileGetter fileGetter) {
        return configSaveService.load(fileGetter).map(config -> {
            setSaveConfig(config);
            return config;
        });
    }
}
