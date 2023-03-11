package com.github.argon.sos.interactions.ui.race.section;

import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GButt;

/**
 * Contains the buttons for applying and resetting the config
 */
@Getter
public class ButtonSection extends GuiSection {
    private final GButt.ButtPanel menuButton;
    private final GButt.ButtPanel undoButton;
    private final GButt.ButtPanel applyButton;
    private final GButt.ButtPanel saveProfileButton;
    private final GButt.ButtPanel loadProfileButton;

    public ButtonSection() {
        this.menuButton = new GButt.ButtPanel("More");
        menuButton.hoverInfoSet("Additional functions");

        this.undoButton = new GButt.ButtPanel("Undo");
        undoButton.hoverInfoSet("Reset current made settings");
        undoButton.bg(COLOR.WHITE25);

        this.applyButton = new GButt.ButtPanel("Apply");
        applyButton.hoverInfoSet("Manipulates race likings according to settings.");
        applyButton.bg(COLOR.WHITE15);


        this.saveProfileButton = new GButt.ButtPanel("Save to profile");
        saveProfileButton.hoverInfoSet("Saves settings to user profile. So it can be used in another save.");

        this.loadProfileButton = new GButt.ButtPanel("Load from profile");
        loadProfileButton.hoverInfoSet("Loads settings from user profile.");

        setButtonsWidth(loadProfileButton.body.width() + 8);

        addDownC(0, menuButton);
        addDownC(20, loadProfileButton);
        addDownC(5, saveProfileButton);
        addDownC(20, undoButton);
        addDownC(5, applyButton);
    }

    public void setButtonsWidth(int width) {
        loadProfileButton.body.setWidth(width);
        saveProfileButton.body.setWidth(width);
        applyButton.body.setWidth(width);
        undoButton.body.setWidth(width);
        menuButton.body.setWidth(width);
    }

    public void markUnApplied() {
        applyButton.bg(COLOR.WHITE15WHITE50);
    }

    public void markApplied() {
        applyButton.bg(COLOR.WHITE15);
    }
}
