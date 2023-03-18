package com.github.argon.sos.interactions.game.json.element;

import com.github.argon.sos.interactions.util.StringUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class JsonString implements JsonElement {
    private final String value;

    @Override
    public String toString() {
        return StringUtil.quote(value);
    }
}
