package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Třída GameLogger centralizuje správu loggeru v celé hře.
 * Umožňuje zapnout nebo vypnout logování bez úpravy zdrojového kódu ostatních tříd.
 */
public class GameLogger {

    private static final Logger LOGGER = Logger.getLogger("GameLogger");

    // Měnitelná hodnota podle argumentu při spuštění nebo jiného vstupu
    private static boolean enabled = false;

    static {
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        LOGGER.addHandler(handler);
        LOGGER.setLevel(Level.ALL);
    }

    /** Zapne nebo vypne logování dynamicky */
    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static void info(String msg) {
        if (enabled) LOGGER.info(msg);
    }

    public static void error(String msg) {
        if (enabled) LOGGER.severe(msg);
    }

    public static boolean isEnabled() {
        return enabled;
    }

}
