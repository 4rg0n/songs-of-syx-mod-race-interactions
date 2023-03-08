package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import snake2d.Errors;
import snake2d.util.file.Json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.github.argon.sos.interactions.util.ClassCastUtil.*;
import static com.github.argon.sos.interactions.util.ClassUtil.instanceOf;
import static com.github.argon.sos.interactions.util.MethodUtil.*;
import static com.github.argon.sos.interactions.util.ReflectionUtil.invokeMethodOneArgument;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUnmarshaller {

    private final static Logger log = Loggers.getLogger(JsonUnmarshaller.class);

    @Getter(lazy = true)
    private final static JsonUnmarshaller instance = new JsonUnmarshaller();

    public <T> T toPojo(Json json, Class<T> clazz) {
        if (!ReflectionUtil.hasNoArgsConstructor(clazz)) {
            throw new JsonConversionException("Class " + clazz.getCanonicalName() + " needs a no argument constructor");
        }

        T instance;
        try {
            instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new JsonConversionException("Can not create instance of class " + clazz.getCanonicalName(), e);
        }

        return toPojo(json, instance);
    }

    public <T> T toPojo(Json json, T pojo) {
        Class<?> clazz = pojo.getClass();
        log.debug("UNMARSHALL Pojo %s", clazz.getCanonicalName());

        // todo optional implement Serializable + version check

        for (Method method : clazz.getDeclaredMethods()) {
            if (!isSetterMethod(method)) {
                continue;
            }
            insert(json, pojo, method);
        }

        return pojo;
    }

    public void insert(Json json, Object pojo, Method method) {
        Class<?> clazz = pojo.getClass();

        String fieldName = extractSetterGetterFieldName(method);
        Field field = ReflectionUtil.getDeclaredField(fieldName, clazz)
            .orElseThrow(() -> new JsonConversionException(
                "No field for setter method " + method.getName() + " in " + clazz.getCanonicalName()));
        Type fieldType = field.getGenericType();
        Class<?> fieldClass = field.getType();

        try {
            invoke(json, pojo, method, fieldType, fieldClass);
        } catch (Exception e) {
            throw new JsonConversionException(
                "Could not invoke " + clazz.getCanonicalName() + "#" + method.getName() + "(" + fieldClass.getTypeName() + ")", e);
        }
    }

    private void invoke(Json json, Object pojo, Method method, Type valueType, Class<?> valueClass) {
        String key = toJsonKey(method);

        log.trace("Unmarshall into '%s(%s)' from '%s'",
            method.getName(), valueType.getTypeName(), key);

        if (instanceOf(valueClass, Integer.class)) {
            invokeMethodOneArgument(method, pojo, json.i(key));
        } else if (instanceOf(valueClass, int.class)) {
            invokeMethodOneArgument(method, pojo, json.i(key));
        } else if (instanceOf(valueClass, Double.class)) {
            invokeMethodOneArgument(method, pojo, json.d(key));
        } else if (instanceOf(valueClass, double.class)) {
            invokeMethodOneArgument(method, pojo, json.d(key));
        } else if (instanceOf(valueClass, String.class)) {
            invokeMethodOneArgument(method, pojo, json.text(key));
        } else if (instanceOf(valueClass, Boolean.class)) {
            invokeMethodOneArgument(method, pojo, json.bool(key));
        } else if (instanceOf(valueClass, boolean.class)) {
            invokeMethodOneArgument(method, pojo, json.bool(key));
        } else if (instanceOf(valueClass, Long.class)) {
            invokeMethodOneArgument(method, pojo, toLong(json.i(key)));
        } else if (instanceOf(valueClass, long.class)) {
            invokeMethodOneArgument(method, pojo, toLong(json.i(key)));
        } else if (instanceOf(valueClass, Byte.class)) {
            invokeMethodOneArgument(method, pojo, toByte(json.i(key)));
        } else if (instanceOf(valueClass, byte.class)) {
            invokeMethodOneArgument(method, pojo, toByte(json.i(key)));
        } else if (instanceOf(valueClass, Short.class)) {
            invokeMethodOneArgument(method, pojo, toShort(json.i(key)));
        } else if (instanceOf(valueClass, short.class)) {
            invokeMethodOneArgument(method, pojo, toShort(json.i(key)));
        } else if (instanceOf(valueClass, Float.class)) {
            invokeMethodOneArgument(method, pojo, toFloat(json.d(key)));
        } else if (instanceOf(valueClass, float.class)) {
            invokeMethodOneArgument(method, pojo, toFloat(json.d(key)));
        } else if (instanceOf(valueClass, BigDecimal.class)) {
            invokeMethodOneArgument(method, pojo, toBigDecimal(json.d(key)));
        } else if (instanceOf(valueClass, Enum.class)) {
            invokeMethodOneArgument(method, pojo, toEnum(json.text(key), valueClass));
        } else if (valueClass.isArray()) {
            // recursion
            invokeArray(json, pojo, method, valueType, valueClass);
        } else if (instanceOf(valueClass, Collection.class)) {
            // recursion
            invokeCollection(json, pojo, method, valueType);
        }  else if (instanceOf(valueClass, Map.class)) {
            // todo recursion
            invokeMap(json, pojo, method, valueType);
        } else {
            // todo recursion
        }
    }

    private void invokeMap(Json json, Object pojo, Method method, Type mapType) {
        Type[] genericTypes = ReflectionUtil.getGenericTypes(mapType);
        Type genericType = genericTypes[1];
        Class<?> genericClass = (Class<?>) genericType;
        String key = toJsonKey(method);


        if (instanceOf(genericClass, Integer.class)) {
        } else if (instanceOf(genericClass, Long.class)) {
        } else if (instanceOf(genericClass, Byte.class)) {
        } else if (instanceOf(genericClass, Short.class)) {
        } else if (instanceOf(genericClass, String.class)) {
        } else if (instanceOf(genericClass, Double.class)) {
        } else if (instanceOf(genericClass, Float.class)) {
        } else if (instanceOf(genericClass, BigDecimal.class)) {
        } else if (instanceOf(genericClass, Enum.class)) {
        } else {
            Json[] jsons = json.jsons(key);
            Collection collection = toPojoCollection(jsons, genericClass);
            invokeMethodOneArgument(method, pojo, collection);
        }
    }

    private Map<?, ?> toMap(Json jsonMap, Type genericValueType) {
        Map map = new HashMap<>();

        jsonMap.keys().forEach(key -> {
            Json json = jsonMap.json(key);
            map.put(ClassCastUtil.toString(key), toPojo(json, (Class<?>) genericValueType));
        });

        return map;
    }

    private void invokeArray(Json json, Object pojo, Method method, Type valueType, Class<?> valueClass) {
        String key = toJsonKey(method);

        if (instanceOf(valueClass, Integer[].class)) {
            invokeMethodOneArgument(method, pojo, ClassCastUtil.toIntegerArray(json.is(key)));
        } else if (instanceOf(valueClass, int[].class)) {
            invokeMethodOneArgument(method, pojo, json.is(key));
        } else if (instanceOf(valueClass, Long[].class)) {
            invokeMethodOneArgument(method, pojo, ClassCastUtil.toLongArray(json.is(key)));
        } else if (instanceOf(valueClass, BigDecimal[].class)) {
            invokeMethodOneArgument(method, pojo, ClassCastUtil.toBigDecimalArray(json.ds(key)));
        } else if (instanceOf(valueClass, long[].class)) {
            invokeMethodOneArgument(method, pojo, PrimitivesCastUtil.toLongArray(json.is(key)));
        } else if (instanceOf(valueClass, Byte[].class)) {
            invokeMethodOneArgument(method, pojo, ClassCastUtil.toByteArray(json.is(key)));
        } else if (instanceOf(valueClass, byte[].class)) {
            invokeMethodOneArgument(method, pojo, PrimitivesCastUtil.toByteArray(json.is(key)));
        } else if (instanceOf(valueClass, Short[].class)) {
            invokeMethodOneArgument(method, pojo, ClassCastUtil.toShortArray(json.is(key)));
        } else if (instanceOf(valueClass, short[].class)) {
            invokeMethodOneArgument(method, pojo, PrimitivesCastUtil.toShortArray(json.is(key)));
        } else if (instanceOf(valueClass, Enum[].class)) {
            invokeMethodOneArgument(method, pojo,
                ClassCastUtil.toEnumArray(json.texts(key), valueClass.getComponentType()));
        } else if (instanceOf(valueClass, String[].class)) {
            invokeMethodOneArgument(method, pojo, json.texts(key));
        } else if (instanceOf(valueClass, double[].class)) {
            invokeMethodOneArgument(method, pojo, json.ds(key));
        } else if (instanceOf(valueClass, Double[].class)) {
            invokeMethodOneArgument(method, pojo, ClassCastUtil.toDoubleArray(json.ds(key)));
        } else if (instanceOf(valueClass, float[].class)) {
            invokeMethodOneArgument(method, pojo, PrimitivesCastUtil.toFloatArray(json.ds(key)));
        } else if (instanceOf(valueClass, Float[].class)) {
            invokeMethodOneArgument(method, pojo, ClassCastUtil.toFloatArray(json.ds(key)));
        } else if (instanceOf(valueClass, Object[].class)) {
            // todo recursion
            Object[] pojos = toPojoArray(json, method, valueClass.getComponentType(), valueClass);
            invokeMethodOneArgument(method, pojo, pojos);
        } else {
            throw new JsonConversionException(
                "Unknown class '" + valueType.getTypeName() + "' to invoke into " + 
                pojo.getClass().getCanonicalName() + "#" + method.getName() + " as array: ");
        }
    }

    private <T> T[] toPojoArray(Json json, Method method, Class<T> arrayEntryClass, Class<?> arrayClass) {
        String key = toJsonKey(method);
        Json[] jsons;

        try {
            jsons = json.jsons(key);
        } catch (Errors.DataError e) {
            log.trace("Json: %s", json);
            throw new JsonConversionException("Could not get an array of jsons from " + key, e);
        }

        T[] instances = (T[]) Array.newInstance(arrayEntryClass, jsons.length);
        for (int i = 0; i < jsons.length; i++) {
            Json json1 = jsons[i];
            instances[i] = toPojo(json1, arrayEntryClass);
        }

        return instances;
    }

    private void invokeCollection(Json json, Object pojo, Method method, Type collectionType) {
        Type[] genericTypes = ReflectionUtil.getGenericTypes(collectionType);
        Type genericType = genericTypes[0];
        Class<?> genericClass = (Class<?>) genericType;
        String key = toJsonKey(method);

        if (instanceOf(genericClass, Integer.class)) {
            invokeMethodOneArgument(method, pojo,
                toCollection(toIntegerArray(json.is(key))));
        } else if (instanceOf(genericClass, Long.class)) {
            invokeMethodOneArgument(method, pojo,
                toCollection(toLongArray(json.is(key))));
        } else if (instanceOf(genericClass, Byte.class)) {
            invokeMethodOneArgument(method, pojo,
                toCollection(toByteArray(json.is(key))));
        } else if (instanceOf(genericClass, Short.class)) {
            invokeMethodOneArgument(method, pojo,
                toCollection(toShortArray(json.is(key))));
        } else if (instanceOf(genericClass, String.class)) {
            invokeMethodOneArgument(method, pojo,
                toCollection(toStringArray(json.texts(key))));
        } else if (instanceOf(genericClass, Double.class)) {
            invokeMethodOneArgument(method, pojo,
                toCollection(toDoubleArray(json.ds(key))));
        } else if (instanceOf(genericClass, Float.class)) {
            invokeMethodOneArgument(method, pojo,
                toCollection(toFloatArray(json.ds(key))));
        } else if (instanceOf(genericClass, BigDecimal.class)) {
            invokeMethodOneArgument(method, pojo,
                toCollection(toBigDecimalArray(json.ds(key))));
        } else if (instanceOf(genericClass, Enum.class)) {
            invokeMethodOneArgument(method, pojo,
                toCollection(toStringArray(json.texts(key))));
        } else if (instanceOf(genericClass, Collection.class)) {
            Json[] jsons = json.jsons(key);
            Collection collection = toPojoCollection(jsons, genericClass);
            invokeMethodOneArgument(method, pojo, collection);
        } else if (instanceOf(genericClass, Map.class)) {
           // todo
        } else if (genericClass.isArray()) {
            // todo
        } else {
           // todo
        }
    }

    private Collection toPojoCollection(Json[] jsons, Class<?> genericClass) {
        Collection collection = new ArrayList<>();

        for (int i = 0; i < jsons.length; i++) {
            Object pojoEntry = toPojo(jsons[i], genericClass);
            collection.add(pojoEntry);
        }

        return collection;
    }
}
