package cz.cvut.fel.pjv.golyakat.dungeon_escape.bars;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Třída {@code HealthBar} slouží k vizuálnímu zobrazení zdraví hráče
 * formou srdíček na obrazovce.
 * <p>
 * Každé srdce představuje 2 jednotky HP a mění svůj vzhled podle aktuálního stavu hráče.
 * Obrazy jednotlivých fází zranění jsou načítány při konstrukci objektu.
 * </p>
 */
public class HealthBar extends GameObject {

    /**
     * Obrázek plného srdce (2 HP).
     */
    private BufferedImage fullHp;

    /**
     * Obrázek srdce při lehkém zásahu (~0.5 HP).
     */
    private BufferedImage hit1;

    /**
     * Obrázek srdce při středním zásahu (~1 HP).
     */
    private BufferedImage hit2;

    /**
     * Obrázek srdce při těžkém zásahu (~1.5 HP).
     */
    private BufferedImage hit3;

    /**
     * Obrázek prázdného (mrtvého) srdce (0 HP).
     */
    private BufferedImage die;

    /**
     * Odkaz na hlavní herní panel, slouží pro zjištění velikosti dlaždic atd.
     */
    private gamePanel gp;

    /**
     * Maximální počet jednotek zdraví (např. 8 = 4 srdce).
     */
    private final int maxHp = 8;

    /**
     * Aktuální počet jednotek zdraví hráče.
     */
    private int currentHp;

    /**
     * Vytvoří nový ukazatel zdraví a načte obrázky všech stavů srdcí.
     *
     * @param gp herní panel, ze kterého se získávají data
     */
    public HealthBar(gamePanel gp) {
        this.gp = gp;
        this.currentHp = maxHp;
        loadImages();
    }

    /**
     * Načte obrázky všech variant srdce z adresáře resource.
     */
    private void loadImages() {
        try {
            fullHp = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/Full_Hp.jpg"));
            hit1 = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/1_hit.jpg"));
            hit2 = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/2_hit.jpg"));
            hit3 = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/3_hit.jpg"));
            die = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/die.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Aktualizuje hodnotu aktuálního zdraví, které bude vizuálně vykresleno.
     *
     * @param playerHp aktuální počet HP hráče (0–8)
     */
    public void update(int playerHp) {
        this.currentHp = playerHp;
    }

    /**
     * Vykreslí ukazatel zdraví v levém horním rohu obrazovky.
     *
     * @param g2 grafický kontext, do kterého se vykresluje
     */
    public void draw(Graphics2D g2) {
        int x = 10;
        int y = 10;
        int spacing = 1;

        for (int i = 0; i < maxHp / 2; i++) {
            int heartHp = Math.min(2, Math.max(0, currentHp - i * 2));
            BufferedImage heartImage = getHeartImage(heartHp);
            g2.drawImage(heartImage, x + (i * (gp.tileSize + spacing)), y, gp.tileSize, gp.tileSize, null);
        }
    }

    /**
     * Vrací odpovídající obrázek pro daný počet HP v jednom srdci (0–2).
     *
     * @param heartHp zdraví v rámci jednoho srdce (např. 1 = polovina)
     * @return obrázek odpovídající danému stavu srdce
     */
    private BufferedImage getHeartImage(int heartHp) {
        if (heartHp >= 2) {
            return fullHp;
        } else if (heartHp >= 1.5) {
            return hit3;
        } else if (heartHp >= 1) {
            return hit2;
        } else if (heartHp > 0) {
            return hit1;
        } else {
            return die;
        }
    }
}
