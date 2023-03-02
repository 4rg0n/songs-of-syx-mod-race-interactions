package com.github.argon.sos.interactions.ui.race.section.preference;

import com.github.argon.sos.interactions.race.RaceInfo;
import com.github.argon.sos.interactions.ui.element.HorizontalLine;
import com.github.argon.sos.interactions.ui.element.Spacer;
import com.github.argon.sos.interactions.ui.element.table.TableRow;
import com.github.argon.sos.interactions.ui.element.table.TableStore;
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
import snake2d.util.sets.LinkedList;
import snake2d.util.sprite.SPRITE;
import util.colors.GCOLOR;
import util.data.GETTER;
import util.gui.misc.GBox;
import util.info.GFORMAT;

/**
 * Represents a single row in the {@link RaceTableSection}.
 * Contains information about the likings of a race to all other races.
 */
public class RaceTableRow extends TableRow<RaceInfo> {

    public RaceTableRow(GETTER<Integer> ier, TableStore<RaceInfo> store) {
        super(ier, store);

        GuiSection container = new GuiSection();


        // race icon
        container.addRightC(0, new Spacer(8, 0));
        container.addRightC(0 ,new RENDEROBJ.Sprite(ICON.MEDIUM.SIZE) {
            @Override
            public void render(SPRITE_RENDERER r, float ds) {
                hoverInfoSet(getEntry().getRace().info.names);
                setSprite(getEntry().getRace().appearance().icon);
                super.render(r, ds);
            }
        });

        // arrow icon
        container.addRightC(8, SPRITES.icons().m.arrow_right);
        container.addRightC(8, Spacer.NOTHING);

        // row with other race icons and likings
        LinkedList<RENDEROBJ> ee = new LinkedList<>();
        for (int ri = 0; ri < RACES.all().size(); ri ++) {
            Race otherRace = RACES.all().get(ri);

            // liking of a single other race
            ee.add(new ArrowsGauge(otherRace.appearance().icon) {
                @Override
                double getValue() {
                    return Math.abs(getEntry().getRace().pref()
                        .other(otherRace));
                }

                @Override
                SPRITE get(double value) {
                    return getEntry().getRace().pref()
                        .other(otherRace) < 0 ? SPRITES.icons().s.arrowDown : SPRITES.icons().s.arrowUp;
                }

                @Override
                COLOR color(double value) {
                    return getEntry().getRace().pref()
                        .other(otherRace) < 0 ? GCOLOR.UI().BAD.normal : GCOLOR.UI().GOOD.normal;
                }

                @Override
                public void hoverInfoGet(GUI_BOX text) {
                    GBox b = (GBox) text;
                    b.title(otherRace.info.names);
                    b.text("Liking to " + getEntry().getRace().info.names);
                    b.NL(8);
                    b.add(GFORMAT.f1(b.text(), getEntry().getRace().pref().other(otherRace)));
                };
            });
        }

        // FIXME: 24.02.2023 when one has too many races the width will be too wide at some point
        //        horizontal scrollbar?
        ee.forEach(renderobj -> {
            container.addRightC(1, renderobj);
        });

        addDownC(0, new Spacer(container.body().width(), 10));
        addDownC( 0, container);
        addDownC(10, new HorizontalLine(container.body().width(), 3, 1));
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        // color each even row with other background
        if (getIndex().get() != null && getIndex().get() % 2 != 0) {
            background(COLOR.WHITE20);
        }
        super.render(r, ds);
    }

    /**
     * Row of red or green arrows pointing down or up
     */
    private static abstract class ArrowsGauge extends HoverableAbs {

        private int max = 5;
        private final SPRITE icon;

        public ArrowsGauge(SPRITE icon) {
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
    }
}
