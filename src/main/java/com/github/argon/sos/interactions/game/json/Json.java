package com.github.argon.sos.interactions.game.json;

import com.github.argon.sos.interactions.game.json.element.JsonObject;
import lombok.Getter;

/**
 * Can process the games json format and produce valid json.
 * Controls parsing of the games json format.
 *
 * <pre>
 *     // standard JSON
 *     String jsonString = "{\"TEST\": 1}";
 *     JsonObject jsonObject = new Json(jsonString).getRoot();
 *
 *     // game JSON
 *     String jsonString = "TEST: 1,";
 *     JsonObject jsonObject = new Json(jsonString).getRoot();
 * </pre>
 */
public class Json {
    private final String rawJson;

    @Getter
    private int index;

    @Getter
    private final JsonObject root;

    @Getter
    private final JsonWriter writer;

    public Json(JsonObject root) {
        this(root, new JsonWriter());
    }

    public Json(JsonObject root, JsonWriter writer) {
        this.root = root;
        this.writer = writer;
        this.rawJson = "";
    }

    public Json(String rawJson) {
        this(rawJson, new JsonWriter());
    }

    public Json(String rawJson, JsonWriter writer) {
        this.rawJson = JsonStringNormalizer.normalize(rawJson.trim());
        this.writer = writer;
        this.index = 0;
        this.root = (JsonObject) JsonParser.parse(this);
    }

    public char currentChar(){
        return rawJson.charAt(index);
    }

    public void indexMove(){
        this.index++;
    }

    public void skipBlank(){
        while (!atEnd() && isWhiteSpace(currentChar())){
            indexMove();
        }
    }

    public String getNextValue() {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = currentChar();
            if (isEndOfValue(c))
                break;
            sb.append(c);
            indexMove();
        }
        return sb.toString();
    }

    public String getNextValue(char lookChar) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = currentChar();
            if (c == lookChar)
                break;
            sb.append(c);
            indexMove();
        }
        return sb.toString();
    }

    public boolean atEnd(){
        return getIndex() == rawJson.length();
    }

    private boolean isEndOfValue(char c) {
        return isWhiteSpace(c) || c == ',' || c == '}' || c == ']';
    }

    private boolean isWhiteSpace(char c) {
        return (c == ' '
            || c=='\n'
            || c=='\t'
            || c=='\f'
            || c=='\r'
        );
    }

    @Override
    public String toString() {
        return writer.write(this.getRoot());
    }
}
