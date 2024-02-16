package com.github.argon.sos.interactions;


import com.github.argon.sos.interactions.ai.AIModule_Race;
import com.github.argon.sos.interactions.config.ConfigStore;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RacePrefCategory;
import com.github.argon.sos.interactions.config.RaceStandingCategory;
import com.github.argon.sos.interactions.game.SCRIPT;
import com.github.argon.sos.interactions.game.api.GameAiApi;
import com.github.argon.sos.interactions.game.api.GameRaceApi;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.race.RaceService;
import com.github.argon.sos.interactions.ui.UIGameConfig;
import com.github.argon.sos.interactions.ui.race.RaceInteractionsConfigPanel;
import lombok.NoArgsConstructor;
import settlement.entity.humanoid.HTYPE;
import util.info.INFO;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Entry point
 */
@NoArgsConstructor
public final class RaceInteractionsModScript implements SCRIPT<RaceInteractionsConfig> {
	private final static Logger log = Loggers.getLogger(RaceInteractionsModScript.class);

	public final static INFO MOD_INFO = new INFO(
			"Race Interactions",
			"Manipulates the likings of races to each other based on their equality in preferences: " +
				Arrays.toString(RacePrefCategory.values()) + ". " +
				"Races nearby other liked races can get boosted standings: " +
				Arrays.toString(RaceStandingCategory.values()));

	private RaceInteractions raceInteractions;

	private AIModule_Race aiModuleRace;

	private RaceInteractionsConfigPanel configPanel;

	private ConfigStore configStore;

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
		Loggers.setLevels(Level.FINER);
//		Loggers.setLevels("com.github.argon.sos.interactions.race", Level.INFO);
	}

	@Override
	public void initGameRunning() {
		// store vanilla likings for possible reset via button click
		GameRaceApi.getInstance().initVanillaLikings();
	}

	@Override
	public void initGameSaveLoaded(RaceInteractionsConfig config) {
		log.debug("Apply configuration loaded from save");
		log.trace("CONFIG: %s", config);

		// no config from save file?
		if (config == null) {
			return;
		}

		configStore.setCurrentConfig(config);

		// adjust likings when game loaded from save
		raceInteractions.manipulateRaceLikings(config);
		configPanel.applyConfig(config);
	}

	@Override
	public void initGamePresent() {
		// inject mod ai into game ai
		GameAiApi.injectAIModule(aiModuleRace,
			HTYPE.SUBJECT,
			HTYPE.RETIREE,
			HTYPE.RECRUIT,
			HTYPE.STUDENT,
			HTYPE.NOBILITY,
			HTYPE.CHILD
		);

		// inject mod ui into game ui
		UIGameConfig uiGameConfig = new UIGameConfig(configPanel);
		uiGameConfig.init();
	}

	@Override
	public SCRIPT_INSTANCE createInstance() {
		configStore = ConfigStore.getInstance();
		RaceInteractionsConfig config = configStore.loadJsonOrDefault();
		configStore.setCurrentConfig(config);

		// "business logic"
		RaceService raceService = new RaceService(config.getVanillaRaces());
		raceInteractions = RaceInteractions.Builder.build(raceService);

		// Race InteractionsAI
		aiModuleRace = RaceInteractions.Builder.buildAI(raceInteractions);

		// Config UI Panel with all it's buttons and stuff
		configPanel = RaceInteractions.Builder.buildConfigUI(
			config,
			raceInteractions,
			raceService.getAllRaceInfo()
		);

		// adjust likings when game created
		raceInteractions.manipulateRaceLikings(config);
		configPanel.applyConfig(config);

		return new Instance(this, configStore);
	}
}
