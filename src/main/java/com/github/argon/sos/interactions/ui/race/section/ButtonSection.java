package com.github.argon.sos.interactions.ui.race.section;

import com.github.argon.sos.interactions.ui.element.Button;
import lombok.Getter;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;

/**
 * Contains the buttons for applying and resetting the config
 */
@Getter
public class ButtonSection extends GuiSection {
    private final Button menuButton;
    private final Button undoButton;
    private final Button applyButton;
    private final Button saveProfileButton;
    private final Button loadProfileButton;

    public ButtonSection() {
        this.menuButton = new Button("More");
        menuButton.hoverInfoSet("Additional functions");

        this.undoButton = new Button("Undo", COLOR.WHITE25);
        undoButton.hoverInfoSet("Reset current made settings");

        this.applyButton = new Button("Apply", COLOR.WHITE15);
        applyButton.hoverInfoSet("Manipulates race likings according to settings.");


        this.saveProfileButton = new Button("Save to profile");
        saveProfileButton.hoverInfoSet("Saves settings to user profile. So it can be used in another save.");

        this.loadProfileButton = new Button("Load from profile");
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
        applyButton.markApplied(false);
    }

    public void markApplied() {
        applyButton.markApplied(true);
    }
}
