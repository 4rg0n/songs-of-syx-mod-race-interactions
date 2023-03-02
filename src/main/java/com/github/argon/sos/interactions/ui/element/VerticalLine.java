package com.github.argon.sos.interactions.ui.element;

import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.colors.GCOLOR;

public class VerticalLine extends RENDEROBJ.RenderImp {

    private final int thickness;

    public VerticalLine(int width, int height, int thickness) {
        super(width, height);
        this.thickness = thickness;
    }

    @Override
    public void render(SPRITE_RENDERER spriteRenderer, float v) {
        GCOLOR.UI().border().render(spriteRenderer, body().cX() + thickness, body().cX() + 1 - thickness, body().y1() , body().y2());
    }
}
