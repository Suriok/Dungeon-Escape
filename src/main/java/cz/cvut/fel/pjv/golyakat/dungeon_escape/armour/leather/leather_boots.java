package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ItemType;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * The {@code leather_boots} class represents leather boots,
 * which are equipped on the player's feet and provide basic protection.
 * <p>
 * The defense value is 1 point.
 * </p>
 */

public class leather_boots extends GameObject implements Armor {
    private final float defensAmount;

    /**
     * Constructs a new {@code leather_boots} armor piece.
     * <p>
     * Initializes the item name, defense amount, and loads the corresponding image resource.
     * </p>
     */
    public leather_boots() {
        name = "leather_boots";
        defensAmount = 1;
        String path = "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_boots.png";

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
            GameLogger.info("Successfully loaded leather_boots.png");
        } catch (IOException e) {
            GameLogger.error("Error loading leather_boots image: " + e.getMessage());
        } catch (NullPointerException e) {
            GameLogger.error("leather_boots.png not found in resources: " + e.getMessage());
        }
    }

    /**
     * Returns the defense amount provided by this armor piece.
     *
     * @return the defense value of the leather boots
     */
    @Override
    public float getDefensAmount() {
        return defensAmount;
    }

    public ItemType getType() {
        return ItemType.ARMOR;
    }
}