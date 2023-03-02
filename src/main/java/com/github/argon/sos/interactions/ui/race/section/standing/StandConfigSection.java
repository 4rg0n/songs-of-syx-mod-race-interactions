package com.github.argon.sos.interactions.ui.race.section.standing;

import com.github.argon.sos.interactions.Mapper;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.ui.HorizontalLine;
import com.github.argon.sos.interactions.ui.VerticalLine;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.INT;
import util.gui.misc.GHeader;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the section for setting up custom weights.
 * It contains the checkboxes and sliders.
 */
@Getter
public class StandConfigSection extends GuiSection {
    private final StandSliderSection standSliderSection;
    private final RaceInteractionsConfig config;

    private final StandOverviewSection standOverviewSection;

    public StandConfigSection(RaceInteractionsConfig config, int width) {
        this.config = config;
        this.standSliderSection = StandSliderSection.build(config);
        this.standOverviewSection = new StandOverviewSection();

        GuiSection container = new GuiSection();
        container.addRight(0, standSliderSection);
        container.addRight(0, new VerticalLine(11, standSliderSection.body().height(), 1));
        container.addRight(5, standOverviewSection);

        addDownC(0, new GHeader("Boost standings when liked races are nearby"));
        addDownC(0, new HorizontalLine(width, 20, 1, true));
        addDownC(5, container);
    }

    public void applyConfig(Map<RaceStandingCategory, Double> standWeightMap, int raceLookRange, boolean raceBoostSelf) {
        Map<RaceStandingCategory, INT.INTE> sliderValues = standSliderSection.getSliderValues();

        sliderValues.forEach((category, value) -> {
            int weight = Mapper.fromWeightToSlider(standWeightMap.get(category));
            value.set(weight);
        });

        standSliderSection.getRaceLookRangeValue().set(raceLookRange);
        standSliderSection.getRaceBoostSelfCheck().selectedSet(raceBoostSelf);
    }

    public Map<RaceStandingCategory, Double> getWeights() {
        Map<RaceStandingCategory, Double> standWeightMap = new HashMap<>();
        getStandSliderSection().getSliderValues().forEach((category, inte) -> {
            double value =  Mapper.fromSliderToWeight(inte.get());
            standWeightMap.put(category, value);
        });

        return standWeightMap;
    }

    public int getRaceLookRangeValue() {
        return standSliderSection.getRaceLookRangeValue().get();
    }

    public boolean isRaceBoostSelf() {
        return standSliderSection.getRaceBoostSelfCheck().selectedIs();
    }
}
