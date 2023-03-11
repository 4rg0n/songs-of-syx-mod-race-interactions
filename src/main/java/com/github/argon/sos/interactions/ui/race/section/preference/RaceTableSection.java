package com.github.argon.sos.interactions.ui.race.section.preference;

import com.github.argon.sos.interactions.race.RaceInfo;
import com.github.argon.sos.interactions.ui.element.table.TableBuilder;
import com.github.argon.sos.interactions.ui.element.table.TableSection;
import lombok.Getter;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.GuiSection;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains the overview about the races and their likings to others
 */
@Getter
public class RaceTableSection extends GuiSection {

    private final TableSection<RaceTableRow, RaceInfo> tableSection;

    public RaceTableSection(List<RaceInfo> raceInfo, int width) {
        this.tableSection = TableBuilder.build(raceInfo, RaceTableRow::new, width, 7);
        body().setWidth(width);

        addRelBody(5, DIR.S, tableSection.getSection());
    }

    public Map<String, List<String>> getRaceBoostingToggles() {
        Map<String, List<String>> raceBoostingToggles = new HashMap<>();

        getTableSection().getRows().forEach(raceTableRow -> {
            raceTableRow.getEntry().ifPresent(raceInfo -> {
                List<String> enabledRaces = raceTableRow.getRaceBoostCheckboxes().entrySet().stream()
                    .map(entry -> {
                        if (entry.getValue().selectedIs()) {
                            return entry.getKey();
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

                String raceName = raceInfo.getRace().key;
                raceBoostingToggles.put(raceName, enabledRaces);
            });
        });

        return raceBoostingToggles;
    }

    public void toggleAllRaceBoostings(boolean enabled) {
        getTableSection().getRows().forEach(raceTableRow -> {
            raceTableRow.getEntry().ifPresent(raceInfo -> {
               raceTableRow.getRaceBoostCheckboxes()
                   .forEach((key, value) -> value.selectedSet(enabled));
            });
        });
    }

    public void apply(Map<String, List<String>> raceBoostingToggles) {
        raceBoostingToggles.forEach((raceName, enabledRaces)->
            getRaceRow(raceName)
                .ifPresent(raceTableRow ->
                    raceTableRow.getRaceBoostCheckboxes().forEach((raceKey, checkbox) -> {
                        checkbox.selectedSet(enabledRaces.contains(raceKey));
                    })
                ));
    }

    public Optional<RaceTableRow> getRaceRow(String raceName) {
        return getTableSection().getRows().stream()
            .filter(raceTableRow -> raceTableRow.getEntry()
                .map(raceInfo -> raceInfo.getRace().key.equals(raceName))
                .orElse(false))
            .findFirst();
    }
}
