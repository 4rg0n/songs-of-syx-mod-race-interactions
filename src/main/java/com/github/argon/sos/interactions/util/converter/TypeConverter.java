package com.github.argon.sos.interactions.util.converter;

import com.github.argon.sos.interactions.util.PrimitivesCastUtil;

public class TypeConverter {

    private final Converters converters;

    public TypeConverter() {
        this.converters = new Converters();
        converters.addConverter(Integer[].class, int[].class, PrimitivesCastUtil::toIntegerArray);
        converters.addConverter(Long[].class, int[].class, PrimitivesCastUtil::toIntegerArray);
        converters.addConverter(Short[].class, int[].class, PrimitivesCastUtil::toIntegerArray);
        converters.addConverter(Byte[].class, int[].class, PrimitivesCastUtil::toIntegerArray);
    }
}
