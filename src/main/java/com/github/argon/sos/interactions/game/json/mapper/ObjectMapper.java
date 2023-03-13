package com.github.argon.sos.interactions.game.json.mapper;

import com.github.argon.sos.interactions.game.json.JsonMapper;
import com.github.argon.sos.interactions.game.json.element.JsonElement;
import com.github.argon.sos.interactions.game.json.element.JsonObject;
import com.github.argon.sos.interactions.util.ClassUtil;
import com.github.argon.sos.interactions.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static com.github.argon.sos.interactions.util.MethodUtil.*;
import static com.github.argon.sos.interactions.util.ReflectionUtil.invokeMethodOneArgument;

public class ObjectMapper extends Mapper<JsonObject> {

    @Override
    public boolean supports(Class<?> clazz) {
        // todo I don't like this
        return clazz != null
            && !clazz.isEnum()
            && !clazz.isArray()
            && !Arrays.asList(
                    Void.class,
                    Boolean.class,
                    boolean.class,
                    Double.class,
                    Float.class,
                    BigDecimal.class,
                    double.class,
                    float.class,
                    Integer.class,
                    Byte.class,
                    Short.class,
                    Long.class,
                    BigInteger.class,
                    int.class,
                    short.class,
                    byte.class,
                    long.class
                ).contains(clazz)
            && !ClassUtil.instanceOf(clazz, Collection.class)
            && !ClassUtil.instanceOf(clazz, Map.class)
            && !ClassUtil.instanceOf(clazz, CharSequence.class);
    }

    @Override
    public Object mapJson(JsonObject json, TypeInfo<?> typeInfo) {
        Class<?> clazz = typeInfo.getTypeClass();

        if (!ReflectionUtil.hasNoArgsConstructor(clazz)) {
            throw new JsonMapperException("Class " + clazz.getCanonicalName() + " needs a no argument constructor");
        }

        Object instance;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new JsonMapperException("Can not create instance of class " + clazz.getCanonicalName(), e);
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (!isSetterMethod(method)) {
                continue;
            }

            String jsonKey = toJsonKey(method);
            if (!json.getMap().containsKey(jsonKey)) {
                continue;
            }

            String fieldName = extractSetterGetterFieldName(method);
            Field field = ReflectionUtil.getDeclaredField(fieldName, clazz)
                .orElseThrow(() -> new JsonMapperException(
                    "No field for setter method " + method.getName() + " in " + clazz.getCanonicalName()
                ));
            Type fieldType = field.getGenericType();
            TypeInfo<?> fieldTypeInfo = TypeInfo.get(fieldType);
            JsonElement jsonElement = json.getMap().get(jsonKey);

            Object mappedObject = JsonMapper.mapJson(jsonElement, fieldTypeInfo);
            // call setter method
            invokeMethodOneArgument(method, instance, mappedObject);
        }

        return instance;
    }

    @Override
    public JsonObject mapObject(Object object, TypeInfo<?> typeInfo) {
        Class<?> clazz = typeInfo.getTypeClass();

        JsonObject jsonObject = new JsonObject();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!isGetterMethod(method)) {
                continue;
            }

            String jsonKey = toJsonKey(method);
            String fieldName = extractSetterGetterFieldName(method);
            Field field = ReflectionUtil.getDeclaredField(fieldName, clazz)
                .orElseThrow(() -> new JsonMapperException(
                    "No field for setter method " + method.getName() + " in " + clazz.getCanonicalName()
                ));
            Type fieldType = field.getGenericType();
            TypeInfo<?> fieldTypeInfo = TypeInfo.get(fieldType);

            // call getter method
            Object returnedObject = ReflectionUtil.invokeMethod(method, object);
            jsonObject.put(jsonKey, JsonMapper.mapObject(returnedObject, fieldTypeInfo));
        }

        return jsonObject;
    }
}
