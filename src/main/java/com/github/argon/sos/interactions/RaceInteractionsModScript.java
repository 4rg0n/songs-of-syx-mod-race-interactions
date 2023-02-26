package com.github.argon.sos.interactions;


import com.github.argon.sos.interactions.config.ConfigUtil;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.race.*;
import com.github.argon.sos.interactions.ui.race.section.ButtonSection;
import com.github.argon.sos.interactions.ui.race.section.ConfigSection;
import com.github.argon.sos.interactions.ui.race.RaceInteractionsConfigPanel;
import com.github.argon.sos.interactions.ui.race.section.RaceTableSection;
import com.github.argon.sos.interactions.ui.GameConfig;
import com.github.argon.sos.interactions.util.SCRIPT;
import lombok.NoArgsConstructor;
import util.info.INFO;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Entry point
 */
@NoArgsConstructor
public final class RaceInteractionsModScript implements SCRIPT {
	private final static Logger log = Loggers.getLogger(RaceInteractionsModScript.class);

	public final static INFO MOD_INFO = new INFO(
			"Race Interactions",
			"Manipulates the likings of races to each other based on their equality in preferences: " +
					Arrays.toString(RacePrefCategory.values()));

	@Override
	public CharSequence name() {
		return MOD_INFO.name;
	}

	@Override
	public CharSequence desc() {
		return MOD_INFO.desc;
	}

	@Override
	public void initBeforeGameCreated() {

	}

	@Override
	public void initGameRunning() {
	}

	@Override
	public void initGameLoaded() {
		Loggers.setLevels(Level.FINE);
		RaceService.initVanillaLikings();

		RaceInteractionsConfig config = ConfigUtil.loadProfileConfig()
				.orElseGet(ConfigUtil::loadModConfig);

		RaceService raceService = new RaceService(config.getGameRaces());
		RaceComparator raceComparator = new RaceComparator();
		RacePrefCalculator racePrefCalculator = new RacePrefCalculator();
		RaceInteractions raceInteractions = new RaceInteractions(
			raceComparator,
			racePrefCalculator,
			raceService
		);

		List<RaceInfo> allRaceInfo = raceService.getAllRaceInfo();
		int width = allRaceInfo.size() * 110;

		ConfigSection configSection = new ConfigSection(config);
		RaceTableSection overviewSection = new RaceTableSection(allRaceInfo, width);
		ButtonSection buttonSection = new ButtonSection();
		RaceInteractionsConfigPanel configPanel = new RaceInteractionsConfigPanel(
				raceInteractions,
				configSection,
				overviewSection,
				buttonSection,
				width
		);

		raceInteractions.manipulateRaceLikings(config);
		new GameConfig(configPanel).init();
	}

	@Override
	public SCRIPT_INSTANCE initAfterGameCreated() {
		return new Instance(this);
	}

}
