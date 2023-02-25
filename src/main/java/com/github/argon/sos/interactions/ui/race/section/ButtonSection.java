package com.github.argon.sos.interactions.ui.race.section;

import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;

@Getter
public class ButtonSection extends GuiSection {

    private final GButt.ButtPanel applyButton;
    private final GButt.ButtPanel resetVanillaButton;
    private final GButt.ButtPanel resetModButton;

    public ButtonSection() {
        this.applyButton = new GButt.ButtPanel("Apply");
        applyButton.hoverInfoSet("Manipulates race likings according to settings");

        this.resetVanillaButton = new GButt.ButtPanel("Reset to Vanilla");
        resetVanillaButton.hoverInfoSet("Restores likings back to original game settings.");

        this.resetModButton = new GButt.ButtPanel("Reset to Mod");
        resetModButton.hoverInfoSet("Restores likings to the default mod settings");


        addRight(0, resetVanillaButton);
        addRight(5, resetModButton);
        addRight(50, applyButton);

    }
}
