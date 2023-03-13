package com.github.argon.sos.interactions.game.json.parser;

import com.github.argon.sos.interactions.game.json.Json;
import com.github.argon.sos.interactions.game.json.element.JsonElement;

public abstract class Parser {
    public abstract JsonElement parse(Json json);
}
