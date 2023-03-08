package com.github.argon.sos.interactions.util.converter;

public interface Converter<T, R> {
    R convert(T object);
}
