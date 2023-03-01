package com.github.argon.sos.interactions.ui.race.section.preference;

import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;

/**
 * Contains the buttons for applying and resetting the config
 */
@Getter
public class ButtonSection extends GuiSection {
    private final GButt.ButtPanel undoButton;
    private final GButt.ButtPanel applyButton;
    private final GButt.ButtPanel resetVanillaButton;
    private final GButt.ButtPanel resetModButton;
    private final GButt.ButtPanel saveProfileButton;
    private final GButt.ButtPanel loadProfileButton;


    public ButtonSection() {
        this.undoButton = new GButt.ButtPanel("Undo");
        undoButton.hoverInfoSet("Reset current made settings");
        undoButton.bg(COLOR.WHITE25);

        this.applyButton = new GButt.ButtPanel("Apply");
        applyButton.hoverInfoSet("Manipulates race likings according to settings.");
        applyButton.bg(COLOR.WHITE15);

        this.resetVanillaButton = new GButt.ButtPanel("Reset to Vanilla");
        resetVanillaButton.hoverInfoSet("Restores likings back to original game settings.");

        this.resetModButton = new GButt.ButtPanel("Reset Mod");
        resetModButton.hoverInfoSet("Restores likings to the default mod settings.");

        this.saveProfileButton = new GButt.ButtPanel("Save to profile");
        saveProfileButton.hoverInfoSet("Saves settings to user profile. So it can be used in another save.");

        this.loadProfileButton = new GButt.ButtPanel("Load from profile");
        loadProfileButton.hoverInfoSet("Loads settings from user profile.");

        setButtonsWidth(loadProfileButton.body.width() + 8);

        addDownC(0, resetVanillaButton);
        addDownC(5, resetModButton);
        addDownC(10, loadProfileButton);
        addDownC(5, saveProfileButton);
        addDownC(10, undoButton);
        addDownC(5, applyButton);
    }

    public void setButtonsWidth(int width) {
        resetVanillaButton.body.setWidth(width);
        resetModButton.body.setWidth(width);
        loadProfileButton.body.setWidth(width);
        saveProfileButton.body.setWidth(width);
        applyButton.body.setWidth(width);
        undoButton.body.setWidth(width);
    }

    public void markUnApplied() {
        applyButton.bg(COLOR.WHITE15WHITE50);
    }

    public void markApplied() {
        applyButton.bg(COLOR.WHITE15);
    }
}
