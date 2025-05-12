package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * The {@code iron_bib} class represents an iron chest piece – the most important part of the armor,
 * which covers the torso and provides the highest defense of all equipment pieces.
 * <p>
 * Its defense value is 5.
 * </p>
 */

public class iron_bib extends GameObject implements Armor {
    private final float defensAmount;

    /**
     * Constructs a new {@code iron_bib} armor piece.
     * <p>
     * Initializes the item name, defense amount, and loads the corresponding image resource.
     * </p>
     */
    public iron_bib() {
        name = "iron_bib";
        defensAmount = 5;
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/iron_bib.png");
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
     * @return the defense value of the iron bib
     */
    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}