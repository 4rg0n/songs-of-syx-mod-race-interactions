package com.github.argon.sos.interactions.ui.element.table;

import lombok.Getter;

import java.util.List;

/**
 * Holds the list of table entries with data
 *
 * @param <T> table entry
 */
public class TableStore<T> {
    @Getter
    private final List<T> entries;

    public TableStore(List<T> entries) {
        this.entries = entries;
    }
}
