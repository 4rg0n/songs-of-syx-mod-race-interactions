package com.github.argon.sos.interactions.game.json;

import com.github.argon.sos.interactions.game.json.element.JsonElement;
import com.github.argon.sos.interactions.game.json.element.JsonObject;
import com.github.argon.sos.interactions.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

public class JsonWriter {

    private int printDepth;
    @Getter
    @Setter
    private boolean quoteKeys = false;
    @Getter
    @Setter
    private boolean prettyPrint = true;

    public JsonWriter() {
    }

    public JsonWriter(boolean quoteKeys, boolean prettyPrint) {
        this.quoteKeys = quoteKeys;
        this.prettyPrint = prettyPrint;
    }

    public String write(JsonElement json) {
        String jsonString = toJson(json);
        printDepth = 0;
        return jsonString;
    }

    private String toJson(JsonElement json) {
        if (json instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) json;
            if (prettyPrint) {
                return prettyPrint(jsonObject);
            }

            return jsonObject.toJson(quoteKeys);
        } else {
            return json.toString();
        }
    }

    private String prettyPrint(JsonObject jsonObject) {
        printDepth += 2;
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");

        boolean isFirst = true;
        for (String key : jsonObject.getMap().keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",\n");
            }
            String indent = StringUtil.repeat(' ', Math.max(0, printDepth));
            sb.append(indent);

            if (quoteKeys) {
                sb.append("\"").append(key).append("\"").append(": ");
            } else {
                sb.append(key).append(": ");
            }

            JsonElement value = jsonObject.getMap().get(key);
            sb.append(toJson(value));
        }
        sb.append('\n');
        String indent = StringUtil.repeat(' ', Math.max(0, printDepth - 2));
        sb.append(indent);
        sb.append("}");

        printDepth -= 2;
        return sb.toString();
    }

}
