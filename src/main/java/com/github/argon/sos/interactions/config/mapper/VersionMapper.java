package com.github.argon.sos.interactions.config.mapper;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class VersionMapper {
    private final static Logger log = Loggers.getLogger(VersionMapper.class);

    @Getter(lazy = true)
    private final static VersionMapper instance = new VersionMapper();

    public final static String KEY_VERSION = "VERSION";

    public Optional<Integer> extractVersion(Json json) {
        if (!json.has(KEY_VERSION)) {
            return Optional.empty();
        }

        try {
            return Optional.of(json.i(KEY_VERSION));
        } catch (Exception e) {
            log.debug("Could not extract version from json", e);
            log.trace("JSON: %s", json);
            return Optional.empty();
        }
    }

    public void injectVersion(JsonE json, int version) {
        json.add(KEY_VERSION, version);
    }

    public Optional<Integer> extractVersion(FileGetter file) {
        int positionBefore = file.getPosition();
        String versionString;

        try {
            versionString = file.chars();
        } catch (Exception e) {
            file.setPosition(positionBefore);
            log.info("Could not extract version from save file", e);
            return Optional.empty();
        }

        if (!versionString.startsWith(KEY_VERSION)) {
            file.setPosition(positionBefore);
            return Optional.empty();
        }

        String[] versionStringSplit = versionString.split(":");
        if (versionStringSplit.length < 2) {
            file.setPosition(positionBefore);
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.valueOf(versionStringSplit[1]));
        } catch (Exception e) {
            file.setPosition(positionBefore);
            log.info("Could not extract version from save file and string '%s'", versionString, e);
            return Optional.empty();
        }
    }

    public void injectVersion(FilePutter file, int version) {
        String versionString = KEY_VERSION + ":" + version;
        file.chars(versionString);
    }
}
