package com.github.argon.sos.interactions.game.json.mapper.jsone;

import com.github.argon.sos.interactions.game.json.element.JsonString;
import com.github.argon.sos.interactions.util.ClassUtil;
import snake2d.util.file.JsonE;

public class JsonEStringMapper extends JsonEMapper<JsonString> {

    @Override
    public JsonE map(JsonE json, String key, JsonString jsonElement) {
        json.addString(key, jsonElement.getValue());

        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonString.class);
    }
}
