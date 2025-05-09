package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Hlavní spouštěcí třída hry */
public class main {

    public static void main(String[] args) {
        /* Vše spustíme na EDT, jak doporučuje Swing */
        SwingUtilities.invokeLater(() -> {

            /* 1) Vytvoření hlavního okna */
            JFrame window = new JFrame("Dungeon Escape");
            window.setResizable(false);

            /* 2) Vytvoření a přidání herního panelu */
            gamePanel gp = new gamePanel();
            window.add(gp);

            /* 3) Automatické nastavení velikosti okna podle preferované velikosti panelu */
            window.pack();
            window.setLocationRelativeTo(null);   // na střed obrazovky

            /* 4) Při zavírání okna nejdřív uložíme hru a pak aplikaci ukončíme */
            window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gp.saveGame();       // uloží aktuální stav
                    System.exit(0);
                    System.out.println("windowClosing → saveGame()");   // korektní ukončení JVM
                }
            });

            /* 5) Zobrazíme okno */
            window.setVisible(true);

            /* 6) Inicializace herních objektů a spuštění smyčky */
            gp.setUpObjects();           // NE staticky – voláme na instanci
            gp.startGameThread();
        });
    }
}
