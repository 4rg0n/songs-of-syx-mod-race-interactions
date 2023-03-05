package com.github.argon.sos.interactions.util;

import lombok.Data;
import org.assertj.core.util.Lists;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class TestPojo {
    private String stringValue = "String";

    private Integer integerValue = 1;
    private int primitiveIntegerValue = 2;

    private Long longValue = 1L;

    private long primitiveLongValue = 2L;

    private Double doubleValue = 1.1D;
    private double primitiveDoubleValue = 2.2d;

    private Float floatValue = 1.1F;
    private float primitiveFloatValue = 2.2f;

    private Byte byteValue = 0b0001;
    private byte primitiveByteValue = 0b0010;

    private Short shortValue = 1;
    private short primitiveShortValue = 2;

    private Boolean booleanValue = false;
    private boolean primitiveBooleanValue = false;

    private TestEnum testEnum = TestEnum.TEST1;

    private LocalDateTime dateTime = LocalDateTime.now();

    private Integer[] integerArray = new Integer[]{1, 2, 3};
    private int[] primitiveIntegerArray = new int[]{4, 5, 6};
    private List<Integer> integerList = Lists.list(integerArray);

    private Long[] longArray = new Long[]{1L, 2L, 3L};
    private long[] primitiveLongArray = new long[]{4L, 5L, 6L};
    private List<Long> longList = Lists.list(longArray);

    private Short[] shortArray = new Short[]{1, 2, 3};
    private short[] primitiveShortArray = new short[]{4, 5, 6};
    private List<Short> shortList = Lists.list(shortArray);

    private Byte[] byteArray = new Byte[]{0b0001, 0b0010, 0b0100};
    private byte[] primitiveByteArray = new byte[]{0b1000, 0b0011, 0b0101};
    private List<Byte> byteList = Lists.list(byteArray);

    private String[] stringArray = new String[]{"test1", "test2", "test3"};
    private List<String> stringList = Lists.list(stringArray);

    private Double[] doubleArray = new Double[]{1.1D, 2.2D, 3.3D};
    private double[] primitiveDoubleArray = new double[]{4.4d, 5.5d, 6.6d};
    private List<Double> doubleList = Lists.list(doubleArray);

    private Float[] floatArray = new Float[]{1.1F, 2.2F, 3.3F};
    private float[] primitiveFloatArray = new float[]{4.4f, 5.5f, 6.6f};
    private List<Float> floatList = Lists.list(floatArray);

    private TestEnum[] enumArray = new TestEnum[]{TestEnum.TEST1, TestEnum.TEST2, TestEnum.TEST3};
    private List<TestEnum> enumList = Lists.list(enumArray);

    private TestNestedPojo nestedPojo = new TestNestedPojo();
    private TestNestedPojo[] nestedPojoArray = new TestNestedPojo[]{new TestNestedPojo(), new TestNestedPojo(), new TestNestedPojo()};
    private List<TestNestedPojo> nestedPojoList = Lists.list(nestedPojoArray);

    private Map<String, Integer> mapStringInteger = MapUtil.of(
        "Test1", 1,
        "Test2", 2,
        "Test3", 3
    );

    private Map<TestEnum, Double> mapEnumDouble = MapUtil.of(
        TestEnum.TEST1, 1.1d,
        TestEnum.TEST2, 2.2d,
        TestEnum.TEST2, 3.3d
    );

    private Map<String, TestNestedPojo> mapStringNestedPojo = MapUtil.of(
        "Pojo1", new TestNestedPojo(),
        "Pojo2", new TestNestedPojo(),
        "Pojo3", new TestNestedPojo()
    );

    @Data
    public static class TestNestedPojo {
        private String stringValue = "String";

        private Integer integerValue = 1;
        private int primitiveIntegerValue = 2;

        private Long longValue = 1L;

        private long primitiveLongValue = 2L;

        private Double doubleValue = 1.1D;
        private double primitiveDoubleValue = 2.2d;

        private Float floatValue = 1.1F;
        private float primitiveFloatValue = 2.2f;

        private Byte byteValue = 0b0001;
        private byte primitiveByteValue = 0b0010;

        private Short shortValue = 1;
        private short primitiveShortValue = 2;

        private Boolean booleanValue = false;
        private boolean primitiveBooleanValue = false;

        private TestEnum testEnum = TestEnum.TEST1;

        private LocalDateTime dateTime = LocalDateTime.now();

        private Integer[] integerArray = new Integer[]{1, 2, 3};
        private int[] primitiveIntegerArray = new int[]{4, 5, 6};
        private List<Integer> integerList = Lists.list(integerArray);

        private Long[] longArray = new Long[]{1L, 2L, 3L};
        private long[] primitiveLongArray = new long[]{4L, 5L, 6L};
        private List<Long> longList = Lists.list(longArray);

        private Byte[] byteArray = new Byte[]{0b0001, 0b0010, 0b0100};
        private byte[] primitiveByteArray = new byte[]{0b1000, 0b0011, 0b0101};
        private List<Byte> byteList = Lists.list(byteArray);

        private String[] stringArray = new String[]{"test1", "test2", "test3"};
        private List<String> stringList = Lists.list(stringArray);

        private Double[] doubleArray = new Double[]{1.1D, 2.2D, 3.3D};
        private double[] primitiveDoubleArray = new double[]{4.4d, 5.5d, 6.6d};
        private List<Double> doubleList = Lists.list(doubleArray);

        private Float[] floatArray = new Float[]{1.1F, 2.2F, 3.3F};
        private float[] primitiveFloatArray = new float[]{4.4f, 5.5f, 6.6f};
        private List<Float> floatList = Lists.list(floatArray);

        private TestEnum[] enumArray = new TestEnum[]{TestEnum.TEST1, TestEnum.TEST2, TestEnum.TEST3};
        private List<TestEnum> enumList = Lists.list(enumArray);
    }

    public enum TestEnum {
        TEST1, TEST2, TEST3
    }
}
