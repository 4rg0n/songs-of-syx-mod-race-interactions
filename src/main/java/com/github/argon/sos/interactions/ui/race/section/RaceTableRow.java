package com.github.argon.sos.interactions.ui.race.section;

import com.github.argon.sos.interactions.race.RaceInfo;
import com.github.argon.sos.interactions.ui.table.TableRow;
import com.github.argon.sos.interactions.ui.table.TableStore;
import init.race.RACES;
import init.race.Race;
import init.sprite.ICON;
import init.sprite.SPRITES;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.CLAMP;
import snake2d.util.sets.LIST;
import snake2d.util.sets.LISTE;
import snake2d.util.sets.LinkedList;
import snake2d.util.sprite.SPRITE;
import util.colors.GCOLOR;
import util.data.GETTER;
import util.gui.misc.GBox;
import util.info.GFORMAT;

public class RaceTableRow extends TableRow<RaceInfo> {
    public RaceTableRow(GETTER<Integer> ier, TableStore<RaceInfo> store) {
        super(ier, store);

        // race icon
        add(new RENDEROBJ.Sprite(ICON.MEDIUM.SIZE) {
            @Override
            public void render(SPRITE_RENDERER r, float ds) {
                hoverInfoSet(getEntry().getRace().info.names);
                setSprite(getEntry().getRace().appearance().icon);
                super.render(r, ds);
            }
        });

        // arrow icon
        addRightC(3, SPRITES.icons().m.arrow_right);

        // row with race icons and likings
        LinkedList<RENDEROBJ> ee = new LinkedList<>();
        for (int ri = 0; ri < RACES.all().size(); ri ++) {
            Race rr = RACES.all().get(ri);

            ee.add(new RGauge(rr.appearance().icon) {

                @Override
                double getValue() {
                    return Math.abs(getEntry().getRace().pref().other(rr));
                }

                @Override
                SPRITE get(double value) {
                    return getEntry().getRace().pref().other(rr) < 0 ? SPRITES.icons().s.arrowDown : SPRITES.icons().s.arrowUp;
                }

                @Override
                COLOR color(double value) {
                    return getEntry().getRace().pref().other(rr) < 0 ? GCOLOR.UI().BAD.normal : GCOLOR.UI().GOOD.normal;
                }

                @Override
                public void hoverInfoGet(GUI_BOX text) {
                    GBox b = (GBox) text;
                    b.title(rr.info.names);
                    b.text("Liking to " + getEntry().getRace().info.names);
                    b.NL(8);
                    b.add(GFORMAT.f1(b.text(), getEntry().getRace().pref().other(rr)));
                };
            });
        }

        // FIXME: 24.02.2023 when one has too many races the width will be too wide at some point
        //        horizontal scrollbar?
        ee.forEach(renderobj -> {
            addRightC(3, renderobj);
        });

        pad(5);
    }

    private static abstract class RGauge extends HoverableAbs{

        private int max = 5;
        private final SPRITE icon;


        public RGauge(SPRITE icon) {
            this.icon = icon;
            body().setDim(ICON.BIG.SIZE*3+4, icon.height());

        }

        @Override
        protected void render(SPRITE_RENDERER r, float ds, boolean isHovered) {
            double v = getValue();
            icon.render(r, body().x1(), body().y1());

            int am = (int) Math.ceil(max*v);
            if (v > 1) {
                am = max;
                am += CLAMP.i((int) (2*v/3.0), 0, 1);
            }

            int x1 = body().x1() + ICON.BIG.SIZE;


            color(v).bind();
            for (int i = 0; i < am; i++) {
                get(v).renderCY(r, x1, body().cY());
                x1 += 9;
            }
            COLOR.unbind();
        }

        abstract double getValue();
        abstract SPRITE get(double value);
        COLOR color(double value) {
            return GCOLOR.UI().GOOD.normal;
        }

        static void add(LISTE<RENDEROBJ> rows, LIST<RENDEROBJ> all) {

            int ri = 5;
            GuiSection s = null;

            for (RENDEROBJ h : all) {
                ri++;
                if (ri >= 5) {
                    ri = 0;
                    if (s != null)
                        s.pad(2, 8);
                    s = new GuiSection();
                    rows.add(s);
                }
                s.addRightC(16, h);

            }


        }
    }
}
