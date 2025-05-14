package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;

/**
 * The {@code iron_helmet} class represents an iron helmet that provides medium level protection for the player's head.
 * <p>
 * It provides a defense value of 2.
 * </p>
 * <p>
 * The image is loaded from the resource folder and is used for rendering in both inventory and on the character.
 * </p>
 */

public class iron_helmet extends GameObject implements Armor {
    private final float defensAmount;

    /**
     * Constructs a new {@code iron_helmet} armor piece.
     * <p>
     * Initializes the item name, defense amount, and loads the corresponding image resource.
     * </p>
     */
    public iron_helmet() {
        name = "iron_helmet";
        defensAmount = 2;
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/iron_helmet.png");
            if (stream == null) {
                GameLogger.error("Error: leather_pants.png not found in resources.");
            } else {
                image = ImageIO.read(stream);
                GameLogger.info("Successfully loaded leather_pants.png");
            }
        } catch (Exception e) {
            GameLogger.error("Error loading leather_pants image: " + e.getMessage());
        }
    }

    /**
     * Returns the defense amount provided by this armor piece.
     *
     * @return the defense value of the iron helmet
     */
    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}