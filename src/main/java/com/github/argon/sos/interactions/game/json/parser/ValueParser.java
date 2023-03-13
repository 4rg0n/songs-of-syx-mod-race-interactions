package com.github.argon.sos.interactions.game.json.parser;

import com.github.argon.sos.interactions.game.json.Json;
import com.github.argon.sos.interactions.game.json.element.JsonElement;
import com.github.argon.sos.interactions.game.json.element.JsonDouble;
import com.github.argon.sos.interactions.game.json.element.JsonLong;
import com.github.argon.sos.interactions.game.json.element.JsonString;

public class ValueParser extends Parser {
    @Override
    public JsonElement parse(Json json) {
        String value = json.getNextValue();
        try {

            // decimal?
            if (value.contains(".") || value.contains(",")) {
                return new JsonDouble(Double.parseDouble(value));
            } else {
                return new JsonLong(Long.parseLong(value));
            }
        } catch (RuntimeException e) {
            // must be a string then?
            return new JsonString(value);
        }
    }
}
