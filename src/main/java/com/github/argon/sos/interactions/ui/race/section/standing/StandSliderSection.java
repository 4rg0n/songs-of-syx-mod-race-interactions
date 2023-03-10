package com.github.argon.sos.interactions.ui.race.section.standing;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.game.SimpleInt;
import com.github.argon.sos.interactions.ui.element.Checkbox;
import com.github.argon.sos.interactions.ui.element.HorizontalLine;
import com.github.argon.sos.interactions.ui.element.Slider;
import init.sprite.UI.UI;
import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.INT;
import util.gui.misc.GButt;
import util.gui.misc.GText;
import util.gui.slider.GSliderInt;

import java.util.HashMap;
import java.util.Map;

import static com.github.argon.sos.interactions.Mapper.*;
import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.*;

@Getter
public class StandSliderSection extends GuiSection {
    private final Map<RaceStandingCategory, Slider> sliders;
    private final Map<RaceStandingCategory, INT.INTE> sliderValues;

    private final GSliderInt raceLookRangeSlider;

    private final GButt.ButtPanel enableAllRaceBoostingsButton;
    private final GButt.ButtPanel disableAllRaceBoostingsButton;
    private final INT.INTE raceLookRangeValue;

    private StandSliderSection(
        Map<RaceStandingCategory,
        Slider> sliders,
        Map<RaceStandingCategory,
        INT.INTE> sliderValues,
        int raceLookRange
    ) {
        this.sliders = sliders;
        this.sliderValues = sliderValues;

        GuiSection slidersSection = new GuiSection();
        GuiSection namesSection = new GuiSection();
        GuiSection checkboxSection = new GuiSection();

        // weight sliders for each RaceStandingCategory
        toOrderedMap(sliders).forEach((category, slider) -> {
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

        // Race look range slider
        this.raceLookRangeValue = new SimpleInt(raceLookRange,0 , DEFAULT_RACE_LOOK_MAX_RANGE);
        this.raceLookRangeSlider = new GSliderInt(raceLookRangeValue, 200, true);

        // Race look range slider label
        GuiSection rangeAndButtonSection = new GuiSection();
        GButt.Glow raceLookRangeSliderName = new GButt.Glow("Range:");
        raceLookRangeSliderName.hoverInfoSet("How far citizens will look for other races nearby in tiles");
        raceLookRangeSliderName.clickSoundSet(null);
        raceLookRangeSliderName.clickActionSet(null);
        raceLookRangeSliderName.body.setHeight(raceLookRangeSlider.body().height());

        rangeAndButtonSection.addRightC(0, raceLookRangeSliderName);
        rangeAndButtonSection.addRightC(5, raceLookRangeSlider);

        this.enableAllRaceBoostingsButton = new GButt.ButtPanel(UI.FONT().S.getText("All"));
        enableAllRaceBoostingsButton.hoverInfoSet("Enable boosting likings between all races");
        this.disableAllRaceBoostingsButton = new GButt.ButtPanel(UI.FONT().S.getText("None"));
        disableAllRaceBoostingsButton.hoverInfoSet("Disable boosting likings between all races");
        GText label = new GText(UI.FONT().S, "Boost:");

        // padding for the buttons text
        enableAllRaceBoostingsButton.body.setDim(
            enableAllRaceBoostingsButton.body.width() + 2,
            enableAllRaceBoostingsButton.body.height() + 2);
        disableAllRaceBoostingsButton.body.setDim(
            disableAllRaceBoostingsButton.body.width() + 2,
            disableAllRaceBoostingsButton.body.height() + 2);

        rangeAndButtonSection.addRightC(30, label);
        rangeAndButtonSection.addRightC(5, enableAllRaceBoostingsButton);
        rangeAndButtonSection.addRightC(5, disableAllRaceBoostingsButton);

        GuiSection weightSliderContainer = new GuiSection();
        weightSliderContainer.addRight(10, checkboxSection);
        weightSliderContainer.addRight(10, namesSection);
        weightSliderContainer.addRight(10, slidersSection);

        addDownC(0, rangeAndButtonSection);
        addDownC(10, new HorizontalLine(rangeAndButtonSection.body().width(), 10, 1, true));
        addDownC(10, weightSliderContainer);
    }

    public static StandSliderSection build(RaceInteractionsConfig config) {

        Map<RaceStandingCategory, Slider> sliders = new HashMap<>(RaceStandingCategory.values().length);
        Map<RaceStandingCategory, INT.INTE> sliderValues = new HashMap<>(RaceStandingCategory.values().length);
        Map<RaceStandingCategory, Double> orderedMap = config.getRaceStandingWeights();

        orderedMap.forEach((category, weight) -> {
            INT.INTE value = new SimpleInt(fromWeightToSlider(weight), toSliderRange(DEFAULT_MIN_WEIGHT), toSliderRange(DEFAULT_MAX_WEIGHT));
            Slider weightSlider = new Slider(value, toSliderWidth(DEFAULT_MIN_WEIGHT, DEFAULT_MAX_WEIGHT), true, true);

            sliders.put(category, weightSlider);
            sliderValues.put(category, value);
        });

        return new StandSliderSection(sliders, sliderValues, config.getRaceLookRange());
    }

    public Slider getSlider(RaceStandingCategory category) {
        return sliders.get(category);
    }

    public INT.INTE getSliderValue(RaceStandingCategory category) {
        return sliderValues.get(category);
    }
}
