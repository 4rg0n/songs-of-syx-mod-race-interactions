package com.github.argon.sos.interactions.util;

import com.github.argon.sos.interactions.log.Logger;
import com.github.argon.sos.interactions.log.Loggers;
import snake2d.util.gui.GuiSection;
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
public class UIUtil {

    private final static Logger log = Loggers.getLogger(UIUtil.class);

    /**
     * Contains UIs like a yes/no prompt or a text input
     *
     * @throws UINotAvailableException when ui isn't initialized yet
     */
    public static Interrupters interrupters() {
        Interrupters interrupters = VIEW.inters();

        if (interrupters == null) {
            throw new UINotAvailableException("Games interrupt ui isn't initialized yet.");
        }

        return interrupters;
    }

    /**
     * Contains the settlements ui elements
     *
     * @throws UINotAvailableException when ui isn't initialized yet
     */
    public static SettView settlement() {
        SettView settView = VIEW.s();

        if (settView == null) {
            throw new UINotAvailableException("Games settlement ui isn't initialized yet.");
        }

        return settView;
    }

    /**
     * @throws UINotAvailableException when ui isn't initialized yet
     */
    public static VIEW.ViewSub currentView() {
        VIEW.ViewSub currentView = VIEW.current();

        if (currentView == null) {
            throw new UINotAvailableException("Games current view isn't initialized yet.");
        }

        return currentView;
    }

    /**
     * For showing tools like the selection tool when copying an area
     */
    public static ToolManager tools() {
        return settlement().tools;
    }

    /**
     * Will open up an {@link ISidePanel} on the left side of the games UI
     */
    public static void showPanel(ISidePanel panel, boolean closeOthers) {
        VIEW.s().panels.add(panel, closeOthers);
    }

    public static Optional<GuiSection> getUIBuildPanelSection() {
        return findUIElement(UIBuildPanel.class)
                .flatMap(uiBuildPanel -> ReflectionUtil.getField("section", uiBuildPanel)
                .map(GuiSection.class::cast));
    }

    /**
     * Tries to find the first UI element in the games ui managers matching the given class.
     */
    public static <T> Optional<T> findUIElement(Class<T> clazz) {
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
    public static <T> Optional<T> findUIElementInCurrentView(Class<T> clazz) {
        return ReflectionUtil.getField("inters", currentView().uiManager)
                .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
    }

    /**
     * Tries to find an ui element by class in {@link VIEW#s()} ui manager.
     */
    public static <T> Optional<T> findUIElementInSettlementView(Class<T> clazz) {
        return ReflectionUtil.getField("inters", settlement().uiManager)
                .flatMap(inters -> extractFromIterable((Iterable<?>) inters, clazz));
    }

    /**
     * Ui elements in popups are only available when displayed.
     */
    public static <T> Optional<T> findUIElementInPopups(Class<T> clazz) {
        return ReflectionUtil.getField("m", interrupters().popup)
                .flatMap(uiManager ->
                    ReflectionUtil.getField("inters", uiManager).flatMap(inters ->
                        extractFromIterable((Iterable<?>) inters, clazz))
                );
    }

    private static <T> Optional<T> extractFromIterable(Iterable<?> iterable, Class<T> clazz) {
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
