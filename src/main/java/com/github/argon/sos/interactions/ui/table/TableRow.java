package com.github.argon.sos.interactions.ui.table;

import lombok.Getter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.data.GETTER;

@Getter
public class TableRow<T> extends GuiSection {
    private final GETTER<Integer> index;

    private final TableStore<T> store;

    private COLOR color;

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

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        if (color != null) {
            color.render(r, body(), 0);
        }
        super.render(r, ds);
    }

    public TableRow<T> background(COLOR c) {
        this.color = c;
        return this;
    }
}
