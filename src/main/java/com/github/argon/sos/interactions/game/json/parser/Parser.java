package com.github.argon.sos.interactions.game.json.parser;

import com.github.argon.sos.interactions.game.json.Json;
import com.github.argon.sos.interactions.game.json.element.JsonElement;

/**
 * Common class for parsers parsing json text
 */
public abstract class Parser {
    public abstract JsonElement parse(Json json);
}
