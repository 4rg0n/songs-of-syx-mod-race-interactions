package com.github.argon.sos.interactions.game.json;

import com.github.argon.sos.interactions.game.json.element.JsonElement;
import com.github.argon.sos.interactions.game.json.element.JsonNull;
import com.github.argon.sos.interactions.game.json.element.JsonObject;
import com.github.argon.sos.interactions.game.json.mapper.JsonMapperException;
import com.github.argon.sos.interactions.game.json.mapper.Mapper;
import com.github.argon.sos.interactions.game.json.mapper.Mappers;
import com.github.argon.sos.interactions.game.json.mapper.TypeInfo;
import com.github.argon.sos.interactions.game.json.mapper.legacy.LegacyMapper;
import com.github.argon.sos.interactions.game.json.mapper.legacy.LegacyMappers;
import snake2d.util.file.JsonE;

/**
 * For mapping objects into and from a {@link JsonElement}.
 *
 * <pre>
 *     // TO {@link JsonElement} from pojo with getter methods
 *     MyPojo pojo = new MyPojo();
 *     // ... set stuff into pojo
 *     JsonObject jsonObject = (JsonObject) JsonMapper.mapObject(pojo);
 *
 *     // TO {@link JsonElement} from object
 *     Map<String, List<String>> map = new HashMap();
 *     // ... put stuff into map
 *     // you will need a {@link TypeInfo} with the type info as generic
 *     JsonObject jsonObject = (JsonObject) JsonMapper.mapObject(map, new TypeToken<Map<String, List<String>>>(){});
 *
 *     // TO pojo with setter methods from {@link JsonElement}
 *     MyPojo pojo = JsonMapper.mapJson(jsonObject, MyPojo.class);
 *
 *     // TO object from {@link JsonElement}
 *     JsonArray jsonArray = new JsonArray();
 *     jsonArray.add(new JsonLong(1L));
 *     jsonArray.add(new JsonLong(2L));
 *     jsonArray.add(new JsonLong(3L));
 *     List<Integer> integerList = JsonMapper.mapJson(jsonArray, new TypeToken<List<Integer>>(){});
 *
 *     // TO games {@link JsonE} from {@link JsonElement}
 *     JsonObject jsonObject = new JsonObject();
 *     jsonObject.put("1", new JsonLong(1L));
 *     jsonObject.put("2", new JsonLong(2L));
 *     jsonObject.put("3", new JsonLong(3L));
 *     JsonE jsonE = JsonMapper.mapLegacy(jsonObject);
 * </pre>
 */
public class JsonMapper {

    private final static Mappers mappers = Mappers.getDefault();
    private final static LegacyMappers legacyMappers = LegacyMappers.getDefault();

    public static <T> T mapJson(JsonElement json, Class<T> clazz) {
        return mapJson(json, TypeInfo.get(clazz));
    }
    public static <T> T mapJson(JsonElement json, TypeInfo<T> typeInfo) {
        Class<? super T> typeClass = typeInfo.getTypeClass();

        @SuppressWarnings("rawtypes")
        Mapper mapper = mappers.findOne(typeClass)
            .orElseThrow(() -> new JsonMapperException("No mapper found for json element " + typeClass.getTypeName()));

        try {
            //noinspection unchecked
            return (T) mapper.mapJson(json, typeInfo);
        } catch (RuntimeException e) {
            throw new JsonMapperException(
                "Could not map " + json.getClass().getSimpleName() + " to " +
                typeClass.getTypeName() + " with mapper " + mapper.getClass().getSimpleName(), e);
        }
    }

    public static JsonElement mapObject(Object object) {
        if (object == null) {
            return new JsonNull();
        }

        TypeInfo<?> typeInfo = TypeInfo.get(object.getClass());
        try {
            return mapObject(object, typeInfo);
        } catch (RuntimeException e) {
            throw new JsonMapperException("Could not map " + object.getClass().getTypeName() + " to JsonElement", e);
        }
    }

    public static JsonElement mapObject(Object object, TypeInfo<?> typeInfo) {
        if (object == null || typeInfo == null) {
            return new JsonNull();
        }
        Class<?> typeClass = typeInfo.getTypeClass();

        Mapper<?> mapper = mappers.findOne(typeClass)
            .orElseThrow(() -> new JsonMapperException("No mapper found for object " + typeClass.getTypeName()));

        try {
            return mapper.mapObject(object, typeInfo);
        } catch (RuntimeException e) {
            throw new JsonMapperException(
                "Could not map " + typeClass.getTypeName() +
                " to JsonElement with mapper " + mapper.getClass().getSimpleName(), e);
        }
    }

    public static JsonE mapLegacy(JsonObject jsonObject) {
        JsonE json = new JsonE();

        jsonObject.getMap().forEach((key, jsonElement) -> {
            JsonMapper.mapLegacy(json, key, jsonElement);
        });

        return json;
    }

    public static JsonE mapLegacy(JsonE json, String key, JsonElement jsonElement) {
        Class<? extends JsonElement> jsonElementClass = jsonElement.getClass();

        @SuppressWarnings("rawtypes")
        LegacyMapper legacyMapper = legacyMappers.findOne(jsonElementClass)
            .orElseThrow(() -> new JsonMapperException("No mapper found for json element " + jsonElementClass.getSimpleName()));

        try {
            //noinspection unchecked
            return legacyMapper.map(json, key, jsonElement);
        } catch (RuntimeException e) {
            throw new JsonMapperException(
                "Could not map " + jsonElementClass.getSimpleName() +
                    " to JsonE with mapper " + legacyMapper.getClass().getSimpleName(), e);
        }
    }
}
