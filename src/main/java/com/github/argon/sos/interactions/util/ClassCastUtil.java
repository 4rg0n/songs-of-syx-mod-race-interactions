package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;

import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;

public class ClassCastUtil {
    private final static Logger log = Loggers.getLogger(ClassCastUtil.class);

    public static boolean instanceOf(Object object, Class<?> clazz) {
        return instanceOf(object.getClass(), clazz);
    }

    public static boolean instanceOf(Object object, Object otherObject) {
        return instanceOf(object.getClass(), otherObject.getClass());
    }

    public static boolean instanceOf(Class<?> clazz, Class<?> otherClazz) {
        if (clazz.getCanonicalName().equals(otherClazz.getCanonicalName())) {
            return true;
        }

        if (otherClazz.isAssignableFrom(clazz)) {
            return true;
        }

        return false;
    }


    public static Object[] toArray(Object object) {
        return (Object[]) object;
    }

    public static String[] toStringArray(Object[] objects) {
        String[] strings = new String[objects.length];

        for (int i = objects.length - 1; i >= 0; i--) {
            Object object = objects[i];
            strings[i] = toString(object);
        }

        return strings;
    }

    public static String[] toStringArrayString(Collection<?> strings) {
        return strings.toArray(strings.toArray(new String[0]));
    }

    public static String[] toStringArrayEnum(Collection<?> enums) {
        return enums.stream()
            .map(object -> toString((Enum<?>) object))
            .toArray(String[]::new);
    }

    public static String toString(Enum<?> aEnum) {
        return aEnum.name();
    }

    public static int toInt(Short sShort) {
        return sShort.intValue();
    }

    public static int toInt(Long aLong) {
        log.info("Casting long %s to an int value. You may loose information here.", aLong);
        return aLong.intValue();
    }

    public static int toInt(Byte aByte) {
        return aByte.intValue();
    }

    public static String toString(Object object) {
        if (object instanceof Enum<?>) {
            return toString((Enum<?>) object);
        } else if (object instanceof String) {
            return (String) object;
        } else if (object instanceof Temporal) {
            Temporal temporal = (Temporal) object;
            return temporal.toString();
        } else {
            return object.toString();
        }
    }

    public static int[] toIntArray(long[] longs) {
        int[] ints = new int[longs.length];

        for (int i = 0, length = longs.length; i < length; i++) {
            ints[i] = (int) longs[i];
        }

        return ints;
    }

    public static int[] toIntArray(Long[] longs) {
        int[] ints = new int[longs.length];

        for (int i = 0, length = longs.length; i < length; i++) {
            ints[i] = toInt(longs[i]);
        }

        return ints;
    }

    public static int[] toIntArrayLong(Collection<?> longs) {
        int[] ints = new int[longs.size()];
        int i = 0;

        for (Object object : longs) {
            ints[i] = toInt((Long) object);
            i++;
        }

        return ints;
    }

    public static int[] toIntArray(byte[] bytes) {
        int[] ints = new int[bytes.length];

        for (int i = 0, length = bytes.length; i < length; i++) {
            ints[i] = bytes[i];
        }

        return ints;
    }

    public static int[] toIntArray(Byte[] bytes) {
        int[] ints = new int[bytes.length];

        for (int i = 0, length = bytes.length; i < length; i++) {
            ints[i] = bytes[i];
        }

        return ints;
    }

    public static int[] toIntArrayByte(Collection<?> bytes) {
        int[] ints = new int[bytes.size()];
        int i = 0;

        for (Object object : bytes) {
            Byte aByte = (Byte) object;
            ints[i] = aByte;
            i++;
        }

        return ints;
    }

    public static int[] toIntArray(short[] shorts) {
        int[] ints = new int[shorts.length];

        for (int i = 0, length = shorts.length; i < length; i++) {
            ints[i] = shorts[i];
        }

        return ints;
    }

    public static int[] toIntArray(Short[] shorts) {
        int[] ints = new int[shorts.length];

        for (int i = 0, length = shorts.length; i < length; i++) {
            ints[i] = shorts[i];
        }

        return ints;
    }

    public static int[] toIntArrayShort(Collection<?> shorts) {
        int[] ints = new int[shorts.size()];
        int i = 0;

        for (Object object : shorts) {
            Short aShort = (Short) object;
            ints[i] = aShort;
            i++;
        }

        return ints;
    }

    public static int[] toIntArray(Integer[] integers) {
        return Arrays.stream(integers)
            .mapToInt(Integer::intValue)
            .toArray();
    }

    public static int[] toIntArrayInteger(Collection<?> integers) {
        int[] ints = new int[integers.size()];
        int i = 0;

        for (Object object : integers) {
            Integer integer = (Integer) object;
            ints[i] = integer;
            i++;
        }

        return ints;
    }

    public static double[] toDoubleArray(float[] floats) {
        double[] doubles = new double[floats.length];

        for (int i = 0, length = floats.length; i < length; i++) {
            doubles[i] = floats[i];
        }

        return doubles;
    }

    public static double[] toDoubleArray(Float[] floats) {
        double[] doubles = new double[floats.length];

        for (int i = 0, length = floats.length; i < length; i++) {
            doubles[i] = toDouble(floats[i]);
        }

        return doubles;
    }

    public static double[] toDoubleArray(BigDecimal[] bigDecimals) {
        double[] doubles = new double[bigDecimals.length];

        for (int i = 0, length = bigDecimals.length; i < length; i++) {
            doubles[i] = toDouble(bigDecimals[i]);
        }

        return doubles;
    }

    public static double[] toDoubleArrayBigDecimal(Collection<?> bigDecimals) {
        double[] doubles = new double[bigDecimals.size()];
        int i = 0;

        for (Object object : bigDecimals) {
            doubles[i] = toDouble((BigDecimal) object);
            i++;
        }

        return doubles;
    }

    public static double[] toDoubleArrayFloat(Collection<?> floats) {
        double[] doubles = new double[floats.size()];
        int i = 0;

        for (Object object : floats) {
            Float aFloat = (Float) object;
            doubles[i] = aFloat;
            i++;
        }

        return doubles;
    }

    public static double[] toDoubleArray(Double[] doubles) {
        return Arrays.stream(doubles)
            .mapToDouble(Double::doubleValue)
            .toArray();
    }

    public static double[] toDoubleArrayDouble(Collection<?> doublesCol) {
        double[] doubles = new double[doublesCol.size()];
        int i = 0;

        for (Object object : doublesCol) {
            Double aDouble = (Double) object;
            doubles[i] = aDouble;
            i++;
        }

        return doubles;
    }

    public static double toDouble(Float aFloat) {
        return aFloat.doubleValue();
    }

    public static double toDouble(BigDecimal bigDecimal) {
        return bigDecimal.doubleValue();
    }

    public static Object box(Object object) {
        return object;
    }

    public static boolean isArray(Object object) {
        return object.getClass().isArray();
    }
}
