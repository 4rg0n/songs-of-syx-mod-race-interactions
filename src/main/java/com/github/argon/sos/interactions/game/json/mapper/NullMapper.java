package com.github.argon.sos.interactions.game.json.mapper;

import com.github.argon.sos.interactions.game.json.element.JsonNull;

public class NullMapper implements Mapper<JsonNull> {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == null || clazz == Void.class;
    }

    @Override
    public Object mapJson(JsonNull json, TypeInfo<?> typeInfo) {
        return null;
    }

    @Override
    public JsonNull mapObject(Object object, TypeInfo<?> typeInfo) {
        return new JsonNull();
    }
}
