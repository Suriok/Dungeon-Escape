package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * The {@code iron_pants} class represents iron pants that protect the lower half of the player's body.
 * <p>
 * They provide a defense value of 3.
 * The pants image is loaded from a file and displayed in both inventory and equipment selection.
 * </p>
 */

public class iron_pants extends GameObject implements Armor {
    private final float defensAmount;


    /**
     * Constructs a new {@code iron_pants} armor piece.
     * <p>
     * Initializes the item name, defense amount, and loads the corresponding image resource.
     * </p>
     */
    public iron_pants() {
        name = "iron_pants";
        defensAmount = 3;
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/iron_pants.png");
            if (stream == null) {
                GameLogger.error("Error: leather_pants.png not found in resources.");
            } else {
                image = ImageIO.read(stream);
                GameLogger.info("Successfully loaded leather_pants.png");
            }
        } catch (Exception e) {
            GameLogger.error("Error loading leather_pants image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Returns the defense amount provided by this armor piece.
     *
     * @return the defense value of the iron pants
     */
    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}