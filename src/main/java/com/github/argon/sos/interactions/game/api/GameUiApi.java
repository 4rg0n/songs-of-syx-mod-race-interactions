package com.github.argon.sos.interactions.game.api;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import com.github.argon.sos.interactions.util.ReflectionUtil;
import com.github.argon.sos.interactions.game.GameUiNotAvailableException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import view.interrupter.ISidePanel;
import view.main.Interrupters;
import view.main.VIEW;
import view.sett.SettView;
import view.sett.ui.bottom.UIBuildPanel;
import view.tool.ToolManager;

import java.util.Optional;

/**
 * For hooking into the games UI
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameUiApi {

    private final static Logger log = Loggers.getLogger(GameUiApi.class);

    @Getter(lazy = true)
    private final static GameUiApi instance = new GameUiApi();

    /**
     * Contains UIs like a yes/no prompt or a text input
     *
     * @throws GameUiNotAvailableException when ui isn't initialized yet
     */
    public Interrupters interrupters() {
        Interrupters interrupters = VIEW.inters();

        if (interrupters == null) {
            throw new GameUiNotAvailableException("Games interrupt ui isn't initialized yet.");
        }

        return interrupters;
    }

    /**
     * Contains the settlements ui elements
     *
     * @throws GameUiNotAvailableException when ui isn't initialized yet
     */
    public SettView settlement() {
        SettView settView = VIEW.s();

        if (settView == null) {
            throw new GameUiNotAvailableException("Games settlement ui isn't initialized yet.");
        }

        return settView;
    }

    /**
     * @throws GameUiNotAvailableException when ui isn't initialized yet
     */
    public VIEW.ViewSub currentView() {
        VIEW.ViewSub currentView = VIEW.current();

        if (currentView == null) {
            throw new GameUiNotAvailableException("Games current view isn't initialized yet.");
        }

        return currentView;
    }

    /**
     * For showing tools like the selection tool when copying an area
     */
    public ToolManager tools() {
        return settlement().tools;
    }

    /**
     * Will open up an {@link ISidePanel} on the left side of the games UI
     */
    public void showPanel(ISidePanel panel, boolean closeOthers) {
        settlement().panels.add(panel, closeOthers);
    }

    /**
     * Will open a popup window near the given button
     */
    public void showPopup(RENDEROBJ popup, CLICKABLE button) {
        interrupters().popup.show(popup, button);
    }

    /**
     * @return bottom build section of the game ui
     */
    public Optional<GuiSection> getUIBuildPanelSection() {
        return findUIElement(UIBuildPanel.class)
                .flatMap(uiBuildPanel -> ReflectionUtil.getDeclaredField("section", uiBuildPanel)
                .map(GuiSection.class::cast));
    }

    /**
     * Tries to find the first UI element in the games ui managers matching the given class.
     */
    public <T> Optional<T> findUIElement(Class<T> clazz) {
        // search in VIEW.current() uiManager
        Optional<T> optionalUiElement = findUIElementInCurrentView(clazz);

        if (optionalUiElement.isPresent()) {
            return optionalUiElement;
        }

        // search in VIEW.s() uiManager
        return findUIElementInSettlementView(clazz);
    }

    /**
     * Tries to find an ui element by class in {@link VIEW#current()} ui manager.
     */
    public <T> Optional<T> findUIElementInCurrentView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredField("inters", currentView().uiManager)
                .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
    }

    /**
     * Tries to find an ui element by class in {@link VIEW#s()} ui manager.
     */
    public <T> Optional<T> findUIElementInSettlementView(Class<T> clazz) {
        return ReflectionUtil.getDeclaredField("inters", settlement().uiManager)
                .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
    }

    /**
     * Ui elements in popups are only available when displayed.
     */
    public <T> Optional<T> findUIElementInPopups(Class<T> clazz) {
        return ReflectionUtil.getDeclaredField("m", interrupters().popup)
                .flatMap(uiManager ->
                    ReflectionUtil.getDeclaredField("inters", uiManager).flatMap(inters ->
                        extractFromIterable((Iterable<?>) inters, clazz)));
    }

    private <T> Optional<T> extractFromIterable(Iterable<?> iterable, Class<T> clazz) {
        for (Object inter : iterable) {
            if (clazz.isInstance(inter)) {
                log.debug("Found ui element %s", clazz.getSimpleName());
                //noinspection unchecked
                return Optional.of((T) inter);
            }
        }

        return Optional.empty();
    }
}
