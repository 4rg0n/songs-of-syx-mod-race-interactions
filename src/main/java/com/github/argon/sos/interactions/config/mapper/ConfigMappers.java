package com.github.argon.sos.interactions.config.mapper;

import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.mapper.version.ConfigMapper;
import com.github.argon.sos.interactions.config.mapper.version.V1ConfigMapper;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.util.MapUtil;
import lombok.Getter;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import snake2d.util.file.Json;
import snake2d.util.file.JsonE;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;


public class ConfigMappers implements ConfigMapper {

    private final static Logger log = Loggers.getLogger(ConfigMappers.class);

    private final TreeMap<Integer, ConfigMapper> versionedMappers;
    private final VersionMapper versionMapper;

    @Getter(lazy = true)
    private final static ConfigMappers instance = new ConfigMappers(
        VersionMapper.getInstance(),
        MapUtil.of(
        1, V1ConfigMapper.getInstance()
        ));

    private ConfigMappers(VersionMapper versionMapper, Map<Integer, ConfigMapper> versionedMappers) {
        this.versionMapper = versionMapper;
        this.versionedMappers = new TreeMap<>(versionedMappers);
    }

    @Override
    public FilePutter toSaveGame(FilePutter file, RaceInteractionsConfig config) {
        Optional<ConfigMapper> optionalConfigMapper = getLatest();

        if (!optionalConfigMapper.isPresent()) {
            throw new ConfigMapperException("There are no mappers registered for writing configuration into safe file");
        }

        return optionalConfigMapper.get().toSaveGame(file, config);
    }

    @Override
    public RaceInteractionsConfig fromSaveGame(FileGetter file) throws IOException {
        Optional<Integer> optionalVersion = versionMapper.extractVersion(file);

        if (!optionalVersion.isPresent()) {
            log.info("No version found in save file. Using default.");
            return RaceInteractionsConfig.Default.getConfig();
        }

        Optional<ConfigMapper> optionalConfigMapper = get(optionalVersion.get());
        if (!optionalConfigMapper.isPresent()) {
            log.info("No mapper found for version %s. Using default.", optionalVersion.get());
            return RaceInteractionsConfig.Default.getConfig();
        }

        return optionalConfigMapper.get().fromSaveGame(file);
    }

    @Override
    public RaceInteractionsConfig fromJson(Json configJson) {
        Optional<Integer> optionalVersion = versionMapper.extractVersion(configJson);

        if (!optionalVersion.isPresent()) {
            log.info("No version found in json. Using default.");
            return RaceInteractionsConfig.Default.getConfig();
        }

        Optional<ConfigMapper> optionalConfigMapper = get(optionalVersion.get());
        if (!optionalConfigMapper.isPresent()) {
            log.info("No mapper found for version %s. Using default.", optionalVersion.get());
            return RaceInteractionsConfig.Default.getConfig();
        }

        return optionalConfigMapper.get().fromJson(configJson);
    }

    @Override
    public JsonE toJson(RaceInteractionsConfig config) {
        Optional<ConfigMapper> optionalConfigMapper = getLatest();

        if (!optionalConfigMapper.isPresent()) {
            throw new ConfigMapperException("There are no mappers registered for writing configuration into safe file");
        }

        return optionalConfigMapper.get().toJson(config);
    }

    private Optional<ConfigMapper> get(int version) {
        return Optional.ofNullable(versionedMappers.get(version));
    }

    private Optional<ConfigMapper> getLatest() {
        return Optional.ofNullable(versionedMappers.lastEntry())
            .map(Map.Entry::getValue);
    }


}
