package com.github.argon.sos.interactions.util.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class HashMapConverter {


    public Map<String, Object> parseJackson(String content) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> parseGson(String content) {
        try {
            Gson gson = new GsonBuilder()
                .setLenient()
                .create();
            Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
            return gson.fromJson(content, mapType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String normalizeGameJsonRegex(String content) {
        // remove comments
        content = content.replaceAll(".*\\*\\*.*", "");

        // quote everything
        Pattern pattern = Pattern.compile("(?<value>[\\w.\\-*]+)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        content = matcher.replaceAll("\"${value}\"");

        // FIX: remove astrix
        content = content.replaceAll("\"\\*\"", "\"ASTRIX_CHARACTER\"");

        // normalize true and false
        content = content.replaceAll("\"true\"", "true");
        content = content.replaceAll("\"false\"", "false");

        // normalize numbers
        pattern = Pattern.compile("\"(?<value>\"?\\d*\\.?\\d*?\"?)\"", Pattern.DOTALL);
        matcher = pattern.matcher(content);
        content = matcher.replaceAll("${value}");

        // remove trailing commas
        content = content.replaceAll("\\s*,\\s*}", "}");
        content = content.replaceAll("\\s*,\\s*]", "]");

        // FIX: remove "-",
        content = content.replaceAll("\"-\"(,)?", "");

        // FIX: keys in arrays
        pattern = Pattern.compile("\\[\\s*(?<content>\"\\w*\":\\s+[\\w,\\s\":]*)]", Pattern.DOTALL);
        matcher = pattern.matcher(content);
        content = matcher.replaceAll("{${content}}");

        // remove possible double quotes
        content = content.replaceAll("\"\"", "\"");
        // wrap in {}
        content = "{" + content + "}";
        // remove trailing commas again
        content = content.replaceAll(",\\s*}", "}");
        content = content.replaceAll(",\\s*]", "]");

        return content;
    }
}
