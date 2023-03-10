package com.github.argon.sos.interactions.config.mapper.version;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.io.IOException;

public interface ConfigMapper {
    FilePutter toSaveGame(FilePutter file, RaceInteractionsConfig config);
    RaceInteractionsConfig fromSaveGame(FileGetter file) throws IOException;
    RaceInteractionsConfig fromJson(Json configJson);
    JsonE toJson(RaceInteractionsConfig config);
}
