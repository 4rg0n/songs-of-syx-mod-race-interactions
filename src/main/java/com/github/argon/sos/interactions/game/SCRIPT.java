package com.github.argon.sos.interactions.game;

public interface SCRIPT extends script.SCRIPT {
    /**
     * Executed when the game is running
     */
    void initGameRunning();

    /**
     * Executed right after "CLICK TO CONTINUE"
     */
    void initGameLoaded();
}