package com.github.argon.sos.interactions.game.json.mapper.legacy;

import com.github.argon.sos.interactions.game.json.element.JsonElement;
import snake2d.util.file.JsonE;

public abstract class LegacyMapper<T extends JsonElement> {
    public abstract JsonE map(JsonE json, String key, T jsonElement);

    public abstract boolean supports(Class<?> clazz);
}
