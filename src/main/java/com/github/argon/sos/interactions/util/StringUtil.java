package com.github.argon.sos.interactions.util;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class StringUtil {

    public static String toString(Object[] objects) {
        return Arrays.toString(objects);
    }

    public static String toString(Map<?, ?> map) {
        return map.keySet().stream()
            .map(key -> key + "=" + map.get(key))
            .collect(Collectors.joining(", ", "{", "}"));
    }

    public static String toScreamingSnakeCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return text.replaceAll("\\B([A-Z])", "_$1").toUpperCase();
    }

    public static String unCapitalize(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }

    public static String toStringPrimitiveArray(Object arg) {
        if (arg instanceof int[]) {
            return Arrays.toString((int[]) arg);
        } else if (arg instanceof long[]) {
            return Arrays.toString((long[]) arg);
        } else if (arg instanceof short[]) {
            return Arrays.toString((short[]) arg);
        } else if (arg instanceof double[]) {
            return Arrays.toString((double[]) arg);
        } else if (arg instanceof float[]) {
            return Arrays.toString((float[]) arg);
        } else if (arg instanceof byte[]) {
            return Arrays.toString((byte[]) arg);
        } else if (arg instanceof boolean[]) {
            return Arrays.toString((boolean[]) arg);
        } else if (arg instanceof char[]) {
            return Arrays.toString((char[]) arg);
        }

        return arg.toString();
    }
}
