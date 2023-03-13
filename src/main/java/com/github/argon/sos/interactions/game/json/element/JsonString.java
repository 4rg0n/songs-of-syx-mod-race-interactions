package com.github.argon.sos.interactions.game.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonString extends JsonElement {
    private final String value;

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
