package com.github.argon.sos.interactions.game.json.mapper.legacy;

import com.github.argon.sos.interactions.game.json.JsonMapper;
import com.github.argon.sos.interactions.game.json.element.JsonObject;
import com.github.argon.sos.interactions.util.ClassUtil;
import snake2d.util.file.JsonE;

public class LegacyObjectMapper extends LegacyMapper<JsonObject> {

    @Override
    public JsonE map(JsonE json, String key, JsonObject jsonElement) {
        json.add(key, JsonMapper.mapLegacy(jsonElement));

        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonObject.class);
    }
}
