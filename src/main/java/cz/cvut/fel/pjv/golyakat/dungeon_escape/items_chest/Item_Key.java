package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ItemType;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * The {@code Item_Key} class represents a golden key
 * that the player can find in a chest and use to open locked doors.
 * <p>
 * The key is visually represented by an icon loaded from internal resources
 * and inherits from {@link GameObject}, which allows its manipulation in both inventory and world.
 * </p>
 */
public class Item_Key extends GameObject {

    /**
     * Creates an instance of the {@code Key} item, sets its name and loads the key image.
     */
    public Item_Key() {
        name = "Key";
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/golden_key.png")));
        } catch (Exception e) {
            GameLogger.error("Error loading image for Key: " + e.getMessage());
        }
    }

    public ItemType getType() {
        return ItemType.KEY;
    }
}
