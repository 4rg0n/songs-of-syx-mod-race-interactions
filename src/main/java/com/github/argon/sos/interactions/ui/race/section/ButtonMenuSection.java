package com.github.argon.sos.interactions.ui.race.section;

import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;

@Getter
public class ButtonMenuSection extends GuiSection {
    private final GButt.ButtPanel resetModButton;
    private final GButt.ButtPanel exportButton;
    private final GButt.ButtPanel importButton;
    public ButtonMenuSection() {
        this.resetModButton = new GButt.ButtPanel("Reset");
        resetModButton.hoverInfoSet("Restores vanilla likings and default mod settings.");

        this.exportButton = new GButt.ButtPanel("Export to clipboard");
        exportButton.hoverInfoSet("Export settings for sharing into clipboard.");

        this.importButton = new GButt.ButtPanel("Import from clipboard");
        importButton.hoverInfoSet("Import exported settings from clipboard.");
        setButtonsWidth(importButton.body.width() + 8);

        addDownC(0, exportButton);
        addDownC(0, importButton);
        addDownC(0, resetModButton);
    }

    public void setButtonsWidth(int width) {
        resetModButton.body.setWidth(width);
        exportButton.body.setWidth(width);
        importButton.body.setWidth(width);
    }
}
