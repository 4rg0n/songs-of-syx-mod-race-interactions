package com.github.argon.sos.interactions.ui.element.table;

import lombok.Getter;
import snake2d.util.gui.GuiSection;

import java.util.List;

/**
 * Represents a table with rows in the game ui.
 *
 * You can build a table with the {@link TableBuilder}.
 *
 * @param <R> table row class
 * @param <T> table entry class
 */
@Getter
public class TableSection<R extends TableRow<T>, T> {
    /**
     * Holds the table entries with data
     */
    private final TableStore<T> store;
    private final GuiSection section;
    private final List<R> rows;

    public TableSection(GuiSection section, TableStore<T> store, List<R> rows) {
        this.store = store;
        this.section = section;
        this.rows = rows;
    }
}
