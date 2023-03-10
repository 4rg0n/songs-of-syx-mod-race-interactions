package com.github.argon.sos.interactions.ui.element.table;

import lombok.Getter;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.gui.GuiSection;
import util.data.GETTER;

import java.util.Optional;

/**
 * Represents a row in a {@link TableSection}
 *
 * @param <T> table entry
 */
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
     * @return null when there's no index or entry
     */
    public Optional<T> getEntry() {
        if (index.get() == null) {
            return Optional.empty();
        }

        return Optional.of(store
            .getEntries()
            .get(index.get()));
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        if (color != null) {
            color.render(r, body(), 0);
        }
        super.render(r, ds);
    }

    /**
     * Change color of the rows background
     *
     * @param color of the background
     */
    public TableRow<T> background(COLOR color) {
        this.color = color;
        return this;
    }
}
