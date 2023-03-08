package com.github.argon.sos.interactions.util.converter;

import com.github.argon.sos.interactions.util.ClassUtil;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Converters {
    private final Map<ConversionType<?, ?>, Converter<?, ?>> converters = new HashMap<>();

    public <T, R> Converter<T, R> addConverter(Class<T> fromClass, Class<R> toClass, Converter<T, R> converter) {
        ConversionType<T, R> conversionType = new ConversionType<>(fromClass, toClass);
        return addConverter(conversionType, converter);
    }

    public <T, R> Converter<T, R> addConverter(ConversionType<T, R>  conversionType, Converter<T, R>  converter) {
        return (Converter<T, R>) converters.put(conversionType, converter);
    }

    public <T, R> Optional<Converter<T, R>> getConverter(Class<T> fromClass, Class<R> toClass) {
        return converters.entrySet().stream().filter(converterEntry -> {
            ConversionType<?, ?> conversionType = converterEntry.getKey();
            return ClassUtil.sameAs(conversionType.getFromType(), fromClass)
                && ClassUtil.sameAs(conversionType.getToType(), toClass);
        })
        .findFirst()
        .map(converterEntry -> (Converter<T, R>) converterEntry.getValue());
    }

    @Getter
    public static class ConversionType<T, R> {
        private final Class<T> fromClass;
        private final Class<R> toClass;

        public ConversionType(Class<T> fromClass, Class<R> toClass) {
            this.fromClass = fromClass;
            this.toClass = toClass;
        }

        public Type getFromType() {
            return fromClass;
        }

        public Type getToType() {
            return toClass;
        }
    }
}
