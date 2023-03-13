package com.github.argon.sos.interactions.game.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
@EqualsAndHashCode
public class JsonArray extends JsonElement {
    private final List<JsonElement> elements = new ArrayList<>();

    public JsonElement get(int index) {
        return elements.get(index);
    }

    public boolean add(JsonElement jsonElement) {
        return elements.add(jsonElement);
    }

    @Override
    public String toString() {
        return elements.toString();
    }
}
