package cz.cvut.fel.pjv.golyakat.dungeon_escape;


import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized logger utility for the Dungeon Escape game.
 * <p>
 * This class manages a {@link java.util.logging.Logger} instance and allows
 * to enable or disable logging globally without modifying other classes.
 * </p>
 */
public class GameLogger {

    /**
     * The underlying {@link Logger} instance used for all logging.
     */
    private static final Logger LOGGER = Logger.getLogger("GameLogger");

    /**
     * Flag indicating whether logging is enabled.
     * <p>
     * When {@code false}, all logging calls are no-ops.
     * </p>
     */
    private static boolean enabled = false;

    static {
        // === Disable use of parent handlers to avoid duplicate logs ===
        LOGGER.setUseParentHandlers(false);

        // === Create and attach a console handler that logs all levels ===
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        LOGGER.addHandler(handler);

        // === Set logger to log all levels by default ===
        LOGGER.setLevel(Level.ALL);
    }

    /**
     * Enables or disables logging at runtime.
     *
     * @param value {@code true} to enable logging; {@code false} to disable it
     */
    public static void setEnabled(boolean value) {
        enabled = value;
    }

    /**
     * Logs an informational message if logging is enabled.
     *
     * @param msg the message string to be logged
     */
    public static void info(String msg) {
        if (enabled) {
            LOGGER.info(msg);
        }
    }

    /**
     * Logs an error message if logging is enabled.
     *
     * @param msg the error message string to be logged
     */
    public static void error(String msg) {
        if (enabled) {
            LOGGER.severe(msg);
        }
    }

    /**
     * Checks whether logging is currently enabled.
     *
     * @return {@code true} if logging is enabled; {@code false} otherwise
     */
    public static boolean isEnabled() {
        return enabled;
    }

}
