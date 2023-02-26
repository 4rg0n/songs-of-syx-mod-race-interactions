package com.github.argon.sos.interactions.ui.race.section;

import com.github.argon.sos.interactions.Mapper;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.race.RacePrefCategory;
import com.github.argon.sos.interactions.ui.Checkbox;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.INT;
import util.gui.misc.GButt;

import java.util.Map;

@Getter
public class ConfigSection extends GuiSection {

    private final GButt.CheckboxTitle onlyCustomRaces;
    private final GButt.CheckboxTitle honorCustomRaces;

    private final WeightSliderSection weightSliderSection;

    private final RaceInteractionsConfig config;

    public ConfigSection(RaceInteractionsConfig config) {
        this.config = config;
        this.onlyCustomRaces = new Checkbox("Only custom races");
        onlyCustomRaces.hoverInfoSet("Will not manipulate any vanilla game races when checked");
        onlyCustomRaces.selectedSet(config.isCustomOnly());

        this.honorCustomRaces = new Checkbox("Honor custom races likings");
        honorCustomRaces.hoverInfoSet("Will not manipulate custom mod races likings to vanilla races when checked");
        honorCustomRaces.selectedSet(config.isHonorCustom());

        this.weightSliderSection = WeightSliderSection.build(config);

        addDown(5, onlyCustomRaces);
        addDown(5, honorCustomRaces);
        addDown(20, weightSliderSection);
    }

    public void apply(RaceInteractionsConfig config) {
        onlyCustomRaces.selectedSet(config.isCustomOnly());
        honorCustomRaces.selectedSet(config.isHonorCustom());

        Map<RacePrefCategory, INT.INTE> sliderValues = weightSliderSection.getSliderValues();

        sliderValues.forEach((category, value) -> {
            int weight = Mapper.fromWeightToSlider(config.getRacePreferenceWeightMap().get(category));
            value.set(weight);
        });
    }
}
