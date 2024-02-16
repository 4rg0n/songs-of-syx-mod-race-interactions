package com.github.argon.sos.interactions.ui.race.section.preference;

import com.github.argon.sos.interactions.Mapper;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RacePrefCategory;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.INT;
import util.gui.misc.GButt;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the section for setting up custom weights.
 * It contains the checkboxes and sliders.
 */
@Getter
public class PrefConfigSection extends GuiSection {
    private final GButt.Checkbox onlyCustomRaces;
    private final GButt.Checkbox honorCustomRaces;
    private final PrefSliderSection prefSliderSection;

    public PrefConfigSection(RaceInteractionsConfig config) {
        this.onlyCustomRaces = new GButt.Checkbox("Only custom races");
        onlyCustomRaces.hoverInfoSet("Will not manipulate any vanilla game races when checked");
        onlyCustomRaces.selectedSet(config.isCustomRaceOnly());

        this.honorCustomRaces = new GButt.Checkbox("Honor custom races likings");
        honorCustomRaces.hoverInfoSet("Will not manipulate custom mod races likings to vanilla races when checked");
        honorCustomRaces.selectedSet(config.isHonorCustomRaceLikings());

        this.prefSliderSection = PrefSliderSection.build(config);

        addDown(5, onlyCustomRaces);
        addDown(5, honorCustomRaces);
        addDown(20, prefSliderSection);
    }

    public void applyConfig(boolean onlyCustom, boolean honorCustom, Map<RacePrefCategory, Double> prefWeightMap) {
        onlyCustomRaces.selectedSet(onlyCustom);
        honorCustomRaces.selectedSet(honorCustom);

        Map<RacePrefCategory, INT.INTE> sliderValues = prefSliderSection.getSliderValues();

        sliderValues.forEach((category, value) -> {
            int weight = Mapper.fromWeightToSlider(prefWeightMap.get(category));
            value.set(weight);
        });
    }

    public boolean isOnlyCustom() {
        return getOnlyCustomRaces().selectedIs();
    }

    public boolean isHonorCustom() {
        return getHonorCustomRaces().selectedIs();
    }

    public Map<RacePrefCategory, Double> getWeights() {
        Map<RacePrefCategory, Double> prefWeightMap = new HashMap<>();
        getPrefSliderSection().getSliderValues().forEach((category, inte) -> {
            double value =  Mapper.fromSliderToWeight(inte.get());
            prefWeightMap.put(category, value);
        });

        return prefWeightMap;
    }
}
