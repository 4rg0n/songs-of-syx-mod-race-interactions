package com.github.argon.sos.interactions.util;

import util.data.INT;

/**
 * Acts as a wrapper for {@link Integer} values.
 */
public class SimpleINTE implements INT.INTE {
    private int value = 0;

    private final int max;

    private final int min;

    public SimpleINTE(int value, int min, int max) {
        this.value = value;
        this.max = max;
        this.min = min;
    }

    public SimpleINTE(int min, int max) {
        this.max = max;
        this.min = min;
    }

    @Override
    public int min() {
        return min;
    }

    @Override
    public int max() {
        return max;
    }

    @Override
    public int get() {
        return value;
    }

    @Override
    public void set(int value) {
        this.value = value;
    }
}
