package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import javax.swing.JFrame;

// Hlavní spouštěcí třída hry
public class main {
    public static void main(String[] args) {
        // 1. Vytvoření hlavního okna aplikace
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Při zavření okna se aplikace ukončí
        window.setResizable(false); // Okno nebude možné měnit velikost
        window.setTitle("Dungeon Escape"); // Název okna

        // 2. Vytvoření instance herního panelu
        gamePanel gamePanel = new gamePanel();

        // 3. Přidání herního panelu do okna
        window.add(gamePanel);

        // 4. Automaticky nastaví velikost okna podle preferované velikosti komponenty (gamePanel)
        window.pack();

        // 5. Umístí okno na střed obrazovky
        window.setLocationRelativeTo(null);

        // 6. Zobrazí okno
        window.setVisible(true);

        // 7. Inicializace herních objektů (truhly, dveře, monstra atd.)
        gamePanel.setUpObjects();

        // 8. Spustí herní smyčku (game loop)
        gamePanel.startGameThread();
    }
}
