package com.github.argon.sos.interactions.game.json.mapper.jsone;

import com.github.argon.sos.interactions.game.json.element.JsonNull;
import com.github.argon.sos.interactions.util.ClassUtil;
import snake2d.util.file.JsonE;

public class JsonENullMapper extends JsonEMapper<JsonNull> {

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
