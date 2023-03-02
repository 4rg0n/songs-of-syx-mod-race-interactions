package com.github.argon.sos.interactions;


import com.github.argon.sos.interactions.ai.AIModule_Race;
import com.github.argon.sos.interactions.config.ConfigStore;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RacePrefCategory;
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
					Arrays.toString(RacePrefCategory.values()));

	private RaceService raceService;
	private RaceInteractions raceInteractions;

	private AIModule_Race aiModuleRace;

	RaceInteractionsConfigPanel configPanel;


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
		Loggers.setLevels("com.github.argon.sos.interactions.race", Level.INFO);
	}

	@Override
	public void initGameRunning() {
		// store vanilla likings for reset
		GameRaceApi.getInstance().initVanillaLikings();
	}

	@Override
	public void initGameLoaded(RaceInteractionsConfig config) {
		log.debug("Initializing game resources and mod config");

		// "business logic"
		raceService = new RaceService(config.getGameRaces());
		raceInteractions = RaceInteractions.Builder.build(raceService);

		// Race InteractionsAI
		aiModuleRace = RaceInteractions.Builder.buildAI(raceInteractions);

		// Config UI Panel
		configPanel = RaceInteractions.Builder.buildConfigUI(
			config,
			raceInteractions,
			raceService.getAllRaceInfo()
		);

		// adjust likings when game loaded
		raceInteractions.manipulateRaceLikings(config);
	}

	@Override
	public void initGamePresent() {
		GameAiApi.injectAIModule(aiModuleRace,
			HTYPE.SUBJECT,
			HTYPE.RETIREE,
			HTYPE.RECRUIT,
			HTYPE.STUDENT,
			HTYPE.NOBILITY,
			HTYPE.CHILD
		);

		UIGameConfig uiGameConfig = new UIGameConfig(configPanel);
		uiGameConfig.init();
	}

	@Override
	public SCRIPT_INSTANCE initAfterGameCreated() {
		return new Instance(this, ConfigStore.getInstance());
	}
}
