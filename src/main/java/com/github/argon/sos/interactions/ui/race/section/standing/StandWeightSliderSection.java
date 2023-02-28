package com.github.argon.sos.interactions.ui.race.section.standing;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.ui.Checkbox;
import com.github.argon.sos.interactions.ui.Slider;
import com.github.argon.sos.interactions.util.SimpleINTE;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.INT;
import util.gui.misc.GButt;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.argon.sos.interactions.Mapper.*;
import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.MAX_WEIGHT;
import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.MIN_WEIGHT;

@Getter
public class StandWeightSliderSection extends GuiSection {
    private final Map<RaceStandingCategory, Slider> sliders;
    private final Map<RaceStandingCategory, INT.INTE> sliderValues;

    private StandWeightSliderSection(Map<RaceStandingCategory, Slider> sliders, Map<RaceStandingCategory, INT.INTE> sliderValues) {
        this.sliders = sliders;
        this.sliderValues = sliderValues;

        GuiSection slidersSection = new GuiSection();
        GuiSection namesSection = new GuiSection();
        GuiSection checkboxSection = new GuiSection();

        sliders.forEach((category, slider) -> {
            GButt.Glow name = new GButt.Glow(category.name());
            name.hoverInfoSet("How much " + category.name() + " shall be boosted");
            name.clickSoundSet(null);
            name.clickActionSet(null);
            name.body.setHeight(slider.body().height());

            INT.INTE value = sliderValues.get(category);
            boolean sliderEnabled = true;
            if (value.get() == 0) {
                sliderEnabled = false;
            }

            Checkbox checkbox = new Checkbox(sliderEnabled);
            checkbox.body.setHeight(slider.body().height());
            checkbox.hoverInfoSet("Disable / enable boosting for " + category.name());

            checkbox.clickActionSet(chkbx -> {
                if (chkbx.selectedIs()) {
                    slider.setEnabled(true);
                } else {
                    slider.setEnabled(false);
                    slider.reset();
                }
            });

            checkboxSection.addDownC(10, checkbox);
            namesSection.addDown(10, name);
            slidersSection.addDown(10, slider);
        });

        addRight(10, checkboxSection);
        addRight(10, namesSection);
        addRight(10, slidersSection);
    }

    public static StandWeightSliderSection build(RaceInteractionsConfig config) {

        Map<RaceStandingCategory, Slider> sliders = new HashMap<>(RaceStandingCategory.values().length);
        Map<RaceStandingCategory, INT.INTE> sliderValues = new HashMap<>(RaceStandingCategory.values().length);
        LinkedHashMap<RaceStandingCategory, Double> orderedMap = toOrderedMap(config.getRaceStandingWeightMap());

        orderedMap.forEach((category, weight) -> {
            INT.INTE value = new SimpleINTE(fromWeightToSlider(weight), toSliderRange(MIN_WEIGHT), toSliderRange(MAX_WEIGHT));
            Slider weightSlider = new Slider(value, toSliderWidth(MIN_WEIGHT, MAX_WEIGHT), true, true);

            sliders.put(category, weightSlider);
            sliderValues.put(category, value);
        });

        return new StandWeightSliderSection(sliders, sliderValues);
    }

    public Slider getSlider(RaceStandingCategory category) {
        return sliders.get(category);
    }

    public INT.INTE getSliderValue(RaceStandingCategory category) {
        return sliderValues.get(category);
    }
}
