package com.github.argon.sos.interactions.ui.race.section.standing;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.ui.Checkbox;
import com.github.argon.sos.interactions.ui.CheckboxTitle;
import com.github.argon.sos.interactions.ui.HorizontalLine;
import com.github.argon.sos.interactions.ui.Slider;
import com.github.argon.sos.interactions.game.SimpleINTE;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.INT;
import util.gui.misc.GButt;
import util.gui.slider.GSliderInt;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.argon.sos.interactions.Mapper.*;
import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.*;
import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.DEFAULT_RACE_LOOK_MAX_RANGE;

@Getter
public class StandSliderSection extends GuiSection {
    private final Map<RaceStandingCategory, Slider> sliders;
    private final Map<RaceStandingCategory, INT.INTE> sliderValues;

    private final GSliderInt raceLookRangeSlider;
    private final INT.INTE raceLookRangeValue;
    private final CheckboxTitle raceBoostSelfCheck;

    private StandSliderSection(
        Map<RaceStandingCategory,
        Slider> sliders,
        Map<RaceStandingCategory,
        INT.INTE> sliderValues,
        int raceLookRange,
        boolean raceBoostSelf
    ) {
        this.sliders = sliders;
        this.sliderValues = sliderValues;

        GuiSection slidersSection = new GuiSection();
        GuiSection namesSection = new GuiSection();
        GuiSection checkboxSection = new GuiSection();

        // weight sliders for each RaceStandingCategory
        sliders.forEach((category, slider) -> {
            GButt.Glow name = new GButt.Glow(category.name());
            name.hoverInfoSet("How much " + category.name() + " shall be boosted");
            name.clickSoundSet(null);
            name.clickActionSet(null);
            name.body.setHeight(slider.body().height());

            INT.INTE value = sliderValues.get(category);
            boolean sliderEnabled = value.get() != 0;

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

        // Boost self checkbox
        this.raceBoostSelfCheck = new CheckboxTitle("Boost Self");
        raceBoostSelfCheck.hoverInfoSet("Whether races should get boosted by their own race");
        raceBoostSelfCheck.selectedSet(raceBoostSelf);

        // Race look range slider
        this.raceLookRangeValue = new SimpleINTE(raceLookRange,0 , DEFAULT_RACE_LOOK_MAX_RANGE);
        this.raceLookRangeSlider = new GSliderInt(raceLookRangeValue, 200, true);

        // Race look range slider label
        GuiSection rangeSliderContainer = new GuiSection();
        GButt.Glow raceLookRangeSliderName = new GButt.Glow("Look Range");
        raceLookRangeSliderName.hoverInfoSet("How far citizens will look for other races nearby in tiles");
        raceLookRangeSliderName.clickSoundSet(null);
        raceLookRangeSliderName.clickActionSet(null);
        raceLookRangeSliderName.body.setHeight(raceLookRangeSlider.body().height());

        rangeSliderContainer.addRightC(0, raceBoostSelfCheck);
        rangeSliderContainer.addRightC(88, raceLookRangeSliderName);
        rangeSliderContainer.addRightC(5, raceLookRangeSlider);

        GuiSection weightSliderContainer = new GuiSection();
        weightSliderContainer.addRight(10, checkboxSection);
        weightSliderContainer.addRight(10, namesSection);
        weightSliderContainer.addRight(10, slidersSection);

        addDownC(0, rangeSliderContainer);
        addDownC(10, new HorizontalLine(rangeSliderContainer.body().width(), 10, 1, true));
        addDownC(10, weightSliderContainer);
    }

    public static StandSliderSection build(RaceInteractionsConfig config) {

        Map<RaceStandingCategory, Slider> sliders = new HashMap<>(RaceStandingCategory.values().length);
        Map<RaceStandingCategory, INT.INTE> sliderValues = new HashMap<>(RaceStandingCategory.values().length);
        LinkedHashMap<RaceStandingCategory, Double> orderedMap = toOrderedMap(config.getRaceStandingWeightMap());

        orderedMap.forEach((category, weight) -> {
            INT.INTE value = new SimpleINTE(fromWeightToSlider(weight), toSliderRange(DEFAULT_MIN_WEIGHT), toSliderRange(DEFAULT_MAX_WEIGHT));
            Slider weightSlider = new Slider(value, toSliderWidth(DEFAULT_MIN_WEIGHT, DEFAULT_MAX_WEIGHT), true, true);

            sliders.put(category, weightSlider);
            sliderValues.put(category, value);
        });

        return new StandSliderSection(sliders, sliderValues, config.getRaceLookRange(), config.isRaceBoostSelf());
    }

    public Slider getSlider(RaceStandingCategory category) {
        return sliders.get(category);
    }

    public INT.INTE getSliderValue(RaceStandingCategory category) {
        return sliderValues.get(category);
    }
}
