package com.github.argon.sos.interactions.config.mapper;

public class ConfigMapperException extends RuntimeException {
    public ConfigMapperException() {
    }

    public ConfigMapperException(String message) {
        super(message);
    }

    public ConfigMapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigMapperException(Throwable cause) {
        super(cause);
    }
}
