package com.github.argon.sos.interactions.game.json.mapper.legacy;

import com.github.argon.sos.interactions.game.json.element.JsonNull;
import com.github.argon.sos.interactions.util.ClassUtil;
import snake2d.util.file.JsonE;

public class LegacyNullMapper extends LegacyMapper<JsonNull> {

    @Override
    public JsonE map(JsonE json, String key, JsonNull jsonElement) {
        json.add(key, "null");
        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonNull.class);
    }
}
