package com.github.argon.sos.interactions.ui.race;

import com.github.argon.sos.interactions.RaceInteractions;
import com.github.argon.sos.interactions.RaceInteractionsModScript;
import com.github.argon.sos.interactions.config.ConfigJsonService;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.ui.HorizontalLine;
import com.github.argon.sos.interactions.ui.VerticalLine;
import com.github.argon.sos.interactions.ui.race.section.preference.ButtonSection;
import com.github.argon.sos.interactions.ui.race.section.preference.PrefConfigSection;
import com.github.argon.sos.interactions.ui.race.section.preference.RaceTableSection;
import com.github.argon.sos.interactions.ui.race.section.standing.StandConfigSection;
import com.github.argon.sos.interactions.util.RaceUtil;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import view.interrupter.ISidePanel;

/**
 * Contains all the ui elements for the configuration.
 * Is displayed on the left side of the game.
 */
@Getter
public class RaceInteractionsConfigPanel extends ISidePanel {
    public final static String TITLE = RaceInteractionsModScript.MOD_INFO.name.toString();

    private final PrefConfigSection prefConfigSection;
    private final RaceTableSection overviewSection;
    private final ButtonSection buttonSection;

    private final StandConfigSection standConfigSection;

    private final RaceInteractions raceInteractions;
    private final ConfigJsonService configJsonService;

    public RaceInteractionsConfigPanel(
        RaceInteractions raceInteractions,
        PrefConfigSection prefConfigSection,
        RaceTableSection overviewSection,
        ButtonSection buttonSection,
        StandConfigSection standConfigSection,
        ConfigJsonService configJsonService,
        int width
    ) {
        this.section = new GuiSection();
        titleSet(TITLE);
        section().body().setWidth(width);

        this.raceInteractions = raceInteractions;
        this.prefConfigSection = prefConfigSection;
        this.overviewSection = overviewSection;
        this.buttonSection = buttonSection;
        this.configJsonService = configJsonService;
        this.standConfigSection = standConfigSection;

        // Apply button
        buttonSection.getApplyButton().clickActionSet(() -> {
            RaceInteractionsConfig config = getConfig();
            raceInteractions.manipulateRaceLikings(config);
            RaceInteractionsConfig.setCurrent(config);
            buttonSection.markApplied();
        });
        // Reset to mod configuration button
        buttonSection.getResetModButton().clickActionSet(() -> {
            RaceInteractionsConfig modConfig = configJsonService.loadModConfig();
            prefConfigSection.applyConfig(
                modConfig.isCustomOnly(),
                modConfig.isHonorCustom(),
                modConfig.getRacePreferenceWeightMap()
            );
            buttonSection.markUnApplied();
        });
        // Reset to vanilla race likings button
        buttonSection.getResetVanillaButton().clickActionSet(() ->
            raceInteractions.applyRaceLikings(RaceUtil.getVanillaLikings())
        );
        // Save settings to user button
        buttonSection.getSaveProfileButton().clickActionSet(() -> {
            RaceInteractionsConfig config = getConfig();
            configJsonService.saveProfileConfig(config);
        });
        // Load settings from user profile button
        buttonSection.getLoadProfileButton().clickActionSet(() ->
            configJsonService.loadProfileConfig().ifPresent(config -> {
                prefConfigSection.applyConfig(
                    config.isCustomOnly(),
                    config.isHonorCustom(),
                    config.getRacePreferenceWeightMap()
                );
                buttonSection.markUnApplied();
            })
        );

        GuiSection container = new GuiSection();
        container.addRight(0, prefConfigSection);
        container.addRight(5, new VerticalLine( 11, prefConfigSection.body().height(), 1));
        container.addRight(5, buttonSection);

        section().addDownC(10, container);
        section().addDownC(10, new HorizontalLine(width, 20, 1, true));
        section().addDownC(0, overviewSection);
        section().addDownC(0, new HorizontalLine(width, 20, 1, true));

        section().addDownC(0, standConfigSection);
    }

    public RaceInteractionsConfig getConfig() {
        return RaceInteractionsConfig.builder()
                .gameRaces(RaceInteractionsConfig.getCurrent()
                        .orElse(RaceInteractionsConfig.Default.getConfig()).getGameRaces())
                .raceLookRange(standConfigSection.getRaceLookRangeValue())
                .raceBoostSelf(standConfigSection.isRaceBoostSelf())
                .racePreferenceWeightMap(prefConfigSection.getWeights())
                .raceStandingWeightMap(standConfigSection.getWeights())
                .honorCustom(prefConfigSection.isHonorCustom())
                .customOnly(prefConfigSection.isOnlyCustom())
                .build();
    }
}
