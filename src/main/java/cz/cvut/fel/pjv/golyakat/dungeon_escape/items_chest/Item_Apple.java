package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ItemType;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * The {@code Item_Apple} class represents a healing item – an apple,
 * which restores a small amount of health (1/4 heart, i.e., 0.5 HP).
 * <p>
 * The apple is graphically represented by an icon and serves as a consumable item
 * in the player's inventory. It inherits from {@link GameObject}, which allows its use
 * both in the game world and inventory.
 * </p>
 */
public class Item_Apple extends GameObject {

    /**
     * Amount of health that the apple restores when used.
     */
    private final float healAmount;

    /**
     * Creates a new apple, sets its name, effect, and loads the image from the resource folder.
     */
    public Item_Apple() {
        name = "Apple";
        healAmount = 0.5f;

        try {
            image = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(
                            "/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/apple.png")));
        } catch (IOException e) {
            GameLogger.error("Error loading apple image" + e.getMessage());
        } catch (NullPointerException e) {
            GameLogger.error("apple.png not found in resources" + e.getMessage());
        }
    }

    /**
     * Returns the number of health units that the apple restores when used.
     *
     * @return health regeneration value (e.g., 0.5)
     */
    public float getHealAmount() {
        return healAmount;
    }

    public ItemType getType() {
        return ItemType.HEALING;
    }
}
