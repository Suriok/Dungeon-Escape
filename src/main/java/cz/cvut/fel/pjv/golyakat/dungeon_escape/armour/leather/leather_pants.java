package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ItemType;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;

/**
 * The {@code leather_pants} class represents leather pants,
 * which protect the lower part of the player's body.
 * <p>
 * The armor has a defense value of 2 points.
 * The icon is loaded from an internal resource file.
 * </p>
 */

public class leather_pants extends GameObject implements Armor {
    private final float defensAmount;

    /**
     * Constructs a new {@code leather_pants} armor piece.
     * <p>
     * Initializes the item name, defense amount, and loads the corresponding image resource.
     * </p>
     */
    public leather_pants() {
        name = "leather_pants";
        defensAmount = 2;
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_pants.png");
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
     * @return the defense value of the leather pants
     */
    @Override
    public float getDefensAmount() {
        return defensAmount;
    }

    public ItemType getType() {
        return ItemType.ARMOR;
    }
}