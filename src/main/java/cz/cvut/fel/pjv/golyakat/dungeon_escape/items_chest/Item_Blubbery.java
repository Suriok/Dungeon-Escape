package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ItemType;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * The {@code Item_Blubbery} class represents a berry (blubbery),
 * which is a consumable healing item that restores a small amount of health to the player.
 * <p>
 * This item inherits from {@link GameObject}, so it can be added to the inventory
 * and rendered in the game using an image.
 * </p>
 */
public class Item_Blubbery extends GameObject {

    /**
     * Number of health units this item restores when used.
     */
    private final float healAmount;

    /**
     * Constructor sets the item's name, its healing effect, and loads the image from the resource folder.
     */
    public Item_Blubbery() {
        name = "blubbery";
        healAmount = 1f;
        String path = "/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/bluberry.png";

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        } catch (IOException e) {
            GameLogger.error("Error loading blubbery image: " + e.getMessage());
        } catch (NullPointerException e) {
            GameLogger.error("bluberry.png not found in resources: " + e.getMessage());
        }
    }

    /**
     * Returns the number of health units the berry restores when used.
     *
     * @return health regeneration value
     */
    public float getHealAmount() {
        return healAmount;
    }

    public ItemType getType() {
        return ItemType.HEALING;
    }
}
