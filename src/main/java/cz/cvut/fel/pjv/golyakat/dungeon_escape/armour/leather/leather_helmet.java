package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * The {@code leather_helmet} class represents a leather helmet,
 * which is equipped on the player's head and provides basic protection.
 * <p>
 * The defense value of the helmet is 1 point.
 * </p>
 */

public class leather_helmet extends GameObject implements Armor {
    private final float defensAmount;

    /**
     * Constructs a new {@code leather_helmet} armor piece.
     * <p>
     * Initializes the item name, defense amount, and loads the corresponding image resource.
     * </p>
     */
    public leather_helmet() {
        name = "leather_helmet";
        defensAmount = 1;
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_helmet.png");
            if (stream == null) {
                GameLogger.error("Error: leather_helmet.png not found in resources.");
            } else {
                image = ImageIO.read(stream);
                GameLogger.info("Successfully loaded leather_helmet.png");
            }
        } catch (Exception e) {
            GameLogger.error("Error loading leather_helmet image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns the defense amount provided by this armor piece.
     *
     * @return the defense value of the leather helmet
     */
    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}