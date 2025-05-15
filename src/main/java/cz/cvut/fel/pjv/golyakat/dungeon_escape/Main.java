package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main launching class of the Dungeon Escape game.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Dungeon Escape");
            window.setResizable(false);

            gamePanel gp = new gamePanel();
            window.add(gp);

            window.pack();
            window.setLocationRelativeTo(null);

            window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    GameLogger.info("Window closing, saving game...");
                    gp.saveGame();
                    System.exit(0);
                }
            });

            window.setVisible(true);

            gp.setUpObjects();
            gp.startGameThread();
        });
    }
}
