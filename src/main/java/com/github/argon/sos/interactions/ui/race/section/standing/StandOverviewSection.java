package com.github.argon.sos.interactions.ui.race.section.standing;

import com.github.argon.sos.interactions.game.api.GameRaceApi;
import settlement.stats.standing.STANDINGS;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GHeader;
import util.gui.misc.GMeter;
import util.gui.misc.GStat;
import util.gui.misc.GText;
import util.info.GFORMAT;

public class StandOverviewSection extends GuiSection {

    private final GameRaceApi gameRaceApi = GameRaceApi.getInstance();

    public StandOverviewSection() {
        addDown(0, loyaltySection());
        addDown(10, happinessSection());
    }

    /**
     * Meter, which displays the current average happiness of all the races in the settlement
     */
    private GuiSection happinessSection() {
        GuiSection headerSection = new GuiSection();
        GuiSection meterSection = new GuiSection();

        headerSection.addRight(0, new GHeader(STANDINGS.CITIZEN().happiness.info().name));
        headerSection.addRightC(5, new GStat() {
            @Override
            public void update(GText text) {
                GFORMAT.perc(text, gameRaceApi.getAvgHappiness());
            }
        });
        RENDEROBJ happinessMeter = new RENDEROBJ.RenderImp(150, 24) {
            @Override
            public void render(SPRITE_RENDERER r, float ds) {
                double now = gameRaceApi.getAvgHappiness();
                GMeter.render(r, GMeter.C_REDGREEN, now, body);
            }
        };

        meterSection.addRight(0, happinessMeter);

        GuiSection section = new GuiSection();
        section.addDown(0,headerSection);
        section.addDown(5, meterSection);

        return section;
    }

    /**
     * Meter, which displays the current average loyalty of all the races in the settlement
     */
    private GuiSection loyaltySection() {
        GuiSection headerSection = new GuiSection();
        GuiSection meterSection = new GuiSection();

        headerSection.addRight(0, new GHeader(STANDINGS.CITIZEN().loyalty.info().name));
        headerSection.addRightC(5, new GStat() {
            @Override
            public void update(GText text) {
                GFORMAT.perc(text, gameRaceApi.getAvgLoyalty());
            }
        });
        RENDEROBJ loyaltyMeter = new RENDEROBJ.RenderImp(150, 24) {
            @Override
            public void render(SPRITE_RENDERER r, float ds) {
                double now = gameRaceApi.getAvgLoyalty();
                GMeter.render(r, GMeter.C_REDGREEN, now, body);
            }
        };

        meterSection.addRight(0, loyaltyMeter);

        GuiSection section = new GuiSection();
        section.addDown(0, headerSection);
        section.addDown(5, meterSection);

        return section;
    }
}
