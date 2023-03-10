package com.github.argon.sos.interactions.ui.race.section.standing;

import com.github.argon.sos.interactions.Mapper;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.ui.element.HorizontalLine;
import com.github.argon.sos.interactions.ui.element.VerticalLine;
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
    private final static Logger log = Loggers.getLogger(StandConfigSection.class);
    private final StandSliderSection standSliderSection;
    private final RaceInteractionsConfig config;

    private final StandOverviewSection standOverviewSection;

    public StandConfigSection(RaceInteractionsConfig config, int width) {
        this.config = config;
        this.standSliderSection = StandSliderSection.build(config);
        this.standOverviewSection = new StandOverviewSection();

        GuiSection container = new GuiSection();
        container.addRight(0, standSliderSection);
        container.addRight(10, new VerticalLine(5, standSliderSection.body().height(), 1));
        container.addRight(5, standOverviewSection);

        addDownC(0, new GHeader("Boost standings when liked races are nearby"));
        addDownC(0, new HorizontalLine(width, 20, 1, true));
        addDownC(5, container);
    }

    /**
     * Displays set configuration in the UI
     */
    public void applyConfig(Map<RaceStandingCategory, Double> standWeightMap, int raceLookRange) {
        Map<RaceStandingCategory, INT.INTE> sliderValues = standSliderSection.getSliderValues();

        sliderValues.forEach((category, value) -> {
            int weight = Mapper.fromWeightToSlider(standWeightMap.get(category));
            value.set(weight);
            log.trace("Set slider %s to %s", category.name(), weight);
        });

        standSliderSection.getRaceLookRangeValue().set(raceLookRange);
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
}
