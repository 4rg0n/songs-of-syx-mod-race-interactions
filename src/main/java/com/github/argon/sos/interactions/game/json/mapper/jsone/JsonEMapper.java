package com.github.argon.sos.interactions.game.json.mapper.jsone;

import com.github.argon.sos.interactions.game.json.element.JsonElement;
import snake2d.util.file.JsonE;

/**
 * Common mapper class for mapping into the games {@link JsonE}
 *
 * @param <T> {@link JsonElement} to map from
 */
public interface JsonEMapper<T extends JsonElement> {
    JsonE mapJsonE(JsonE json, String key, T jsonElement);
    boolean supports(Class<?> clazz);
}
