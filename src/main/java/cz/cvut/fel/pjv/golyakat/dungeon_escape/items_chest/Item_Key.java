package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Třída {@code Item_Key} reprezentuje zlatý klíč,
 * který může hráč najít v truhle a následně jej použít k otevření zamčených dveří.
 * <p>
 * Klíč je vizuálně reprezentován ikonou načtenou z interních zdrojů
 * a dědí z {@link GameObject}, což umožňuje jeho manipulaci v inventáři i světě.
 * </p>
 */
public class Item_Key extends GameObject {

    /**
     * Vytváří instanci předmětu {@code Key}, nastavuje název a načítá obrázek klíče.
     */
    public Item_Key() {
        name = "Key";
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/golden_key.png")));
        } catch (Exception e) {
            GameLogger.error("Chyba při načítání obrázku pro Key: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
