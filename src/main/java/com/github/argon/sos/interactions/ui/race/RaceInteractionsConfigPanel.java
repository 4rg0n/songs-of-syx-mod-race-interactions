package com.github.argon.sos.interactions.ui.race;

import com.github.argon.sos.interactions.RaceInteractions;
import com.github.argon.sos.interactions.RaceInteractionsModScript;
import com.github.argon.sos.interactions.config.ConfigUtil;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.race.RaceService;
import com.github.argon.sos.interactions.ui.HorizontalLine;
import com.github.argon.sos.interactions.ui.VerticalLine;
import com.github.argon.sos.interactions.ui.race.section.ButtonSection;
import com.github.argon.sos.interactions.ui.race.section.ConfigSection;
import com.github.argon.sos.interactions.ui.race.section.RaceTableSection;
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

    private final ConfigSection configSection;
    private final RaceTableSection overviewSection;
    private final ButtonSection buttonSection;
    private final RaceInteractions raceInteractions;

    public RaceInteractionsConfigPanel(
        RaceInteractions raceInteractions,
        ConfigSection configSection,
        RaceTableSection overviewSection,
        ButtonSection buttonSection,
        int width
    ) {
        this.section = new GuiSection();
        titleSet(TITLE);
        section().body().setWidth(width);

        this.raceInteractions = raceInteractions;
        this.configSection = configSection;
        this.overviewSection = overviewSection;
        this.buttonSection = buttonSection;

        // Apply button
        buttonSection.getApplyButton().clickActionSet(() -> {
            RaceInteractionsConfig config = configSection.getConfig();
            raceInteractions.manipulateRaceLikings(config);
            ConfigUtil.setCurrentConfig(config);
            buttonSection.markApplied();
        });
        // Reset to mod configuration button
        buttonSection.getResetModButton().clickActionSet(() -> {
            RaceInteractionsConfig modConfig = ConfigUtil.loadModConfig();
            configSection.applyConfig(modConfig);
            buttonSection.markUnApplied();
        });
        // Reset to vanilla race likings button
        buttonSection.getResetVanillaButton().clickActionSet(() ->
            raceInteractions.applyRaceLikings(RaceService.getVanillaLikings())
        );
        // Save settings to user  button
        buttonSection.getSaveProfileButton().clickActionSet(() -> {
            RaceInteractionsConfig config = configSection.getConfig();
            ConfigUtil.saveProfileConfig(config);
        });
        // Load settings from user profile button
        buttonSection.getLoadProfileButton().clickActionSet(() -> {
            ConfigUtil.loadProfileConfig().ifPresent(config -> {
                configSection.applyConfig(config);
                buttonSection.markUnApplied();
            });
        });


        GuiSection container = new GuiSection();

        container.addRight(0, configSection);
        container.addRight(5, new VerticalLine( 11, configSection.body().height(), 1));
        container.addRight(5, buttonSection);

        section().addDownC(10, container);
        section().addDownC(0, new HorizontalLine(width, 21, 1));
        section().addDownC(0, overviewSection);
        section().addDownC(0, new HorizontalLine(width, 21, 1));
    }
}
