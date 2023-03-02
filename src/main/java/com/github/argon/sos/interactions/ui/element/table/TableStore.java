package com.github.argon.sos.interactions.ui.element.table;

import lombok.Getter;

import java.util.List;

public class TableStore<T> {
    @Getter
    private final List<T> entries;

    public TableStore(List<T> entries) {
        this.entries = entries;
    }
}
