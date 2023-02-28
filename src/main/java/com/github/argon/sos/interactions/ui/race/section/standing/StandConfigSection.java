package com.github.argon.sos.interactions.ui.race.section.standing;

import com.github.argon.sos.interactions.Mapper;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.ui.HorizontalLine;
import com.github.argon.sos.interactions.ui.VerticalLine;
import init.sprite.UI.UI;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.INT;
import util.gui.misc.GText;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the section for setting up custom weights.
 * It contains the checkboxes and sliders.
 */
@Getter
public class StandConfigSection extends GuiSection {
    private final StandWeightSliderSection standWeightSliderSection;
    private final RaceInteractionsConfig config;

    private final StandOverviewSection standOverviewSection;

    public StandConfigSection(RaceInteractionsConfig config, int width) {
        this.config = config;
        this.standWeightSliderSection = StandWeightSliderSection.build(config);
        this.standOverviewSection = new StandOverviewSection();

        GuiSection container = new GuiSection();
        container.addRight(0, standWeightSliderSection);
        container.addRight(0, new VerticalLine(11, standWeightSliderSection.body().height(), 1));
        container.addRight(5, standOverviewSection);

        addDownC(0, new GText(UI.FONT().M, "Boosts standings when liked races are close together"));
        addDownC(0, new HorizontalLine(width, 20, 1, true));
        addDownC(5, container);
    }

    public void applyConfig(Map<RaceStandingCategory, Double> standWeightMap) {
        Map<RaceStandingCategory, INT.INTE> sliderValues = standWeightSliderSection.getSliderValues();

        sliderValues.forEach((category, value) -> {
            int weight = Mapper.fromWeightToSlider(standWeightMap.get(category));
            value.set(weight);
        });
    }

    public Map<RaceStandingCategory, Double> getWeights() {
        Map<RaceStandingCategory, Double> standWeightMap = new HashMap<>();
        getStandWeightSliderSection().getSliderValues().forEach((category, inte) -> {
            double value =  Mapper.fromSliderToWeight(inte.get());
            standWeightMap.put(category, value);
        });

        return standWeightMap;
    }
}
