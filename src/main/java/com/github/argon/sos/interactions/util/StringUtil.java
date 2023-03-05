package com.github.argon.sos.interactions.util;

public class StringUtil {
    public static String toScreamingSnakeCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        return text.replaceAll("\\B([A-Z])", "_$1").toUpperCase();
    }

    public static String unCapitalize(String text) {
        return Character.toLowerCase(text.charAt(0)) + text.substring(1);
    }
}
