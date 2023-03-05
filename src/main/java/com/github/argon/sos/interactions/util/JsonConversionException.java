package com.github.argon.sos.interactions.util;

public class JsonConversionException extends RuntimeException {
    public JsonConversionException() {
    }

    public JsonConversionException(String message) {
        super(message);
    }

    public JsonConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonConversionException(Throwable cause) {
        super(cause);
    }
}
