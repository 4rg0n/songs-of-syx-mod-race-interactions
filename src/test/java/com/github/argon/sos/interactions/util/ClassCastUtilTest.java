package com.github.argon.sos.interactions.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ClassCastUtilTest {

    @Test
    void instanceOf() {
        String testString = "test";
        Assertions.assertThat(ClassCastUtil.instanceOf(testString, String.class)).isTrue();

        List<String> testStringList = new ArrayList<>();
        testStringList.add("test");
        Assertions.assertThat(ClassCastUtil.instanceOf(testStringList, ArrayList.class)).isTrue();
        Assertions.assertThat(ClassCastUtil.instanceOf(testStringList, List.class)).isTrue();
        Assertions.assertThat(ClassCastUtil.instanceOf(testStringList, Iterable.class)).isTrue();
        Assertions.assertThat(ClassCastUtil.instanceOf(testStringList, Collection.class)).isTrue();

        int testIntPrimitive = 1;
        Assertions.assertThat(ClassCastUtil.instanceOf(testIntPrimitive, Integer.class)).isTrue();
    }
}