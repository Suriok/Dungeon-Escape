package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Abstraktní třída {@code GameObject} slouží jako základ pro všechny interaktivní objekty ve hře.
 * <p>
 * Obsahuje základní atributy jako obrázky, pozici, kolizní plochu a logiku vykreslování.
 * Je určena k dědění – konkrétní objekty (truhly, dveře, crafting table) ji rozšiřují.
 * </p>
 */
public abstract class GameObject {

    /**
     * Obrázek objektu (hlavní vzhled).
     */
    public BufferedImage image;

    /**
     * Druhý obrázek – může být použit pro animaci nebo alternativní stav.
     */
    public BufferedImage image2;

    /**
     * Třetí obrázek – rozšířený vzhled (např. otevřená verze).
     */
    public BufferedImage image3;

    /**
     * Čtvrtý obrázek (volitelný).
     */
    public BufferedImage image4;

    /**
     * Pátý obrázek (volitelný).
     */
    public BufferedImage image5;

    /**
     * Název objektu (slouží k identifikaci).
     */
    public String name;

    /**
     * Příznak určující, zda objekt způsobuje kolizi (blokuje průchod).
     */
    public boolean Collision = true;

    /**
     * X-ová souřadnice objektu ve světovém prostoru.
     */
    public int worldX;

    /**
     * Y-ová souřadnice objektu ve světovém prostoru.
     */
    public int worldY;

    /**
     * Kolizní oblast objektu – slouží k detekci kontaktu s hráčem nebo entitami.
     */
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);

    /**
     * Výchozí X souřadnice kolizní oblasti.
     */
    public int solidAreaDefaultX = 0;

    /**
     * Výchozí Y souřadnice kolizní oblasti.
     */
    public int solidAreaDefaultY = 0;

    /**
     * Vykreslí objekt na obrazovku, pokud je v rámci zobrazitelné oblasti vůči hráči.
     *
     * @param g2d grafický kontext pro vykreslení
     * @param gp  hlavní panel hry, ze kterého se získává pozice hráče
     */
    public void draw(Graphics2D g2d, gamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
            if (image != null) {
                g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            } else {
                System.err.println("Nelze vykreslit objekt '" + name + "': obrázek je null");
            }
        } else {
            System.out.println(name + " se nachází mimo viditelnou oblast.");
        }
    }

    /**
     * Metoda pro interakci s objektem – je určena k přepsání v podtřídách.
     * <p>
     * Např. otevření dveří, otevření truhly, zahájení craftování atd.
     * </p>
     */
    public void interact() {
        // Přepsáno v podtřídách dle potřeby
    }
}
