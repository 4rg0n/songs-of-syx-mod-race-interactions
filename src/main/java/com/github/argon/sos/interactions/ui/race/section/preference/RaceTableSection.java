package com.github.argon.sos.interactions.ui.race.section.preference;

import com.github.argon.sos.interactions.race.RaceInfo;
import com.github.argon.sos.interactions.ui.table.TableBuilder;
import com.github.argon.sos.interactions.ui.table.TableSection;
import lombok.Getter;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;

import java.util.List;

/**
 * Contains the overview about the races and their likings to others
 */
@Getter
public class RaceTableSection extends GuiSection {

    private final TableSection<RaceInfo> tableSection;

    public RaceTableSection(List<RaceInfo> raceInfo, int width) {
        this.tableSection = TableBuilder.build(raceInfo, RaceTableRow::new, width, 8);
        body().setWidth(width);

        addRelBody(5, DIR.S, tableSection.getSection());
    }
}
