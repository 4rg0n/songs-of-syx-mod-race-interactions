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
import com.github.argon.sos.interactions.util.AIUtil;
import com.github.argon.sos.interactions.util.RaceUtil;
import com.github.argon.sos.interactions.util.SCRIPT;
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
		RaceUtil.initVanillaLikings();
	}

	@Override
	public void initGameLoaded() {
		log.debug("Initializing game resources and mod config");
		ConfigJsonService configJsonService = ConfigJsonService.getInstance();

		// load current config from save game or from user profile or use mod config
		RaceInteractionsConfig config = RaceInteractionsConfig.getCurrent().orElseGet(() ->
				configJsonService.loadProfileConfig().orElseGet(configJsonService::loadModConfig));

		RaceService raceService = new RaceService(config.getGameRaces());
		RaceInteractions raceInteractions = RaceInteractions.Builder.build(raceService);
		AIModule_Race ai = RaceInteractions.Builder.buildAI(raceInteractions);
		AIUtil.injectAIModule(HTYPE.SUBJECT, ai);

		RaceInteractionsConfigPanel configPanel = RaceInteractions.Builder.buildConfigUI(config, raceInteractions, raceService.getAllRaceInfo());

		// adjust likings when game loaded
		raceInteractions.manipulateRaceLikings(config);
		UIGameConfig uiGameConfig = new UIGameConfig(configPanel);
		// inject ui elements into game ui
		uiGameConfig.init();
	}

	@Override
	public SCRIPT_INSTANCE initAfterGameCreated() {
		return new Instance(this,
			ConfigSaver.getInstance(),
			ConfigJsonService.getInstance()
		);
	}

}
