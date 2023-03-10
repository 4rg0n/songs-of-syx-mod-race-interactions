package com.github.argon.sos.interactions.ui.element.table;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import snake2d.Errors;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.data.GETTER;
import util.gui.table.GTableBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * For building a table with single column rows.
 */
public class TableBuilder {

    private final static Logger log = Loggers.getLogger(TableBuilder.class);

    /**
     * Builds a table for usage in the games ui
     *
     * @param entries list of entries to fill the table with. Can be empty and filled later.
     * @param rowBuilder function to instantiate your row
     * @param width of the whole table. Note that a wider row will extend the tables width
     * @param rowsToShow how many rows the table should display
     *
     * @param <R> table row
     * @param <T> table storage entry
     */
    public static <R extends TableRow<T>, T> TableSection<R, T> build(
        List<T> entries,
        BiFunction<GETTER<Integer>, TableStore<T>, R> rowBuilder,
        int width,
        int rowsToShow
    ) {
        TableStore<T> tableStore = new TableStore<>(entries);
        GTableBuilder tableBuilder = new GTableBuilder() {
            @Override
            public int nrOFEntries() {
                return tableStore.getEntries().size();
            }
        };

        List<R> rows = new ArrayList<>();
        tableBuilder.column(null, width, new GTableBuilder.GRowBuilder() {
            @Override
            public RENDEROBJ build(GETTER<Integer> ier) {
                R row = rowBuilder.apply(ier, tableStore);
                rows.add(row);
                return row;
            }
        });

        GuiSection tableSection;

        try {
            tableSection = tableBuilder.create(rowsToShow, false);
        } catch (Errors.DataError e) {
            log.error("You have initialized a table without width or height: %s", e.getMessage());
            throw e;
        }

        return new TableSection<>(tableSection, tableStore, rows);
    }
}
