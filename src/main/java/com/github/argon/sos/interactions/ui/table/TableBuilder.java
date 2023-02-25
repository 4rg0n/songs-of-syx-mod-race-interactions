package com.github.argon.sos.interactions.ui.table;

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

public class TableBuilder {

    private final static Logger log = Loggers.getLogger(TableBuilder.class);

    public static <T> TableSection<T> build(
        List<T> entries,
        BiFunction<GETTER<Integer>,
        TableStore<T> , ? extends TableRow<T>> rowBuilder,
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

        List<TableRow<T>> rows = new ArrayList<>();
        tableBuilder.column(null, width, new GTableBuilder.GRowBuilder() {
            @Override
            public RENDEROBJ build(GETTER<Integer> ier) {
                return rowBuilder.apply(ier, tableStore);
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
