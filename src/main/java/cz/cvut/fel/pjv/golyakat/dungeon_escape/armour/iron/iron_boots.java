package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;

/**
 * The {@code iron_boots} class represents iron boots that provide basic protection for the feet.
 * The boots have a defense value of 2.
 *
 */
public class iron_boots extends GameObject implements Armor {
    private final float defensAmount;

    /**
     * Constructs a new {@code iron_boots} armor piece.
     * <p>
     * Initializes the item name, defense amount, and loads the corresponding image resource.
     * </p>
     */
    public iron_boots() {
        name = "iron_boots";
        defensAmount = 2;
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/iron_boots.png");
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
     * @return the defense value of the iron boots
     */
    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}