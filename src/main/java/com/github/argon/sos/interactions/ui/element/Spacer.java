package com.github.argon.sos.interactions.ui.element;

import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.renderable.RENDEROBJ;

public class Spacer extends RENDEROBJ.RenderImp{

    public final static Spacer NOTHING = new Spacer();

    public Spacer(int width, int height) {
        super(width, height);
    }

    public Spacer() {
    }

    @Override
    public void render(SPRITE_RENDERER spriteRenderer, float v) {

    }
}
