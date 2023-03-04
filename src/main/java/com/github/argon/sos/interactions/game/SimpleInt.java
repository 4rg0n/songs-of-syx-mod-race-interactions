package com.github.argon.sos.interactions.game;

import com.github.argon.sos.interactions.ui.element.Slider;
import util.data.INT;

/**
 * Used for {@link Slider} and {@link util.gui.slider.GSliderInt}
 * as value storage.
 */
public class SimpleInt extends INT.IntImp {
    public SimpleInt(int value, int min, int max) {
        super(min, max);
        set(value);
    }
}
