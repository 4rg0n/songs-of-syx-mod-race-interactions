package com.github.argon.sos.interactions.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.colors.GCOLOR;

public class HorizontalLine extends RENDEROBJ.RenderImp {

    private final int thickness;

    public HorizontalLine(int width, int height, int thickness) {
        super(width, height);
        this.thickness = thickness;
    }

    @Override
    public void render(SPRITE_RENDERER spriteRenderer, float v) {
        GCOLOR.UI().border().render(spriteRenderer, body().x1(), body().x2(), body().cY() + thickness, body().cY() + 1 - thickness);
    }
}
