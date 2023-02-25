package com.github.argon.sos.interactions.util;

public class UINotAvailableException extends RuntimeException {
    public UINotAvailableException() {
    }

    public UINotAvailableException(String message) {
        super(message);
    }

    public UINotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public UINotAvailableException(Throwable cause) {
        super(cause);
    }
}
