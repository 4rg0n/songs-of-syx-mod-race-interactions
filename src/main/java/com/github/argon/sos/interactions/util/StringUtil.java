package com.github.argon.sos.interactions.util;

import java.util.Map;
import java.util.stream.Collectors;

public class StringUtil {
    public static String toString(Map<?, ?> map) {
        return map.keySet().stream()
            .map(key -> key + "=" + map.get(key))
            .collect(Collectors.joining(", ", "{", "}"));
    }
}
