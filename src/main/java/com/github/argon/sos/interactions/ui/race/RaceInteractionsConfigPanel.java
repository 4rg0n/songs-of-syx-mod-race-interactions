package com.github.argon.sos.interactions.ui.race;

import com.github.argon.sos.interactions.Mapper;
import com.github.argon.sos.interactions.RaceInteractions;
import com.github.argon.sos.interactions.RaceInteractionsModScript;
import com.github.argon.sos.interactions.config.ConfigUtil;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.race.RacePrefCategory;
import com.github.argon.sos.interactions.race.RaceService;
import com.github.argon.sos.interactions.ui.race.section.ButtonSection;
import com.github.argon.sos.interactions.ui.race.section.ConfigSection;
import com.github.argon.sos.interactions.ui.race.section.RaceTableSection;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import view.interrupter.ISidePanel;

import java.util.HashMap;
import java.util.Map;

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
            RaceInteractionsConfig appliedConfig = applyConfig();
            ConfigUtil.saveProfileConfig(appliedConfig);
        });
        // Reset to mod configuration button
        buttonSection.getResetModButton().clickActionSet(() -> {
            RaceInteractionsConfig modConfig = ConfigUtil.loadModConfig();
            raceInteractions.manipulateRaceLikings(modConfig);
            ConfigUtil.saveProfileConfig(modConfig);
            configSection.apply(modConfig);
        });
        // Reset to vanilla race likings button
        buttonSection.getResetVanillaButton().clickActionSet(() ->
            raceInteractions.applyRaceLikings(RaceService.getVanillaLikings())
        );

        section().addDownC(10, configSection);
        section().addDownC(10, buttonSection);
        section().addDownC(30, overviewSection);
    }

    private RaceInteractionsConfig applyConfig() {
        boolean onlyCustom = configSection.getOnlyCustomRaces().selectedIs();
        boolean honorCustom = configSection.getHonorCustomRaces().selectedIs();

        Map<RacePrefCategory, Double> prefWeightMap = new HashMap<>();
        configSection.getWeightSliderSection().getSliderValues().forEach((category, inte) -> {
            double value =  Mapper.fromSliderToWeight(inte.get());
            prefWeightMap.put(category, value);
        });

        RaceInteractionsConfig config = RaceInteractionsConfig.builder()
                .customOnly(onlyCustom)
                .honorCustom(honorCustom)
                .racePreferenceWeightMap(prefWeightMap)
                .gameRaces(ConfigUtil.getDefaultGameRaces())
                .build();
        raceInteractions.manipulateRaceLikings(config);

        return config;
    }
}
