package com.github.argon.sos.interactions.game;

public interface SCRIPT<T> extends script.SCRIPT {
    /**
     * Executed when the game is running
     */
    void initGameRunning();

    /**
     * Executed right after "CLICK TO CONTINUE"
     */
    void initGamePresent();

    /**
     * @param config nullable
     */
    void initGameSaveLoaded(T config);
}