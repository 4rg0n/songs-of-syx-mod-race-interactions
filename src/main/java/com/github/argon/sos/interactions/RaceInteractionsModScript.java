package com.github.argon.sos.interactions;


import com.github.argon.sos.interactions.ai.AIModule_Race;
import com.github.argon.sos.interactions.config.ConfigJsonService;
import com.github.argon.sos.interactions.config.ConfigSaver;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.config.RacePrefCategory;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.race.RaceService;
import com.github.argon.sos.interactions.ui.UIGameConfig;
import com.github.argon.sos.interactions.ui.race.RaceInteractionsConfigPanel;
import com.github.argon.sos.interactions.game.api.GameAiApi;
import com.github.argon.sos.interactions.game.api.GameRaceApi;
import com.github.argon.sos.interactions.game.SCRIPT;
import lombok.NoArgsConstructor;
import settlement.entity.humanoid.HTYPE;
import util.info.INFO;

import java.util.Arrays;
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
		Loggers.setLevels(Level.FINER);
	}

	@Override
	public void initGameRunning() {
		// store vanilla likings for reset
		GameRaceApi.getInstance().initVanillaLikings();
	}

	@Override
	public void initGameLoaded() {
		log.debug("Initializing game resources and mod config");
		ConfigJsonService configJsonService = ConfigJsonService.getInstance();

		// load current config from save game (current) or from user profile or use mod config or fallback to default =D
		RaceInteractionsConfig config = RaceInteractionsConfig.getCurrent().orElseGet(() ->
				configJsonService.loadProfileConfig().orElseGet(configJsonService::loadModOrDefaultConfig));

		// "business logic"
		RaceService raceService = new RaceService(config.getGameRaces());
		RaceInteractions raceInteractions = RaceInteractions.Builder.build(raceService);

		// Race InteractionsAI
		AIModule_Race aiModuleRace = RaceInteractions.Builder.buildAI(raceInteractions);
		// todo this COULD be configurable via json
		GameAiApi.injectAIModule(aiModuleRace,
			HTYPE.SUBJECT,
			HTYPE.RETIREE,
			HTYPE.RECRUIT,
			HTYPE.STUDENT,
			HTYPE.NOBILITY,
			HTYPE.CHILD
		);

		// Config UI Panel
		RaceInteractionsConfigPanel configPanel = RaceInteractions.Builder.buildConfigUI(
			config,
			raceInteractions,
			raceService.getAllRaceInfo()
		);
		UIGameConfig uiGameConfig = new UIGameConfig(configPanel);
		uiGameConfig.init();

		// adjust likings when game loaded
		raceInteractions.manipulateRaceLikings(config);
	}

	@Override
	public SCRIPT_INSTANCE initAfterGameCreated() {
		return new Instance(this,
			ConfigSaver.getInstance(),
			ConfigJsonService.getInstance()
		);
	}

}
