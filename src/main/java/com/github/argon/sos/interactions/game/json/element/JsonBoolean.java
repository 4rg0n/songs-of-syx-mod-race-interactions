package com.github.argon.sos.interactions.game.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonBoolean extends JsonElement {
    private final Boolean value;

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
