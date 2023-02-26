package com.github.argon.sos.interactions.ui;

import com.github.argon.sos.interactions.ui.race.RaceInteractionsConfigPanel;
import com.github.argon.sos.interactions.util.UIUtil;
import init.sprite.SPRITES;
import snake2d.util.gui.clickable.CLICKABLE;
import util.gui.misc.GButt;
import view.interrupter.IDebugPanel;

import static com.github.argon.sos.interactions.RaceInteractionsModScript.MOD_INFO;

/**
 * Adds mod ui elements into the game
 */
public class GameConfig {
    private final RaceInteractionsConfigPanel panel;

    public GameConfig(RaceInteractionsConfigPanel panel) {
        this.panel = panel;
    }

    public void init() {
        IDebugPanel.add(MOD_INFO.name + ":config", () -> {
            UIUtil.showPanel(panel, false);
        });

        UIUtil.getUIBuildPanelSection().ifPresent(
            guiSection -> {
                CLICKABLE bppButton = new GButt.ButtPanel(SPRITES.icons().m.citizen) {
                    @Override
                    protected void clickA() {
                        UIUtil.showPanel(panel, false);
                    }
                }.hoverInfoSet(MOD_INFO.name);
                guiSection.addRightC(10, bppButton);
            });
    }
}
