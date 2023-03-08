package com.github.argon.sos.interactions.util;

import java.lang.reflect.Type;

public class ClassUtil {
    public static boolean instanceOf(Object object, Class<?> clazz) {
        return instanceOf(object.getClass(), clazz);
    }

    public static boolean instanceOf(Object object, Object otherObject) {
        return instanceOf(object.getClass(), otherObject.getClass());
    }

    public static boolean instanceOf(Class<?> clazz, Class<?> otherClazz) {
        if (clazz.getCanonicalName().equals(otherClazz.getCanonicalName())) {
            return true;
        }

        if (otherClazz.isAssignableFrom(clazz)) {
            return true;
        }

        return false;
    }

    public static boolean sameAs(Type type, Class<?> clazz) {
        return type.getTypeName().equals(clazz.getTypeName());
    }
}
