package com.github.argon.sos.interactions;

import com.github.argon.sos.interactions.config.ConfigSaver;
import com.github.argon.sos.interactions.config.ConfigJsonService;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.game.SCRIPT;
import lombok.RequiredArgsConstructor;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import util.gui.misc.GBox;
import view.keyboard.KEYS;
import view.main.VIEW;

import java.io.IOException;

@RequiredArgsConstructor
final class Instance implements SCRIPT.SCRIPT_INSTANCE {

	private final static Logger log = Loggers.getLogger(Instance.class);

	private boolean init = false;

	private boolean initGameLoaded = false;

	private final SCRIPT script;

	private final ConfigSaver configSaver;

	private final ConfigJsonService configJsonService;


	@Override
	public void save(FilePutter file) {
		RaceInteractionsConfig.getCurrent().ifPresent(config -> configSaver.save(file, config));
	}

	@Override
	public void load(FileGetter file) throws IOException {
		RaceInteractionsConfig config = configSaver.load(file)
			.orElse(configJsonService.loadProfileConfig()
				.orElse(configJsonService.loadModOrDefaultConfig()));
		RaceInteractionsConfig.setCurrent(config);
	}

	@Override
	public boolean handleBrokenSavedState() {
		RaceInteractionsConfig.setCurrent(configJsonService.loadProfileConfig().orElseGet(configJsonService::loadModOrDefaultConfig));
		return true;
	}

	@Override
	public void update(double ds) {
		if (!init) {
			log.debug("initGameRunning");
			script.initGameRunning();
			init = true;
		}

		if (!initGameLoaded && !VIEW.inters().load.isActivated()) {
			log.debug("initGameLoaded");
			script.initGameLoaded();
			initGameLoaded = true;
		}
	}
	
	@Override
	public void hoverTimer(double mouseTimer, GBox text) {
	}

	@Override
	public void render(Renderer r, float ds) {
	}

	@Override
	public void keyPush(KEYS key) {

	}

	@Override
	public void mouseClick(MButt button) {

	}

	@Override
	public void hover(COORDINATE mCoo, boolean mouseHasMoved) {

	}
}
