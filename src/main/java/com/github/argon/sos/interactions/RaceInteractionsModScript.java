package com.github.argon.sos.interactions;


import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.race.RacePrefCategory;
import com.github.argon.sos.interactions.util.SCRIPT;
import lombok.NoArgsConstructor;
import util.info.INFO;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Entry point
 */
@NoArgsConstructor
public final class RaceInteractionsModScript implements SCRIPT {
	private final static Logger log = Loggers.getLogger(RaceInteractionsModScript.class);

	private final INFO info = new INFO(
			"Race Interactions",
			"Manipulates the likings of races to each other based on their equality in preferences: " +
					Arrays.toString(RacePrefCategory.values()));

	@Override
	public CharSequence name() {
		return info.name;
	}

	@Override
	public CharSequence desc() {
		return info.desc;
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
		RaceInteractionsConfig raceInteractionsConfig = RaceInteractionsConfig.load();

		RaceInteractions raceInteractions = new RaceInteractions(raceInteractionsConfig);
		raceInteractions.manipulateRaceLikings();
	}

	@Override
	public SCRIPT_INSTANCE initAfterGameCreated() {
		return new Instance(this);
	}

}
