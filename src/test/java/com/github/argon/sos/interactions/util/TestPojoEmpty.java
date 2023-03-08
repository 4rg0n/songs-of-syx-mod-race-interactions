package com.github.argon.sos.interactions.util;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TestPojoEmpty {
    private String stringValue;
    private BigDecimal bigDecimal;

    private Integer integerValue;
    private int primitiveIntegerValue;

    private Long longValue;

    private long primitiveLongValue;

    private Double doubleValue;
    private double primitiveDoubleValue;

    private Float floatValue;
    private float primitiveFloatValue;

    private Byte byteValue;
    private byte primitiveByteValue;

    private Short shortValue;
    private short primitiveShortValue;

    private Boolean booleanValue = false;
    private boolean primitiveBooleanValue = false;

    private TestEnum testEnum;

    private Integer[] integerArray;
    private int[] primitiveIntegerArray;
    private List<Integer> integerList;

    private Long[] longArray;
    private long[] primitiveLongArray;
    private List<Long> longList;

    private Short[] shortArray;;
    private short[] primitiveShortArray;
    private List<Short> shortList;

    private Byte[] byteArray;
    private byte[] primitiveByteArray;
    private List<Byte> byteList;

    private String[] stringArray;
    private List<String> stringList;

    private Double[] doubleArray;
    private double[] primitiveDoubleArray;
    private List<Double> doubleList;

    private BigDecimal[] bigDecimalArray;
    private List<BigDecimal> bigDecimalList;

    private Float[] floatArray;
    private float[] primitiveFloatArray;
    private List<Float> floatList;

    private TestEnum[] enumArray;
    private List<TestEnum> enumList;

    @Data
    public static class NestedPojo {
        private String stringValue;
        private BigDecimal bigDecimal;

        private Integer integerValue;
        private int primitiveIntegerValue;

        private Long longValue;

        private long primitiveLongValue;

        private Double doubleValue;
        private double primitiveDoubleValue;

        private Float floatValue;
        private float primitiveFloatValue;

        private Byte byteValue;
        private byte primitiveByteValue;

        private Short shortValue;
        private short primitiveShortValue;

        private Boolean booleanValue = false;
        private boolean primitiveBooleanValue = false;

        private TestEnum testEnum;

        private Integer[] integerArray;
        private int[] primitiveIntegerArray;
        private List<Integer> integerList;

        private Long[] longArray;
        private long[] primitiveLongArray;
        private List<Long> longList;

        private Short[] shortArray;;
        private short[] primitiveShortArray;
        private List<Short> shortList;

        private Byte[] byteArray;
        private byte[] primitiveByteArray;
        private List<Byte> byteList;

        private String[] stringArray;
        private List<String> stringList;

        private Double[] doubleArray;
        private double[] primitiveDoubleArray;
        private List<Double> doubleList;

        private BigDecimal[] bigDecimalArray;
        private List<BigDecimal> bigDecimalList;

        private Float[] floatArray;
        private float[] primitiveFloatArray;
        private List<Float> floatList;

        private TestEnum[] enumArray;
        private List<TestEnum> enumList;
    }

}
