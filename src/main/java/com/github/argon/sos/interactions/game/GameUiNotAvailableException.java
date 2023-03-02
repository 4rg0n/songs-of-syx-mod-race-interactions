package com.github.argon.sos.interactions.game;

public class GameUiNotAvailableException extends RuntimeException {
    public GameUiNotAvailableException() {
    }

    public GameUiNotAvailableException(String message) {
        super(message);
    }

    public GameUiNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public GameUiNotAvailableException(Throwable cause) {
        super(cause);
    }
}
