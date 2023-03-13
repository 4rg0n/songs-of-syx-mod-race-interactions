package com.github.argon.sos.interactions.game.json.parser;

import com.github.argon.sos.interactions.game.json.Json;
import com.github.argon.sos.interactions.game.json.element.JsonBoolean;
import com.github.argon.sos.interactions.game.json.element.JsonElement;

public class BooleanParser extends Parser {
    @Override
    public JsonElement parse(Json json) {
        String value = json.getNextValue();

        switch (value) {
            case "true":
                return new JsonBoolean(true);
            case "false":
                return new JsonBoolean(false);
            default:
                throw new JsonParseException("Could not parse number value from '" + value + "' at position " + json.getIndex());
        }
    }
}
