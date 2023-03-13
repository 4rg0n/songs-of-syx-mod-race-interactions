package com.github.argon.sos.interactions.game.json.element;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonDouble extends JsonElement {
    private final Double value;

    @Override
    public String toString() {
        return Double.toString(value);
    }
}
