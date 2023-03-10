package com.github.argon.sos.interactions.ui;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.ui.race.RaceInteractionsConfigPanel;
import com.github.argon.sos.interactions.game.api.GameUiApi;
import init.sprite.SPRITES;
import snake2d.util.gui.clickable.CLICKABLE;
import util.gui.misc.GButt;
import view.interrupter.IDebugPanel;

import static com.github.argon.sos.interactions.RaceInteractionsModScript.MOD_INFO;

/**
 * Adds mod ui elements into the game
 */
public class UIGameConfig {
    private final static Logger log = Loggers.getLogger(UIGameConfig.class);
    private final RaceInteractionsConfigPanel panel;

    private final GameUiApi gameUiApi = GameUiApi.getInstance();

    public UIGameConfig(RaceInteractionsConfigPanel panel) {
        this.panel = panel;
    }

    public void init() {
        log.debug("Injecting UI elements into game");
        IDebugPanel.add(MOD_INFO.name + ":config", () -> {
            gameUiApi.showPanel(panel, false);
        });

        gameUiApi.getUIBuildPanelSection().ifPresent(
            guiSection -> {
                CLICKABLE bppButton = new GButt.ButtPanel(SPRITES.icons().m.citizen) {
                    @Override
                    protected void clickA() {
                        gameUiApi.showPanel(panel, false);
                    }
                }.hoverInfoSet(MOD_INFO.name);
                guiSection.addRightC(10, bppButton);
            });
    }
}
