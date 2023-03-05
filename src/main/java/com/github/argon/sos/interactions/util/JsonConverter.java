package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import snake2d.util.file.JsonE;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import static com.github.argon.sos.interactions.util.ClassCastUtil.*;
import static com.github.argon.sos.interactions.util.MethodUtil.isGetterMethod;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonConverter {

    private final static Logger log = Loggers.getLogger(JsonConverter.class);

    @Getter(lazy = true)
    private final static JsonConverter instance = new JsonConverter();

    public JsonE fromPojo(Object object) {
        Class<?> clazz = object.getClass();
        JsonE json = new JsonE();

        log.debug("> CONVERTING Pojo %s", clazz.getCanonicalName());
        for (Method method : clazz.getDeclaredMethods()) {
            if (!isGetterMethod(method)) {
                continue;
            }

            String fieldName = extractFieldName(method);
            Field field = ReflectionUtil.getField(fieldName, clazz)
                .orElseThrow(() -> new JsonConversionException(
                    "No field for getter method " + method.getName() + " in class " + clazz.getCanonicalName()));
            Type fieldType = field.getGenericType();

            Object returned;
            try {
                returned = ReflectionUtil.invokeMethod(method, object);
            } catch (RuntimeException e) {
                log.warn("Could not call getter method %s#%s. Skipping.", clazz.getCanonicalName(), method.getName(), e);
                continue;
            }

            String key = toJsonKey(method);
            log.trace("Converting %s from '%s()' for '%s' with %s",
                returned, method.getName(), key, fieldType.getTypeName());
            insertValue(json, key, returned, fieldType);
        }

        return json;
    }

    public JsonE fromMap(Map<?, ?> map, Type genericValueType) {
        JsonE json = new JsonE();
        log.trace("> CONVERTING Map<?, ?> with value type %s", genericValueType.getTypeName());

        map.forEach((mapkey, mapValue) -> {
            String key = ClassCastUtil.toString(mapkey);
            // recursion
            insertValue(json, key, mapValue, genericValueType);
        });

        return json;
    }

    public JsonE[] fromObjectArray(Object[] objects) {
        JsonE[] jsons = new JsonE[objects.length];
        log.trace("> CONVERTING Object[] %s", objects.getClass().getTypeName());

        for (int i = 0, objectsLength = objects.length; i < objectsLength; i++) {
            Object object = objects[i];
            // recursion
            jsons[i] = fromPojo(object);
        }

        return jsons;
    }

    public JsonE[] fromCollection(Collection<?> objects) {
        JsonE[] jsons = new JsonE[objects.size()];
        int i = 0;
        log.trace("CONVERTING Collection<?> %s", objects.getClass().getTypeName());

        for (Object object : objects) {
            // recursion
            jsons[i] = fromPojo(object);
            i++;
        }

        return jsons;
    }

    private JsonE insertValue(JsonE json, String key, Object value, Type fieldType) {
        if (value instanceof Integer) {
            json.add(key, (int) value);
        } else if (value instanceof Long) {
            json.add(key, toInt((Long) value));
        } else if (value instanceof Byte) {
            json.add(key, toInt((Byte) value));
        } else if (value instanceof Short) {
            json.add(key, toInt((Short) value));
        } else if (value instanceof String) {
            json.addString(key, (String) value);
        } else if (value instanceof Double) {
            json.add(key, (double) value);
        } else if (value instanceof Float) {
            json.add(key, toDouble((Float) value));
        } else if (value instanceof BigDecimal) {
            json.add(key, toDouble((BigDecimal) value));
        } else if (value instanceof Boolean) {
            json.add(key, (boolean) value);
        } else if (value instanceof JsonE) {
            json.add(key, (JsonE) value);
        } else if (value instanceof Enum<?>) {
            json.addString(key, ClassCastUtil.toString((Enum<?>) value));
        } else if (value instanceof Collection) {
            Type[] genericTypes = ReflectionUtil.getGenericTypes(fieldType);
            injectCollection(json, key, (Collection<?>) value, genericTypes[0]);
        } else if (isArray(value)) {
            injectArray(json, key, value);
        } else if (value instanceof Map) {
            Type[] genericTypes = ReflectionUtil.getGenericTypes(fieldType);
            JsonE jsonMap = fromMap((Map<?, ?>) value, genericTypes[1]);
            json.add(key, jsonMap);
        } else {
            // recursion fixme DANGER endless loop, when a type can not be assigned
            JsonE jsonPojo = fromPojo(value);
            json.add(key, jsonPojo);
        }

        return json;
    }

    private JsonE injectArray(JsonE json, String key, Object objects) {
        if (objects instanceof Integer[]) {
            json.add(key, toIntArray((Integer[]) objects));
        } else if (objects instanceof int[]) {
            json.add(key, (int[]) objects);
        } else if (objects instanceof Long[]) {
            json.add(key, toIntArray((Long[]) objects));
        } else if (objects instanceof BigDecimal[]) {
            json.add(key, toDoubleArray((BigDecimal[]) objects));
        } else if (objects instanceof long[]) {
            json.add(key, toIntArray((long[]) objects));
        } else if (objects instanceof Byte[]) {
            json.add(key, toIntArray((Byte[]) objects));
        } else if (objects instanceof byte[]) {
            json.add(key, toIntArray((byte[]) objects));
        } else if (objects instanceof Short[]) {
            json.add(key, toIntArray((Short[]) objects));
        } else if (objects instanceof short[]) {
            json.add(key, toIntArray((short[]) objects));
        } else if (objects instanceof Enum[]) {
            json.addStrings(key, toStringArray((Enum<?>[]) objects));
        } else if (objects instanceof String[]) {
            json.addStrings(key, (String[]) objects);
        } else if (objects instanceof double[]) {
            json.add(key, (double[]) objects);
        } else if (objects instanceof Double[]) {
            json.add(key, toDoubleArray((Double[]) objects));
        } else if (objects instanceof float[]) {
            json.add(key, toDoubleArray((float[]) objects));
        } else if (objects instanceof Float[]) {
            json.add(key, toDoubleArray((Float[]) objects));
        } else if (objects instanceof Object[]){
            // recursion
            json.add(key, fromObjectArray((Object[]) objects));
        } else {
            throw new JsonConversionException("Unknown class to inject as array: " + objects.getClass().getTypeName());
        }

        return json;
    }

    private JsonE injectCollection(JsonE json, String key, Collection<?> objects, Type genericType) {
        Class<?> genericClass = (Class<?>) genericType;
        if (instanceOf(genericClass, Integer.class)) {
            json.add(key, toIntArrayInteger(objects));
        } else if (instanceOf(genericClass, Long.class)) {
            json.add(key, toIntArrayLong(objects));
        } else if (instanceOf(genericClass, Byte.class)) {
            json.add(key, toIntArrayByte(objects));
        } else if (instanceOf(genericClass, Short.class)) {
            json.add(key, toIntArrayShort(objects));
        }  else if (instanceOf(genericClass, String.class)) {
            json.addStrings(key, toStringArrayString(objects));
        } else if (instanceOf(genericClass, Double.class)) {
            json.add(key, toDoubleArrayDouble(objects));
        } else if (instanceOf(genericClass, Float.class)) {
            json.add(key, toDoubleArrayFloat(objects));
        } else if (instanceOf(genericClass, BigDecimal.class)) {
            json.add(key, toDoubleArrayBigDecimal(objects));
        } else if (instanceOf(genericClass, Enum.class)) {
            json.add(key, toStringArrayEnum(objects));
        } else {
            JsonE[] jsons = fromCollection(objects);
            // recursion
            json.add(key, jsons);
        }

        return json;
    }

    private String toJsonKey(Method method) {
        String methodName = method.getName();
        String name = stripMethodPrefix(methodName);

        return StringUtil.toScreamingSnakeCase(name);
    }

    private String extractFieldName(Method method) {
        String methodName = method.getName();
        String name = stripMethodPrefix(methodName);

        return StringUtil.unCapitalize(name);
    }

    private String stripMethodPrefix(String methodName) {
        String name;

        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            name = methodName.substring(3);
        } else if (methodName.startsWith("is")) {
            name = methodName.substring(2);
        } else {
            name = methodName;
        }

        return name;
    }
}
