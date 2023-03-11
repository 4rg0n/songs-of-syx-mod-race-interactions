package com.github.argon.sos.interactions.ui.race.section;

import com.github.argon.sos.interactions.config.ConfigStore;
import com.github.argon.sos.interactions.config.OtherModInfo;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.ui.element.Button;
import com.github.argon.sos.interactions.ui.element.HorizontalLine;
import com.github.argon.sos.interactions.ui.race.RaceInteractionsConfigPanel;
import init.sprite.UI.UI;
import snake2d.util.gui.GUI_BOX;
import snake2d.util.gui.GuiSection;
import snake2d.util.sprite.SPRITE;
import snake2d.util.sprite.text.Text;
import util.gui.misc.GBox;
import util.gui.misc.GHeader;

import java.util.List;
import java.util.Optional;

/**
 * Selection of mods containing {@link RaceInteractionsConfig} as json.
 */
public class ImportOtherModSection extends GuiSection {

    private final static int WIDTH = 300;
    private final static int BUTTON_TEXT_PADDING = 10;

    public ImportOtherModSection(
        List<OtherModInfo> otherMods,
        RaceInteractionsConfigPanel configPanel,
        ConfigStore configStore
    ) {
        GHeader header = new GHeader("Import from other mods");

        addDownC(0, header);
        addDownC(0, new HorizontalLine(WIDTH, 21, 1, true));

        if (otherMods.isEmpty()) {
            Text text = new Text(UI.FONT().S, "No settings from other active mods found :(");
            addDownC(10, text);
        } else {
            // buttons for loading and applying other mods configs
            for (OtherModInfo otherModInfo : otherMods) {
                Text text = new Text(UI.FONT().S, otherModInfo.getName())
                    .setMaxWidth(WIDTH - BUTTON_TEXT_PADDING)
                    .setMultipleLines(false);

                Button button = new Button((SPRITE) text) {
                    @Override
                    public void hoverInfoGet(GUI_BOX text) {
                        GBox infoBox = (GBox) text;
                        infoBox.title(otherModInfo.getName());
                        infoBox.text(otherModInfo.getDescription());
                        infoBox.NL(10);
                        infoBox.add(new Text(UI.FONT().S, otherModInfo.getConfigPath().toString()));
                    }

                    /**
                     * Load and apply other mods config
                     */
                    @Override
                    protected void clickA() {
                        Optional<RaceInteractionsConfig> optionalConfig = configStore
                            .loadJson(otherModInfo.getConfigPath());

                        if (!optionalConfig.isPresent()) {
                            markSuccess(false);
                        } else {
                            boolean success = configPanel.applyConfig(optionalConfig.get());
                            markSuccess(success);
                        }
                    }
                };

                button.body.setWidth(WIDTH);
                button.body.setHeight(text.height() + BUTTON_TEXT_PADDING);

                addDownC(0, button);
            }
        }
    }
}
