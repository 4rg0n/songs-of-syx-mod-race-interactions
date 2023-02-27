package com.github.argon.sos.interactions.ui;

import init.D;
import init.sprite.ICON;
import init.sprite.SPRITES;
import init.sprite.UI.UI;
import snake2d.MButt;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.misc.CLAMP;
import snake2d.util.misc.STRING_RECIEVER;
import snake2d.util.sprite.text.Str;
import util.colors.GCOLOR;
import util.data.INT;
import util.gui.misc.GBox;
import util.gui.misc.GButt;
import util.gui.misc.GStat;
import util.gui.misc.GText;
import util.gui.slider.GSliderInt;
import util.info.GFORMAT;
import view.main.VIEW;


/**
 * Uses mostly code from {@link GSliderInt} and adds handling for negative values.
 */
public class Slider extends GuiSection {
    private final INT.INTE in;
    private static final int midWidth = 8;
    private static final CharSequence setAmount = "Set amount";
    private static final CharSequence setAmountD = "Set amount {0}-{1}";

    static {
        D.ts(GSliderInt.class);
    }

    public Slider(INT.INTE in, int width, boolean input, boolean showValue){
        this(in, width, 24, input, showValue);
    }

    public Slider(INT.INTE in, int width, int height, boolean input, boolean showValue){
        this.in = in;

        if (input) {
            width -= (ICON.SMALL.SIZE+2)*3;
        }

        width -= 4;

        if (width < 0)
            width = 0;

        if (input) {
            addRightC(0, new GButt.ButtPanel(SPRITES.icons().s.minifier) {
                private double clickSpeed;

                @Override
                protected void clickA() {
                    in.inc(-1);
                }

                @Override
                protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected,
                                      boolean isHovered) {
                    if (isHovered &&  MButt.LEFT.isDown()) {
                        clickSpeed += ds;
                        if (clickSpeed > 10)
                            clickSpeed = 10;
                        in.inc(-(int)clickSpeed);

                    }else {
                        clickSpeed = 0;
                    }
                    super.render(r, ds, isActive, isSelected, isHovered);
                }
            });

        }

        addRightC(4, new Slider.Mid(width, height));

        if (input) {
            addRightC(4, new GButt.ButtPanel(SPRITES.icons().s.magnifier) {
                private double clickSpeed;

                @Override
                protected void clickA() {
                    in.inc(1);
                }

                @Override
                protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected,
                                      boolean isHovered) {
                    if (isHovered &&  MButt.LEFT.isDown()) {
                        clickSpeed += ds*2;
                        if (clickSpeed > 10)
                            clickSpeed = 10;
                        in.inc((int)clickSpeed);

                    }else {
                        clickSpeed = 0;
                    }
                    super.render(r, ds, isActive, isSelected, isHovered);
                }
            });

            addRightC(0, new GButt.ButtPanel(SPRITES.icons().s.pluses) {

                @Override
                protected void clickA() {
                    Str.TMP.clear().add(setAmountD).insert(0, in.min()).insert(1, in.max());
                    VIEW.inters().input.requestInput(rec, Str.TMP);
                }


            }.hoverInfoSet(setAmount));

        }

        if (showValue) {
            GStat value = new GStat() {
                @Override
                public void update(GText text) {
                    GFORMAT.iBig(text, in.get());
                }
            };

            GuiSection section = new GuiSection();
            int max = Math.max(Math.abs(in.min()), Math.abs(in.max()));
            String maxString = Integer.toString(max);
            if (in.min() < 0) {
                maxString = "-" + maxString;
            }

            int valueWidth = UI.FONT().S.width(maxString, 0, maxString.length() - 1, 1.4);

            section.body().setWidth(valueWidth);
            section.body().setHeight(body().height());

            section.addCentredX(value, 0);
            addRight(5, section);
        }
    }

    private final STRING_RECIEVER rec = new STRING_RECIEVER() {

        @Override
        public void acceptString(CharSequence string) {
            String s = ""+string;
            try {
                int i = Integer.parseInt(s);
                i = CLAMP.i(i, in.min(), in.max());
                in.set(i);
            }catch(Exception e) {

            }

        }
    };

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        activeSet(in.max() > 0);
        super.render(r, ds);
    }

    @Override
    public void hoverInfoGet(GUI_BOX text) {
        GBox b = (GBox) text;
        b.add(GFORMAT.i(b.text(), in.get()));
    }

    protected void renderPositiveMidColor(SPRITE_RENDERER r, int x1, int width, int y1, int y2) {
        COLOR.WHITE50.render(r, x1, x1+width, y1, y2);
    }

    protected void renderNegativeMidColor(SPRITE_RENDERER r, int x1, int width, int y1, int y2) {
        COLOR.WHITE35.render(r, x1, x1+width, y1, y2);
    }

    private class Mid extends CLICKABLE.ClickableAbs {

        private boolean clicked = false;

        Mid(int width, int height){
            super(width, height-4);
        }

        @Override
        protected void clickA() {
            clicked = true;
            setFromClickPos();
        }

        private void setFromClickPos() {
            double clickPos = getClickPos();
            double value;

            if (in.min() < 0) {
                value = CLAMP.d(clickPos, -1, 1);
            } else {
                value = CLAMP.d(clickPos, 0, 1);
            }

            in.setD(value);
        }

        private double getClickPos() {
            int barStartX = body().x1();
            int clickX = VIEW.mouse().x();
            int clickPos = clickX - barStartX;

            if (in.min() < 0) {
                int barLength = body().width() / 2;
                int barCenterX = barStartX + barLength;
                int barRange = clickX - barCenterX;

                return barRange / (double) (barLength);
            } else {
                return clickPos / (double) body().width();
            }
        }

        @Override
        protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected, boolean isHovered) {
            clicked &= MButt.LEFT.isDown();
            int barFullWidth = body().width();
            int barStartX = body().x1();
            int barCenterX = barStartX + barFullWidth / 2;

            if (clicked) {
                setFromClickPos();
            }

            GCOLOR.UI().border().render(r, body, 2);
            GCOLOR.UI().bg(isActive, isSelected, isHovered).render(r, body, 1);

            int slideCursorPos;

            if (in.min() > 0) {
                slideCursorPos = (int) (body.x1()+ barFullWidth * in.getD());
                renderWithPositiveValues(r);
            } else {
                renderWithNegativeValues(r);
                int barWidth = body().width() / 2;
                int barColoredLength = (int) (in.getD() * barWidth);

                if (in.get() < 0) {
                    slideCursorPos = body().x1() + barWidth - barColoredLength;
                } else {
                    slideCursorPos = body().x1() + barWidth + barColoredLength;
                }

                // splitting bar in the middle
                COLOR.WHITE120.render(r, barCenterX-1, barCenterX+1, body().y1()-1, body().y2()+1);
            }

            // draggable cursor
            if (!isHovered) {
                COLOR.WHITE85.render(r, slideCursorPos-1, slideCursorPos+1, body().y1()+1, body().y2()-1);
            } else {
                GCOLOR.UI().border().render(r, slideCursorPos-midWidth/2, slideCursorPos+midWidth/2, body().y1(), body().y2());
                COLOR c = isHovered || clicked ? GCOLOR.T().H1 : GCOLOR.T().H2;
                c.render(r, slideCursorPos-midWidth/2+1, slideCursorPos+midWidth/2-1, body().y1()+1, body().y2()-1);
                COLOR.BLACK.render(r, slideCursorPos-1, slideCursorPos+2, body().y1()+2, body().y2()-2);
            }
        }

        private void renderWithPositiveValues(SPRITE_RENDERER r) {
            int barFullWidth = body().width();
            int barColoredLength = (int) (in.getD() * barFullWidth);
            int barColoredEndPos = body().x1() + barColoredLength;
            int barColoredStartPos = body().x1();
            int barColoredWidth = barColoredEndPos - barColoredStartPos;

            // renders the white bar
            renderPositiveMidColor(r, barColoredStartPos, barColoredWidth, body().y1(), body().y2());
        }

        private void renderWithNegativeValues(SPRITE_RENDERER r) {
            int barWidth = body().width() / 2;
            int barColoredLength = (int) (in.getD() * barWidth);

            if (in.get() < 0) {
                barColoredLength = - barColoredLength;
            }

            int barColoredEndPos = body().x1() + barWidth;
            int barColoredStartPos = body().x1() + barWidth + barColoredLength;
            int barColoredWidth = barColoredEndPos - barColoredStartPos;

            if (in.get() < 0) {
                // renders negative bar part
                renderNegativeMidColor(r, barColoredStartPos, barColoredWidth, body().y1(), body().y2());
            } else {
                // renders positive bar part
                renderPositiveMidColor(r, barColoredStartPos, barColoredWidth, body().y1(), body().y2());
            }
        }
        @Override
        public boolean hover(COORDINATE mCoo) {
            if (super.hover(mCoo)) {
                double d = MButt.clearWheelSpin();
                if (d < 0)
                    in.inc(-1);
                else if (d > 0)
                    in.inc(1);
                return true;
            }
            return false;
        }


    }
}
