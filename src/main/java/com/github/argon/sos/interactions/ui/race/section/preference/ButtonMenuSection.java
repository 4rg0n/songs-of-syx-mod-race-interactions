package com.github.argon.sos.interactions.ui.race.section.preference;

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

        this.exportButton = new GButt.ButtPanel("Export");
        exportButton.hoverInfoSet("COMING SOON: Export settings for sharing.");

        this.importButton = new GButt.ButtPanel("Import");
        importButton.hoverInfoSet("COMING SOON: Import exported settings.");
        setButtonsWidth(exportButton.body.width() + 8);

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
