package com.github.argon.sos.interactions.game.json.mapper;

public class JsonMapperException extends RuntimeException {
    public JsonMapperException() {
    }

    public JsonMapperException(String message) {
        super(message);
    }

    public JsonMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonMapperException(Throwable cause) {
        super(cause);
    }
}
