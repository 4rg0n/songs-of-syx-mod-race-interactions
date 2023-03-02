package com.github.argon.sos.interactions;

import com.github.argon.sos.interactions.config.ConfigStore;
import com.github.argon.sos.interactions.config.RaceInteractionsConfig;
import com.github.argon.sos.interactions.game.SCRIPT;
import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import lombok.RequiredArgsConstructor;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import util.gui.misc.GBox;
import view.keyboard.KEYS;
import view.main.VIEW;

@RequiredArgsConstructor
final class Instance implements SCRIPT.SCRIPT_INSTANCE {

	private final static Logger log = Loggers.getLogger(Instance.class);

	private boolean init = false;

	private boolean initGamePresent = false;

	private final SCRIPT<RaceInteractionsConfig> script;

	private final ConfigStore configStore;

	@Override
	public void save(FilePutter file) {
		configStore.saveGame(file);
	}

	@Override
	public void load(FileGetter file) {
		RaceInteractionsConfig config = configStore.loadSave(file)
			.orElseGet(configStore::loadJsonOrDefault);
		configStore.setCurrentConfig(config);
		script.initGameLoaded(config);
	}

	@Override
	public boolean handleBrokenSavedState() {
		configStore.setCurrentConfig(configStore.loadJsonOrDefault());
		return true;
	}

	@Override
	public void update(double ds) {
		if (!init) {
			log.debug("initGameRunning");
			script.initGameRunning();
			init = true;
		}

		if (!initGamePresent && !VIEW.inters().load.isActivated()) {
			log.debug("initGameLoaded");
			script.initGamePresent();
			initGamePresent = true;
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
