package com.github.argon.sos.interactions.game.config;

import com.github.argon.sos.interactions.game.json.Json;
import com.github.argon.sos.interactions.game.json.JsonMapper;
import com.github.argon.sos.interactions.game.json.JsonWriter;
import com.github.argon.sos.interactions.game.json.element.JsonObject;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.util.ReflectionUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import snake2d.util.file.JsonE;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigManager {
    private final Logger log = Loggers.getLogger(ConfigManager.class);

    @Getter(lazy = true)
    private final static ConfigManager instance = new ConfigManager(
        FileService.getInstance(),
        JsonWriter.getJsonE()
    );

    private final FileService fileService;

    private final JsonWriter jsonWriter;

    private final Map<Class<?>, Path> configPaths = new HashMap<>();

    public <T> Optional<T> load(Path configDirectoryPath, Class<T> configClass) {
        return ReflectionUtil.getAnnotation(configClass, ConfigProperties.class)
            .flatMap(configProperties -> {
                String path = configProperties.path();
                Path relativePath = Paths.get(path);
                boolean isResource = configProperties.isResource();

                if (isResource) {
                    return loadResource(configClass, relativePath);
                }

                Path configPath = relativePath.resolve(configDirectoryPath);
                return load(configClass, configPath);
            });
    }

    public <T> Optional<T> load(Class<T> configClass) {
        if (!configPaths.containsKey(configClass)) {
            return Optional.empty();
        }

        return load(configClass, configPaths.get(configClass));
    }

    public <T> Optional<T> load(Class<T> configClass, Path path) {
        try {
            JsonObject jsonObject = json(path);
            T mappedObject = JsonMapper.mapJson(jsonObject, configClass);

            configPaths.putIfAbsent(configClass, path);
            return Optional.of(mappedObject);
        } catch (Exception e) {
            log.info("Could not load %s from %s: %s", configClass.getSimpleName(), path, e.getMessage());
            log.trace("", e);
            return Optional.empty();
        }
    }

    public <T> Optional<T> loadResource(Class<T> configClass) {
        if (!configPaths.containsKey(configClass)) {
            return Optional.empty();
        }

        return loadResource(configClass, configPaths.get(configClass));
    }

    public <T> Optional<T> loadResource(Class<T> configClass, Path path) {
        try {
            JsonObject jsonObject = jsonResource(path);
            T mappedObject = JsonMapper.mapJson(jsonObject, configClass);
            configPaths.putIfAbsent(configClass, path);
            return Optional.of(mappedObject);
        } catch (Exception e) {
            log.info("Could not load %s from %s: %s", configClass.getSimpleName(), path, e.getMessage());
            log.trace("", e);
            return Optional.empty();
        }
    }

    public boolean save(Path configDirectoryPath, Object object) {
        return ReflectionUtil.getAnnotation(object.getClass(), ConfigProperties.class)
            .map(ConfigProperties::path)
            .map(relativePath -> {
                Path path = Paths.get(relativePath);
                Path configPath = path.resolve(configDirectoryPath);

                return save(object, configPath);
            }).orElse(false);
    }

    public boolean save(Object object) {
        if (!configPaths.containsKey(object.getClass())) {
            return false;
        }

        return save(object, configPaths.get(object.getClass()));
    }

    public boolean save(Object object, Path path) {
        Class<?> objectClass = object.getClass();
        try {
            JsonObject jsonElement = (JsonObject) JsonMapper.mapObject(object);
            Json json = new Json(jsonElement, jsonWriter);

            fileService.write(json.toString(), path);
            configPaths.putIfAbsent(objectClass, path);
            return true;
        } catch (Exception e) {
            log.info("Could not save %s to %s: %s", objectClass.getCanonicalName(), path, e.getMessage());
            log.trace("", e);
            return false;
        }
    }

    public JsonObject json(Path path) throws IOException {
        String content = fileService.readFile(path);
        return new Json(content).getRoot();
    }

    public JsonObject jsonResource(Path path) throws IOException {
        String content = fileService.readResource(path);
        return new Json(content).getRoot();
    }

    public JsonE jsonEResource(Path path) throws IOException {
        return JsonMapper.mapJsonE(jsonResource(path));
    }

    public JsonE jsonE(Path path) throws IOException {
        return JsonMapper.mapJsonE(json(path));
    }

    public Path detach(Class<?> configClass) {
        return configPaths.remove(configClass);
    }

    public Path attach(Class<?> configClass, Path path) {
        return configPaths.put(configClass, path);
    }
}
