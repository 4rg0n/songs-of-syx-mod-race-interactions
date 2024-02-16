package com.github.argon.sos.interactions.ui.race.section.preference;

import com.github.argon.sos.interactions.race.RaceInfo;
import com.github.argon.sos.interactions.ui.element.Checkbox;
import com.github.argon.sos.interactions.ui.element.HorizontalLine;
import com.github.argon.sos.interactions.ui.element.Spacer;
import com.github.argon.sos.interactions.ui.element.table.TableRow;
import com.github.argon.sos.interactions.ui.element.table.TableStore;
import init.race.RACES;
import init.race.Race;
import init.sprite.SPRITES;
import init.sprite.UI.Icon;
import lombok.Getter;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a single row in the {@link RaceTableSection}.
 * Contains information about the likings of a race to all other races.
 */
public class RaceTableRow extends TableRow<RaceInfo> {

    @Getter
    private final Map<String, Checkbox> raceBoostCheckboxes = new HashMap<>();

    public Optional<Checkbox> getRaceBoostCheckbox(String raceName) {
        return Optional.ofNullable(raceBoostCheckboxes.get(raceName));
    }

    public RaceTableRow(GETTER<Integer> ier, TableStore<RaceInfo> store) {
        super(ier, store);

        GuiSection container = new GuiSection();

        // race icon
        container.addRightC(0, new Spacer(8, 0));
        container.addRightC(0 ,new RENDEROBJ.Sprite(Icon.M) {
            @Override
            public void render(SPRITE_RENDERER r, float ds) {
                getEntry().ifPresent(raceInfo -> {
                    hoverInfoSet(raceInfo.getRace().info.names);
                    setSprite(raceInfo.getRace().appearance().icon);
                });

                super.render(r, ds);
            }
        });

        // arrow icon
        container.addRightC(8, SPRITES.icons().m.arrow_right);
        container.addRightC(8, Spacer.NOTHING);

        // row with other race icons and likings
        LinkedList<RENDEROBJ> columns = new LinkedList<>();
        for (int ri = 0; ri < RACES.all().size(); ri ++) {
            Race otherRace = RACES.all().get(ri);
            GuiSection column = new GuiSection();
            Checkbox raceBoostCheckbox = new Checkbox() {
                @Override
                public void hoverInfoGet(GUI_BOX text) {
                    getEntry().ifPresent(raceInfo -> {
                        GBox b = (GBox) text;
                        b.title(otherRace.info.names);
                        b.text("Boost likings when nearby " + raceInfo.getRace().info.names);
                    });
                }
            };

            raceBoostCheckboxes.put(otherRace.key, raceBoostCheckbox);
            ArrowsGauge arrowsGauge = new ArrowsGauge(otherRace.appearance().icon, 5) {
                @Override
                double getValue() {
                    return getEntry().map(raceInfo ->
                            Math.abs(raceInfo.getRace().pref().race(otherRace)))
                        .orElse(0d);
                }

                @Override
                SPRITE get(double value) {
                    return getEntry()
                        .filter(raceInfo -> (!(raceInfo.getRace().pref().race(otherRace) < 0)))
                        .map(raceInfo -> SPRITES.icons().s.arrowUp)
                        .orElse(SPRITES.icons().s.arrowDown);
                }

                @Override
                COLOR color(double value) {
                    return getEntry()
                        .filter(raceInfo -> !(raceInfo.getRace().pref()
                        .race(otherRace) < 0))
                        .map(raceInfo -> GCOLOR.UI().GOOD.normal)
                        .orElse(GCOLOR.UI().BAD.normal);
                }

                @Override
                public void hoverInfoGet(GUI_BOX text) {
                    getEntry().ifPresent(raceInfo -> {
                        GBox b = (GBox) text;
                        b.title(otherRace.info.names);
                        b.text("Liking to " + raceInfo.getRace().info.names);
                        b.NL(8);
                        b.add(GFORMAT.f1(b.text(), raceInfo.getRace().pref().race(otherRace)));
                    });
                }
            };

            column.addRightC(0, raceBoostCheckbox);
            column.addRightC(1, arrowsGauge);

            // liking of a single other race
            columns.add(column);
        }

        // FIXME: 24.02.2023 when one has too many races the width will be too wide at some point
        //        horizontal scrollbar?
        columns.forEach(column -> {
            container.addRightC(1, column);
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

        private final int maxArrows = 5;

        private final int arrowWidth = 9;

        private final int raceIconToArrowsMargin = 5;

        private final SPRITE raceIcon;

        public ArrowsGauge(SPRITE raceIcon, int padding) {
            this.raceIcon = raceIcon;
            int arrowsWidth = maxArrows * arrowWidth;

            body().setDim( arrowsWidth + raceIcon.width() + padding + raceIconToArrowsMargin + 4, raceIcon.height());
        }

        @Override
        protected void render(SPRITE_RENDERER r, float ds, boolean isHovered) {
            double gaugeValue = getValue();
            raceIcon.render(r, body().x1(), body().y1());

            int arrowsToDisplay = (int) Math.ceil(maxArrows * gaugeValue);
            if (gaugeValue > 1) {
                arrowsToDisplay = maxArrows;
                arrowsToDisplay += CLAMP.i((int) (2* gaugeValue/3.0), 0, 1);
            }

            int arrowsStartX = body().x1() + raceIcon.width() + raceIconToArrowsMargin;

            color(gaugeValue).bind();
            for (int i = 0; i < arrowsToDisplay; i++) {
                get(gaugeValue).renderCY(r, arrowsStartX, body().cY());
                arrowsStartX += arrowWidth;
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
