package com.github.argon.sos.interactions.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.colors.GCOLOR;

public class HorizontalLine extends RENDEROBJ.RenderImp {

    private final int thickness;
    private final boolean shadow;

    public HorizontalLine(int width, int height, int thickness) {
        this(width, height, thickness, false);
    }

    public HorizontalLine(int width, int height, int thickness, boolean shadow) {
        super(width, height);
        this.thickness = thickness;
        this.shadow = shadow;
    }

    @Override
    public void render(SPRITE_RENDERER spriteRenderer, float v) {
        GCOLOR.UI().border().render(spriteRenderer, body().x1(), body().x2(), body().cY() + thickness, body().cY() + 1 - thickness);

        if (shadow) {
            COLOR.WHITE10.render(spriteRenderer, body().x1(), body().x2(), body().cY() + thickness + thickness, body().cY() + 1 - thickness + thickness);
        }
    }
}
