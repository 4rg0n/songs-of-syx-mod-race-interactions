package com.github.argon.sos.interactions.game.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class JsonObject implements JsonElement {

    @Getter
    private final Map<String, JsonElement> map = new HashMap<>();

    public JsonElement get(String key) {
        return map.get(key);
    }

    public JsonElement put(String key, JsonElement value) {
        return map.put(key, value);
    }

    public JsonElement put(Enum<?> key, JsonElement value) {
        return map.put(key.name(), value);
    }

    @Override
    public String toString() {
        return map.keySet().stream()
            .map(key -> key + ": " + map.get(key))
            .collect(Collectors.joining(", ", "{", "}"));
    }
}
