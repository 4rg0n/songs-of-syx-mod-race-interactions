package com.github.argon.sos.interactions.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ReflectionUtilTest {

    @Test
    void invokeEnumArray() throws NoSuchMethodException {
        TestPojoEmpty testPojoEmpty = new TestPojoEmpty();

        Method method = testPojoEmpty.getClass().getDeclaredMethod("setEnumArray", TestEnum[].class);



        Enum<?> anEnum = ClassCastUtil.toEnum("TEST1", TestEnum.class);
        TestEnum[] anEnums = new TestEnum[]{(TestEnum) anEnum};
        ReflectionUtil.invokeMethodOneArgument(method, testPojoEmpty, anEnums);

        Enum<?>[] enums = ClassCastUtil.toEnumArray(new String[]{"TEST1"}, TestEnum.class);
        ReflectionUtil.invokeMethodOneArgument(method, testPojoEmpty, enums);

        return;
    }
}