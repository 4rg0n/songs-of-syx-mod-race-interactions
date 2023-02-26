package com.github.argon.sos.interactions.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.util.sprite.SPRITE;
import util.gui.misc.GButt;

public class Checkbox extends GButt.CheckboxTitle {
    public Checkbox(SPRITE label) {
        super(label);
    }

    public Checkbox(CharSequence text) {
        super(text);
    }

    protected void clickA() {
        selectedToggle();
    }
    @Override
    protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
        selectedSet(isSelected);
        super.render(r, ds, isActive, isSelected, isHovered);
    }
}
