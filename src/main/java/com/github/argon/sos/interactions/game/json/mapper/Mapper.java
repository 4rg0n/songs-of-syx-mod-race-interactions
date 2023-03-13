package com.github.argon.sos.interactions.game.json.mapper;

import com.github.argon.sos.interactions.game.json.element.JsonElement;

public abstract class Mapper<T extends JsonElement> {
    public Mapper() {
    }

    /**
     * @param clazz nullable
     */
    public abstract boolean supports(Class<?> clazz);
    public abstract Object mapJson(T json, TypeInfo<?> typeInfo);
    public abstract T mapObject(Object object, TypeInfo<?> typeInfo);
}
