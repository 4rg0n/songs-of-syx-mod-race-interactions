package com.github.argon.sos.interactions.game.json;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonStringNormalizer {
    /**
     * This exists to make it easier for the {@link JsonParser} to process the games json format
     */
    public static String normalize(String jsonString) {
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
}
