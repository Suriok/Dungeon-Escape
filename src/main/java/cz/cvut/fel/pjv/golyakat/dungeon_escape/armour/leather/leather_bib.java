package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ItemType;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * The {@code leather_bib} class represents a leather chest piece,
 * which is equipped on the player's torso and provides higher level of protection.
 * <p>
 * The defense value of the chest armor is 4 points. This is the most important piece of equipment.
 * </p>
 */
public class leather_bib extends GameObject implements Armor {
    private final float defensAmount;

    /**
     * Constructs a new {@code leather_bib} armor piece.
     * <p>
     * Initializes the item name, defense amount, and loads the corresponding image resource.
     * </p>
     */
    public leather_bib() {
        name = "leather_bib";
        defensAmount = 4;
        String path = "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_bib.png";

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
            GameLogger.info("Successfully loaded leather_bib.png");
        } catch (IOException e) {
            GameLogger.error("Error loading leather_bib image: " + e.getMessage());
        } catch (NullPointerException e) {
            GameLogger.error("leather_bib.png not found in resources: " + e.getMessage());
        }
    }


    /**
     * Returns the defense amount provided by this armor piece.
     *
     * @return the defense value of the leather bib
     */
    @Override
    public float getDefensAmount() {
        return defensAmount;
    }

    public ItemType getType() {
        return ItemType.ARMOR;
    }
}