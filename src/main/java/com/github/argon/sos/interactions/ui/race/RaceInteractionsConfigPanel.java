package com.github.argon.sos.interactions.ui.race;

import com.github.argon.sos.interactions.RaceInteractions;
import com.github.argon.sos.interactions.RaceInteractionsModScript;
import com.github.argon.sos.interactions.config.ConfigStore;
import com.github.argon.sos.interactions.config.OtherModInfo;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.game.api.GameModApi;
import com.github.argon.sos.interactions.game.api.GameRaceApi;
import com.github.argon.sos.interactions.game.api.GameUiApi;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.ui.element.Button;
import com.github.argon.sos.interactions.ui.element.HorizontalLine;
import com.github.argon.sos.interactions.ui.element.VerticalLine;
import com.github.argon.sos.interactions.ui.race.section.ButtonMenuSection;
import com.github.argon.sos.interactions.ui.race.section.ButtonSection;
import com.github.argon.sos.interactions.ui.race.section.ImportOtherModSection;
import com.github.argon.sos.interactions.ui.race.section.preference.PrefConfigSection;
import com.github.argon.sos.interactions.ui.race.section.preference.RaceTableSection;
import com.github.argon.sos.interactions.ui.race.section.standing.StandConfigSection;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.table.GScrollRows;
import view.interrupter.ISidePanel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private final GameModApi gameModApi;


    private double updateTimerSeconds = 0d;

    private final static int UPDATE_INTERVAL_SECONDS = 1;

    public RaceInteractionsConfigPanel(
        RaceInteractions raceInteractions,
        PrefConfigSection prefConfigSection,
        RaceTableSection raceTableSection,
        ButtonSection buttonSection,
        ButtonMenuSection buttonMenuSection,
        StandConfigSection standConfigSection,
        GameRaceApi gameRaceApi,
        GameUiApi gameUiApi,
        GameModApi gameModApi,
        ConfigStore configStore,
        int width
    ) {
        this.section = new GuiSection();
        titleSet(TITLE);
        section().body().setWidth(width);

        this.raceInteractions = raceInteractions;
        this.prefConfigSection = prefConfigSection;
        this.raceTableSection = raceTableSection;
        this.buttonSection = buttonSection;
        this.configStore = configStore;
        this.standConfigSection = standConfigSection;
        this.buttonMenuSection = buttonMenuSection;
        this.gameRaceApi = gameRaceApi;
        this.gameUiApi = gameUiApi;
        this.gameModApi = gameModApi;

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
        Button saveProfileButton = buttonSection.getSaveProfileButton();
        saveProfileButton.clickActionSet(() -> {
            log.debug("Save to Profile click");
            RaceInteractionsConfig config = getConfig();
            boolean success = configStore.saveProfileConfig(config);
            saveProfileButton.markSuccess(success);
        });
        // Load settings from user profile button
        Button loadProfileButton = buttonSection.getLoadProfileButton();
        loadProfileButton.clickActionSet(() -> {
            log.debug("Load from Profile click");
            Optional<RaceInteractionsConfig> optionalConfig = configStore.getProfileConfig();

            if (!optionalConfig.isPresent()) {
                loadProfileButton.markSuccess(false);
                return;
            }

            applyConfig(optionalConfig.get());
            loadProfileButton.markSuccess(true);
        });
        // More / Menu
        Button menuButton = buttonSection.getMenuButton();
        menuButton.clickActionSet(() -> {
            log.debug("More click");
            gameUiApi.showPopup(buttonMenuSection, menuButton);
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
        Button resetModButton = buttonMenuSection.getResetModButton();
        resetModButton.clickActionSet(() -> {
            log.debug("Reset click");
            RaceInteractionsConfig modConfig = configStore.getModConfig()
                .orElse(RaceInteractionsConfig.Default.getConfig());
            boolean success = applyConfig(modConfig);
            resetModButton.markSuccess(success);

            if (success) {
                raceInteractions.applyRaceLikings(this.gameRaceApi.getVanillaLikings());
            }
        });
        // Export button
        Button exportButton = buttonMenuSection.getExportButton();
        exportButton.clickActionSet(() -> {
            log.debug("Export click");
            RaceInteractionsConfig config = getConfig();
            boolean success = configStore.toClipboard(config);
            exportButton.markSuccess(success);
        });
        // Import button
        Button importButton = buttonMenuSection.getImportButton();
        importButton.clickActionSet(() -> {
            log.debug("Import click");
            Optional<RaceInteractionsConfig> optionalConfig = configStore.fromClipboard();

            if (!optionalConfig.isPresent()) {
                importButton.markSuccess(false);
                return;
            }
            log.debug("Applying config from clipboard");
            boolean success = applyConfig(optionalConfig.get());
            importButton.markSuccess(success);
        });
        // Import from other mods button
        Button importOtherModsButton = buttonMenuSection.getImportOtherModsButton();
        importOtherModsButton.clickActionSet(() -> {
            log.debug("Import other mods click");
            List<OtherModInfo> otherMods = gameModApi.findOtherModsWithConfig();
            ImportOtherModSection importOtherModSection = new ImportOtherModSection(otherMods, this, configStore);

            // selection with other mods containing race interactions config
            gameUiApi.showPopup(
                importOtherModSection,
                // position popup right next to button
                importOtherModsButton
            );
        });


        GuiSection container = new GuiSection();
        container.addRight(0, prefConfigSection);
        container.addRight(5, new VerticalLine( 11, prefConfigSection.body().height(), 1));
        container.addRight(5, buttonSection);

        GuiSection center = new GuiSection();
        center.body().setWidth(width);
        center.addDownC(0, container);

        GuiSection containerTable = new GuiSection();
        containerTable.addDownC(10, new HorizontalLine(width, 20, 1, true));
        containerTable.addDownC(0, raceTableSection);
        containerTable.addDownC(0, new HorizontalLine(width, 20, 1, true));

        List<? extends RENDEROBJ> guiSections = Arrays.asList(
            center,
            containerTable,
            standConfigSection);

        GScrollRows gScrollRows = new GScrollRows(guiSections, HEIGHT);
        section().add(gScrollRows.view());
    }

    public RaceInteractionsConfig getConfig() {
        return RaceInteractionsConfig.builder()
            .vanillaRaces(configStore.getCurrentConfig()
                .orElse(RaceInteractionsConfig.Default.getConfig()).getVanillaRaces())
            .raceLookRange(standConfigSection.getRaceLookRangeValue())
            .preferenceWeights(prefConfigSection.getWeights())
            .standingWeights(standConfigSection.getWeights())
            .raceBoostToggles(raceTableSection.getRaceBoostingToggles())
            .honorCustomRaceLikings(prefConfigSection.isHonorCustom())
            .customRaceOnly(prefConfigSection.isOnlyCustom())
            .build();
    }

    public boolean applyConfig(RaceInteractionsConfig config) {
        log.debug("Applying config to ui panel: %s", config);
        try {
            prefConfigSection.applyConfig(
                config.isCustomRaceOnly(),
                config.isHonorCustomRaceLikings(),
                config.getPreferenceWeights()
            );
            standConfigSection.applyConfig(
                config.getStandingWeights(),
                config.getRaceLookRange()
            );
            raceTableSection.apply(config.getRaceBoostToggles());
        } catch (Exception e) {
            log.info("Could not apply configuration to panel: %s", e.getMessage());
            log.trace("", e);
            return false;
        }

        return true;
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
