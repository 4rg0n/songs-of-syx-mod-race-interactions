package com.github.argon.sos.interactions.ui.race.section;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.race.RacePrefCategory;
import com.github.argon.sos.interactions.ui.Slider;
import com.github.argon.sos.interactions.util.SimpleINTE;
import init.sprite.UI.UI;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.INT;
import util.gui.misc.GText;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.argon.sos.interactions.Mapper.*;
import static com.github.argon.sos.interactions.config.ConfigUtil.Default.MAX_WEIGHT;
import static com.github.argon.sos.interactions.config.ConfigUtil.Default.MIN_WEIGHT;

/**
 * Contains the sliders for setting up the weights.
 * Each {@link RacePrefCategory} gets a slider.
 */
@Getter
public class WeightSliderSection extends GuiSection {

    private final Map<RacePrefCategory, Slider> sliders;
    private final Map<RacePrefCategory, INT.INTE> sliderValues;

    private WeightSliderSection(Map<RacePrefCategory, Slider> sliders, Map<RacePrefCategory, INT.INTE> sliderValues) {
        this.sliders = sliders;
        this.sliderValues = sliderValues;

        GuiSection slidersSection = new GuiSection();
        GuiSection namesSection = new GuiSection();

        sliders.forEach((category, slider) -> {
            GText name = new GText(UI.FONT().M, category.name());

            namesSection.addDown(10, name);
            slidersSection.addDown(10, slider);
        });

        addRight(10, namesSection);
        addRight(10, slidersSection);
    }

    public static WeightSliderSection build(RaceInteractionsConfig config) {

        Map<RacePrefCategory, Slider> sliders = new HashMap<>(RacePrefCategory.values().length);
        Map<RacePrefCategory, INT.INTE> sliderValues = new HashMap<>(RacePrefCategory.values().length);
        LinkedHashMap<RacePrefCategory, Double> orderedPrefMap = toOrderedMap(config.getRacePreferenceWeightMap());

        orderedPrefMap.forEach((racePrefCategory, weight) -> {
            INT.INTE value = new SimpleINTE(fromWeightToSlider(weight), toSliderRange(MIN_WEIGHT), toSliderRange(MAX_WEIGHT));
            Slider weightSlider = new Slider(value, toSliderWidth(MIN_WEIGHT, MAX_WEIGHT), true, true);

            sliders.put(racePrefCategory, weightSlider);
            sliderValues.put(racePrefCategory, value);
        });

        return new WeightSliderSection(sliders, sliderValues);
    }

    public Slider getSlider(RacePrefCategory category) {
        return sliders.get(category);
    }

    public INT.INTE getSliderValue(RacePrefCategory category) {
        return sliderValues.get(category);
    }
}
