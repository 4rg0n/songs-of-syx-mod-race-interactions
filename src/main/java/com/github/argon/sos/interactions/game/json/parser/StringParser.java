package com.github.argon.sos.interactions.game.json.parser;

import com.github.argon.sos.interactions.game.json.Json;
import com.github.argon.sos.interactions.game.json.element.JsonElement;
import com.github.argon.sos.interactions.game.json.element.JsonString;

public class StringParser extends Parser {
    @Override
    public JsonElement parse(Json json) {
        // skip "
        json.indexMove();
        String value = json.getNextValue('\"');
        // skip "
        json.indexMove();
        return new JsonString(value);
    }
}
