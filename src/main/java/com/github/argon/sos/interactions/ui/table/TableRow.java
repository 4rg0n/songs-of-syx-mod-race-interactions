package com.github.argon.sos.interactions.ui.table;

import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.GETTER;

@Getter
public class TableRow<T> extends GuiSection {
    private final GETTER<Integer> ier;

    private final TableStore<T> store;

    public TableRow(GETTER<Integer> ier, TableStore<T> store) {
        this.ier = ier;
        this.store = store;
    }
    public T getEntry() {
        return store.getEntries().get(ier.get());
    }
}
