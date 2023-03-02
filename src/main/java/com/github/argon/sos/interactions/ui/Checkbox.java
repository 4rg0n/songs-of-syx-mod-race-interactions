package com.github.argon.sos.interactions.ui;

import com.github.argon.sos.interactions.game.ACTION;
import snake2d.SPRITE_RENDERER;
import util.gui.misc.GButt;

/**
 * Adds some basic toggle functionality to {@link Checkbox}
 */
public class Checkbox extends GButt.Checkbox {

    @SuppressWarnings("unchecked")
    private ACTION.ACTION_O<com.github.argon.sos.interactions.ui.Checkbox> clickObjectAction = ACTION.NOPO;
    @SuppressWarnings("unchecked")
    private ACTION.ACTION_O<com.github.argon.sos.interactions.ui.Checkbox> renderObjectAction = ACTION.NOPO;

    public Checkbox() {
    }

    public Checkbox(boolean selected) {
        selectedSet(selected);
    }

    @Override
    protected void clickA() {
        super.clickA();
        selectedToggle();
        clickObjectAction.exe(this);
    }

    @Override
    protected void renAction() {
        super.renAction();
        renderObjectAction.exe(this);
    }

    @Override
    protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
        selectedSet(isSelected);
        super.render(r, ds, isActive, isSelected, isHovered);
    }

    public void clickActionSet(ACTION.ACTION_O<com.github.argon.sos.interactions.ui.Checkbox> action) {
        clickObjectAction = action;
    }

    public void renderActionSet(ACTION.ACTION_O<com.github.argon.sos.interactions.ui.Checkbox> action) {
        renderObjectAction = action;
    }
}
