package com.github.argon.sos.interactions.ui.race.section;

import com.github.argon.sos.interactions.ui.element.Button;
import lombok.Getter;
import snake2d.util.gui.GuiSection;

@Getter
public class ButtonMenuSection extends GuiSection {
    private final Button resetModButton;
    private final Button exportButton;
    private final Button importButton;
    private final Button importOtherModsButton;
    public ButtonMenuSection() {
        this.resetModButton = new Button("Reset");
        resetModButton.hoverInfoSet("Restores vanilla likings and default mod settings.");

        this.exportButton = new Button("Export to clipboard");
        exportButton.hoverInfoSet("Export settings for sharing into clipboard.");

        this.importButton = new Button("Import from clipboard");
        importButton.hoverInfoSet("Import exported settings from clipboard.");

        this.importOtherModsButton = new Button("Import from other mods");
        importOtherModsButton.hoverInfoSet("TODO");
        setButtonsWidth(importOtherModsButton.body.width() + 8);

        addDownC(0, exportButton);
        addDownC(0, importButton);
        addDownC(0, importOtherModsButton);
        addDownC(0, resetModButton);
    }

    public void setButtonsWidth(int width) {
        resetModButton.body.setWidth(width);
        exportButton.body.setWidth(width);
        importButton.body.setWidth(width);
        importOtherModsButton.body.setWidth(width);
    }
}
