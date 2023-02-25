package com.github.argon.sos.interactions.ui.race.section;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;

@Getter
public class ConfigSection extends GuiSection {

    private final GButt.CheckboxTitle onlyCustomRaces;
    private final GButt.CheckboxTitle honorCustomRaces;

    private final WeightSliderSection weightSliderSection;

    private final RaceInteractionsConfig config;

    public ConfigSection(RaceInteractionsConfig config) {
        this.config = config;
        this.onlyCustomRaces = new GButt.CheckboxTitle("Only custom races");
        onlyCustomRaces.hoverInfoSet("Will not manipulate any vanilla game races when checked");

        this.honorCustomRaces = new GButt.CheckboxTitle("Honor custom races likings");
        honorCustomRaces.hoverInfoSet("Will not manipulate custom mod races likings to vanilla races when checked");

        this.weightSliderSection = WeightSliderSection.build(config);

        addDown(5, onlyCustomRaces);
        addDown(5, honorCustomRaces);
        addDown(20, weightSliderSection);
    }


}
