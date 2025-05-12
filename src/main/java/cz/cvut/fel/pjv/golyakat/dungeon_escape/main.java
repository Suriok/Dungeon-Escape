package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main launching class of the Dungeon Escape game.
 * <p>
 * Creates the main application window, initializes the game panel,
 * sets up the closing logic, and starts the main game loop.
 * </p>
 */
public class main {

    /**
     * Main entry method – launches the entire game.
     * <p>
     * Runs in the context of the Event Dispatch Thread (EDT), as recommended by the Swing API.
     * </p>
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        // Run everything on the EDT – Swing components are not thread-safe
        SwingUtilities.invokeLater(() -> {

            /**
             * 1) Creation of the main game window (JFrame instance).
             * The window is named, and resizing is disabled.
             */
            JFrame window = new JFrame("Dungeon Escape");
            window.setResizable(false);

            /**
             * 2) Creation of an instance of the game panel {@link gamePanel} and attaching it to the window.
             * The panel contains all graphics, game logic, and the main loop.
             */
            gamePanel gp = new gamePanel();
            window.add(gp);

            /**
             * 3) Setting the window size based on the preferred size of the game panel.
             * Also centers the window on the screen.
             */
            window.pack();
            window.setLocationRelativeTo(null);

            /**
             * 4) Custom handling of window closing – first saves the game using {@link gamePanel#saveGame()},
             * then terminates the program via {@link System#exit(int)}.
             */
            window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gp.saveGame();   // Saving the current game state to XML
                    System.exit(0);  // Terminating the application
                    GameLogger.info("windowClosing → saveGame()"); // Debug message
                }
            });

            /**
             * 5) Making the game window visible.
             */
            window.setVisible(true);

            /**
             * 6) Initialization of objects on the map using {@link gamePanel#setUpObjects()} and starting
             * the game loop using {@link gamePanel#startGameThread()}.
             */
            gp.setUpObjects();
            gp.startGameThread();
        });
    }
}