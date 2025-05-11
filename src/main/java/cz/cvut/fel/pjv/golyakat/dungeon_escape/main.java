package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Hlavní spouštěcí třída hry Dungeon Escape.
 * <p>
 * Vytváří hlavní okno aplikace, inicializuje herní panel,
 * nastavuje zavírací logiku a spouští hlavní herní smyčku.
 * </p>
 */
public class main {

    /**
     * Hlavní vstupní metoda – spuštění celé hry.
     * <p>
     * Běží v kontextu Event Dispatch Threadu (EDT), jak doporučuje Swing API.
     * </p>
     *
     * @param args argumenty z příkazové řádky (nevyužité)
     */
    public static void main(String[] args) {
        // Vše spustíme na EDT – Swing komponenty nejsou thread-safe
        SwingUtilities.invokeLater(() -> {

            /**
             * 1) Vytvoření hlavního okna hry (instance JFrame).
             * Okno je pojmenováno a zablokována možnost změny velikosti.
             */
            JFrame window = new JFrame("Dungeon Escape");
            window.setResizable(false);

            /**
             * 2) Vytvoření instance herního panelu {@link gamePanel} a jeho připojení do okna.
             * Panel obsahuje veškerou grafiku, logiku hry i hlavní smyčku.
             */
            gamePanel gp = new gamePanel();
            window.add(gp);

            /**
             * 3) Nastavení velikosti okna podle preferované velikosti herního panelu.
             * Také umístíme okno doprostřed obrazovky.
             */
            window.pack();
            window.setLocationRelativeTo(null);

            /**
             * 4) Vlastní ošetření zavření okna – nejprve se uloží hra pomocí {@link gamePanel#saveGame()},
             * poté se ukončí program přes {@link System#exit(int)}.
             */
            window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gp.saveGame();   // Uložení aktuálního stavu hry do XML
                    System.exit(0);  // Ukončení aplikace
                    GameLogger.info("windowClosing → saveGame()"); // Debug zpráva
                }
            });

            /**
             * 5) Zviditelnění herního okna.
             */
            window.setVisible(true);

            /**
             * 6) Inicializace objektů na mapě pomocí {@link gamePanel#setUpObjects()} a spuštění
             * herní smyčky pomocí {@link gamePanel#startGameThread()}.
             */
            gp.setUpObjects();
            gp.startGameThread();
        });
    }
}
