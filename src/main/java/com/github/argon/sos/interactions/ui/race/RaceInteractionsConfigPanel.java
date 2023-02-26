package com.github.argon.sos.interactions.ui.race;

import com.github.argon.sos.interactions.Mapper;
import com.github.argon.sos.interactions.RaceInteractions;
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

@Getter
public class RaceInteractionsConfigPanel extends ISidePanel {
    public final static String TITLE = "Race Interactions Config";

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
        buttonSection.getApplyButton().clickActionSet(this::applyConfig);
        buttonSection.getResetModButton().clickActionSet(() ->
            raceInteractions.manipulateRaceLikings(RaceInteractionsConfig.load())
        );
        buttonSection.getResetVanillaButton().clickActionSet(() ->
                raceInteractions.applyRaceLikings(RaceService.getVanillaLikings())
        );

        section().addDownC(10, configSection);
        section().addDownC(10, buttonSection);
        section().addDownC(30, overviewSection);
    }

    private void applyConfig() {
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
                .build();

        raceInteractions.manipulateRaceLikings(config);
    }
}
