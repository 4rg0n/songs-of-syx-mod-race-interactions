package com.github.argon.sos.interactions.ui.race;

import com.github.argon.sos.interactions.RaceInteractions;
import com.github.argon.sos.interactions.RaceInteractionsModScript;
import com.github.argon.sos.interactions.config.ConfigStore;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.game.api.GameRaceApi;
import com.github.argon.sos.interactions.game.api.GameUiApi;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.ui.element.HorizontalLine;
import com.github.argon.sos.interactions.ui.element.VerticalLine;
import com.github.argon.sos.interactions.ui.race.section.ButtonMenuSection;
import com.github.argon.sos.interactions.ui.race.section.ButtonSection;
import com.github.argon.sos.interactions.ui.race.section.preference.PrefConfigSection;
import com.github.argon.sos.interactions.ui.race.section.preference.RaceTableSection;
import com.github.argon.sos.interactions.ui.race.section.standing.StandConfigSection;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import view.interrupter.ISidePanel;

/**
 * Contains all the ui elements for the configuration.
 * Is displayed on the left side of the game.
 */
@Getter
public class RaceInteractionsConfigPanel extends ISidePanel {

    private final static Logger log = Loggers.getLogger(RaceInteractionsConfigPanel.class);
    public final static String TITLE = RaceInteractionsModScript.MOD_INFO.name.toString();

    private final PrefConfigSection prefConfigSection;
    private final RaceTableSection raceTableSection;
    private final ButtonSection buttonSection;
    private final ButtonMenuSection buttonMenuSection;
    private final StandConfigSection standConfigSection;

    private final RaceInteractions raceInteractions;
    private final ConfigStore configStore;

    private final GameRaceApi gameRaceApi;
    private final GameUiApi gameUiApi;

    private double updateTimerSeconds = 0d;

    private final static int UPDATE_INTERVAL_SECONDS = 1;

    public RaceInteractionsConfigPanel(
        RaceInteractions raceInteractions,
        PrefConfigSection prefConfigSection,
        RaceTableSection raceTableSection,
        ButtonSection buttonSection,
        ButtonMenuSection buttonMenuSection, StandConfigSection standConfigSection,
        GameRaceApi gameRaceApi, GameUiApi gameUiApi, ConfigStore configStore,
        int width
    ) {
        this.buttonMenuSection = buttonMenuSection;
        this.gameRaceApi = gameRaceApi;
        this.gameUiApi = gameUiApi;
        this.section = new GuiSection();
        titleSet(TITLE);
        section().body().setWidth(width);

        this.raceInteractions = raceInteractions;
        this.prefConfigSection = prefConfigSection;
        this.raceTableSection = raceTableSection;
        this.buttonSection = buttonSection;
        this.configStore = configStore;
        this.standConfigSection = standConfigSection;

        // Undo button
        buttonSection.getUndoButton().clickActionSet(() -> {
            log.debug("Undo click");
                configStore.getCurrentConfig()
                    .ifPresent(this::applyConfig);
            }
        );
        // Apply button
        buttonSection.getApplyButton().clickActionSet(() -> {
            log.debug("Apply click");
            RaceInteractionsConfig config = getConfig();
            raceInteractions.manipulateRaceLikings(config);
            configStore.setCurrentConfig(config);
        });
        // Save settings to user button
        buttonSection.getSaveProfileButton().clickActionSet(() -> {
            log.debug("Save to Profile click");
            RaceInteractionsConfig config = getConfig();
            configStore.saveProfileConfig(config);
        });
        // Load settings from user profile button
        buttonSection.getLoadProfileButton().clickActionSet(() -> {
            log.debug("Load from Profile click");
            configStore.getProfileConfig().ifPresent(this::applyConfig);
        });
        // Menu
        buttonSection.getMenuButton().clickActionSet(() -> {
            log.debug("Menu click");
            gameUiApi.showPopup(buttonMenuSection, buttonSection.getMenuButton());
        });

        // Boost: All button
        standConfigSection.getStandSliderSection().getEnableAllRaceBoostingsButton().clickActionSet(() -> {
            log.debug("Enable all races boosts click");
            raceTableSection.toggleAllRaceBoostings(true);
        });
        // Boost: None button
        standConfigSection.getStandSliderSection().getDisableAllRaceBoostingsButton().clickActionSet(() -> {
            log.debug("Disable all races boosts click");
            raceTableSection.toggleAllRaceBoostings(false);
        });

        // Reset to mod configuration button
        buttonMenuSection.getResetModButton().clickActionSet(() -> {
            log.debug("Reset click");
            RaceInteractionsConfig modConfig = configStore.getModConfig()
                .orElse(RaceInteractionsConfig.Default.getConfig());
            applyConfig(modConfig);
            raceInteractions.applyRaceLikings(this.gameRaceApi.getVanillaLikings());
        });
        // Export button
        buttonMenuSection.getExportButton().clickActionSet(() -> {
            log.debug("Export click");
            RaceInteractionsConfig config = getConfig();
            configStore.toClipboard(config);
        });
        // Export button
        buttonMenuSection.getImportButton().clickActionSet(() -> {
            log.debug("Import click");
            configStore.fromClipboard()
                .ifPresent(config -> {
                    log.debug("Applying config from clipboard");
                    applyConfig(config);
                });
        });


        GuiSection container = new GuiSection();
        container.addRight(0, prefConfigSection);
        container.addRight(5, new VerticalLine( 11, prefConfigSection.body().height(), 1));
        container.addRight(5, buttonSection);

        section().addDownC(10, container);
        section().addDownC(10, new HorizontalLine(width, 20, 1, true));
        section().addDownC(0, raceTableSection);
        section().addDownC(0, new HorizontalLine(width, 20, 1, true));

        section().addDownC(0, standConfigSection);
    }

    public RaceInteractionsConfig getConfig() {
        return RaceInteractionsConfig.builder()
            .gameRaces(configStore.getCurrentConfig()
                .orElse(RaceInteractionsConfig.Default.getConfig()).getGameRaces())
            .raceLookRange(standConfigSection.getRaceLookRangeValue())
            .racePreferenceWeights(prefConfigSection.getWeights())
            .raceStandingWeights(standConfigSection.getWeights())
            .raceBoostingToggles(raceTableSection.getRaceBoostingToggles())
            .honorCustom(prefConfigSection.isHonorCustom())
            .customOnly(prefConfigSection.isOnlyCustom())
            .build();
    }

    public void applyConfig(RaceInteractionsConfig config) {
        log.debug("Applying config to ui panel: %s", config);
        prefConfigSection.applyConfig(
            config.isCustomOnly(),
            config.isHonorCustom(),
            config.getRacePreferenceWeights()
        );
        standConfigSection.applyConfig(
            config.getRaceStandingWeights(),
            config.getRaceLookRange()
        );
        raceTableSection.apply(config.getRaceBoostingToggles());
    }

    @Override
    protected void update(float seconds) {
        updateTimerSeconds += seconds;

        // check for changes in config
        if (updateTimerSeconds >= UPDATE_INTERVAL_SECONDS) {
            updateTimerSeconds = 0d;
            if (isDirty()) {
                buttonSection.markUnApplied();
            } else {
                buttonSection.markApplied();
            }
        }

        super.update(seconds);
    }

    /**
     * @return whether panel configuration is different from {@link ConfigStore#getCurrentConfig()} ()}
     */
    private boolean isDirty() {
        return configStore.getCurrentConfig()
                .map(currentConfig -> !getConfig().equals(currentConfig))
                // no current config in memory
                .orElse(true);
    }
}
