package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * For accessing game class properties
 */
public class ReflectionUtil {

    private final static Logger log = Loggers.getLogger(ReflectionUtil.class);

    public static void setField(String fieldName, Object instance, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        setField(field, instance, newValue);
    }

    public static void setField(Field field, Object instance, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);

        field.set(instance, newValue);
    }

    public static <T> Optional<T> getField(String fieldName, Object instance)  {
        try {
            Field field = instance.getClass().getDeclaredField(fieldName);
            return getField(field, instance);
        } catch (NoSuchFieldException e) {
            log.error("Field %s does not exist", fieldName, e);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> getField(Field field, Object instance) {
        field.setAccessible(true);

        try {
            //noinspection unchecked
            return Optional.of((T) field.get(instance));
        } catch (Exception e) {
            log.error("Can not access field %s in %s.",
                    field.getName(), instance.getClass().getSimpleName(), e);
            return Optional.empty();
        }
    }
}
