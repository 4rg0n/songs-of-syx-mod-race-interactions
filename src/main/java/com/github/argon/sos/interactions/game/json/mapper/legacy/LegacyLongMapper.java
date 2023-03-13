package com.github.argon.sos.interactions.game.json.mapper.legacy;

import com.github.argon.sos.interactions.game.json.element.JsonLong;
import com.github.argon.sos.interactions.util.ClassUtil;
import snake2d.util.file.JsonE;

public class LegacyLongMapper extends LegacyMapper<JsonLong> {

    @Override
    public JsonE map(JsonE json, String key, JsonLong jsonElement) {
        json.add(key,  jsonElement.getValue().intValue());

        return json;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ClassUtil.instanceOf(clazz, JsonLong.class);
    }
}
