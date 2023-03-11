package com.github.argon.sos.interactions.config;

import com.github.argon.sos.interactions.ai.PlanRaceInteract;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.ui.race.RaceInteractionsConfigPanel;
import com.github.argon.sos.interactions.util.ClipboardUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;

import java.nio.file.Path;
import java.util.Optional;

/**
 * For handling json and save game config.
 * Stores different configs from different sources e.g. {@link ConfigStore#saveConfig}.
 * Holds the {@link ConfigStore#currentConfig}, which is used by {@link RaceInteractionsConfigPanel}
 * and the {@link PlanRaceInteract} classes. It serves as storage of the current used configuration.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigStore {
    private final static Logger log = Loggers.getLogger(ConfigStore.class);

    @Getter(lazy = true)
    private final static ConfigStore instance = new ConfigStore(
        ConfigJsonService.getInstance(),
        ConfigSaveService.getInstance(),
        ConfigDecoderEncoder.getInstance()
    );

    private RaceInteractionsConfig currentConfig;
    private RaceInteractionsConfig modConfig;

    @Setter
    private RaceInteractionsConfig saveConfig;

    private final ConfigJsonService configJsonService;
    private final ConfigSaveService configSaveService;

    private final ConfigDecoderEncoder configDecoderEncoder;


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
    public boolean saveProfileConfig(RaceInteractionsConfig config) {
        return configJsonService.saveProfileConfig(config);
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

    public Optional<RaceInteractionsConfig> loadJson(Path path) {
        return configJsonService.load(path);
    }

    /**
     * Saves {@link RaceInteractionsConfig} encoded into the system clipboard.
     * See {@link ConfigDecoderEncoder#encode(RaceInteractionsConfig)}
     *
     * @return success
     */
    public boolean toClipboard(RaceInteractionsConfig config) {
        log.debug("Writing config to clipboard");
        log.trace("CONFIG: %s", config);

        return configDecoderEncoder.encode(config)
            .map(ClipboardUtil::write)
            .orElse(false);
    }

    /**
     * Reads encoded {@link RaceInteractionsConfig} from system clipboard.
     * See {@link ConfigDecoderEncoder#decode(String)}
     */
    public Optional<RaceInteractionsConfig> fromClipboard() {
        log.debug("Reading config from clipboard");
        return ClipboardUtil.read()
           .flatMap(configDecoderEncoder::decode);
    }
}
