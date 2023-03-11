package com.github.argon.sos.interactions.game.api;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import game.GAME;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameApi {
    private final static Logger log = Loggers.getLogger(GameApi.class);

    @Getter(lazy = true)
    private final static GameApi instance = new GameApi();

    public int getMajorVersion() {
        return game.VERSION.versionMajor(GAME.version());
    }

}
