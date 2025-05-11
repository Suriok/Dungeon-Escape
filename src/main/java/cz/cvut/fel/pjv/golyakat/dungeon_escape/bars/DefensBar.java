package cz.cvut.fel.pjv.golyakat.dungeon_escape.bars;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import java.awt.*;

/**
 * Třída {@code DefensBar} slouží k vizuálnímu zobrazení celkové obrany hráče.
 * <p>
 * Obranný ukazatel se zobrazuje jako vodorovný pruh, který se mění v závislosti
 * na síle nasazeného brnění. Barva výplně je modrá, obvod je bílý.
 * </p>
 */
public class DefensBar {

    /**
     * Odkaz na hlavní panel hry, ze kterého se čerpají rozměry a stav.
     */
    private gamePanel gp;

    /**
     * Hodnota aktuální obrany hráče (0–10).
     */
    private float defense;

    /**
     * Souřadnice X levého horního rohu obranného ukazatele.
     */
    private int x;

    /**
     * Souřadnice Y levého horního rohu obranného ukazatele.
     */
    private int y;

    /**
     * Šířka ukazatele obrany.
     */
    private int barWidth;

    /**
     * Výška ukazatele obrany.
     */
    private int barHeight;

    /**
     * Konstruktor inicializuje ukazatel obrany s výchozími souřadnicemi a rozměry.
     *
     * @param gp hlavní panel hry
     */
    public DefensBar(gamePanel gp) {
        this.gp = gp;
        this.x = 10;
        this.y = 80;
        this.barWidth = 200;
        this.barHeight = 10;
        this.defense = 0;
    }

    /**
     * Aktualizuje hodnotu obrany hráče pro vykreslení.
     *
     * @param defense aktuální hodnota obrany (0–10)
     */
    public void update(float defense) {
        this.defense = defense;
    }

    /**
     * Vykreslí ukazatel obrany na obrazovku – pozadí, výplň a text s hodnotou.
     *
     * @param g2 grafický kontext
     */
    public void draw(Graphics2D g2) {
        g2.setColor(Color.GRAY);
        g2.fillRect(x, y, barWidth, barHeight);

        g2.setColor(Color.BLUE);
        int filledWidth = (int) (barWidth * (defense / 10.0f));
        g2.fillRect(x, y, filledWidth, barHeight);

        g2.setColor(Color.WHITE);
        g2.drawRect(x, y, barWidth, barHeight);

        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Defense: " + defense, x, y - 5);
    }

    /**
     * Vrací svislou pozici Y horní hrany obranného ukazatele.
     *
     * @return souřadnice Y
     */
    public int getY() {
        return y;
    }

    /**
     * Vrací výšku ukazatele obrany.
     *
     * @return výška v pixelech
     */
    public int getBarHeight() {
        return barHeight;
    }
}
