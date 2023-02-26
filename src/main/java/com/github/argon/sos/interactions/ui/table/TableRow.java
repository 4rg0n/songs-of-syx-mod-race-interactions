package com.github.argon.sos.interactions.ui.table;

import lombok.Getter;
import snake2d.util.gui.GuiSection;
import util.data.GETTER;

@Getter
public class TableRow<T> extends GuiSection {
    private final GETTER<Integer> index;

    private final TableStore<T> store;

    public TableRow(GETTER<Integer> index, TableStore<T> store) {
        this.index = index;
        this.store = store;
    }

    /**
     * @return null when there's no index
     */
    public T getEntry() {
        if (index.get() == null) {
            return null;
        }

        return store
            .getEntries()
            .get(index.get());
    }
}
