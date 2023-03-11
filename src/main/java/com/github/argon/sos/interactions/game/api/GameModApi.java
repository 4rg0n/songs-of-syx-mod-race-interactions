package com.github.argon.sos.interactions.game.api;

import com.github.argon.sos.interactions.Mapper;
import com.github.argon.sos.interactions.RaceInteractionsModScript;
import com.github.argon.sos.interactions.config.OtherModInfo;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import init.paths.ModInfo;
import init.paths.PATHS;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.github.argon.sos.interactions.config.RaceInteractionsConfig.Default.MOD_FILE_PATH;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GameModApi {
    private final static Logger log = Loggers.getLogger(GameModApi.class);

    @Getter(lazy = true)
    private final static GameModApi instance = new GameModApi(GameApi.getInstance());

    private final GameApi gameApi;

    public List<ModInfo> getCurrentMods() {
        return Mapper.toJavaList(PATHS.currentMods());
    }

    /**
     * @return paths to configuration found in other active mods
     */
    public List<OtherModInfo> findOtherModsWithConfig() {
        List<ModInfo> currentMods = getCurrentMods();
        List<OtherModInfo> otherMods = new ArrayList<>();

        for (ModInfo currentMod : currentMods) {
            // skip own mod
            if (RaceInteractionsModScript.MOD_INFO.name.equals(currentMod.name)) {
                continue;
            }

            Path absolutePath = Paths.get(currentMod.absolutePath);
            int versionMajor = gameApi.getMajorVersion();
            Path modFileRelativePath = Paths.get("V" + versionMajor + "/" + MOD_FILE_PATH);
            Path modFilePath = absolutePath.resolve(modFileRelativePath);

            if (Files.exists(modFilePath)) {
                otherMods.add(OtherModInfo.builder()
                    .configPath(modFilePath)
                    .absolutePath(absolutePath)
                    .name(currentMod.name)
                    .description(currentMod.desc)
                    .build());
            }
        }

        return otherMods;
    }
}
