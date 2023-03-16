package com.github.argon.sos.interactions.game.json.util;

import com.github.argon.sos.interactions.game.json.JsonParser;
import com.github.argon.sos.interactions.util.MethodUtil;
import com.github.argon.sos.interactions.util.StringUtil;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {
    /**
     * This exists to make it easier for the {@link JsonParser} to process the games json format
     */
    public static String normalizeJson(String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            return jsonString;
        }

        // remove comments
        Pattern regex = Pattern.compile("^\\s*\\*\\*\\s*.*", Pattern.MULTILINE);
        Matcher regexMatcher = regex.matcher(jsonString);
        jsonString = regexMatcher.replaceAll("");

        if (jsonString.charAt(0) != '{') {
            // wrap everything in {}
            jsonString = "{" + jsonString + "}";
        }

        // remove trailing commas
        jsonString = jsonString.replaceAll("\\s*,\\s*}", "}");
        jsonString = jsonString.replaceAll("\\s*,\\s*]", "]");

        return jsonString;
    }

    public static String toJsonKey(Method method) {
        String name = MethodUtil.extractSetterGetterFieldName(method);
        return StringUtil.toScreamingSnakeCase(name);
    }
}
