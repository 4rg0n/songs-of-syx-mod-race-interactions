package com.github.argon.sos.interactions.ui.table;

import lombok.Getter;
import snake2d.util.gui.GuiSection;

import java.util.List;

@Getter
public class TableSection<T> {
    private final TableStore<T> store;
    private final GuiSection section;
    private final List<TableRow<T>> rows;

    public TableSection(GuiSection section, TableStore<T> store, List<TableRow<T>> rows) {
        this.store = store;
        this.section = section;
        this.rows = rows;
    }
}
