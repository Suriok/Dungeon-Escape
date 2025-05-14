package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ItemType;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * The {@code Item_partKey1} class represents the first part of the silver key,
 * which the player must find for later creation of the complete key {@link Item_SilverKey}.
 * <p>
 * This key part is visually represented by an icon and can be placed in the crafting table.
 * </p>
 */


public class Item_partKey1 extends GameObject {
    /**
     * Constructs a new key fragment item ("Key1").
     * <p>
     * Sets the item name and attempts to load its image from the resource path
     * <code>/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key_1.png</code>.
     * If the image cannot be found or loaded, logs an error via {@link GameLogger} and
     * falls back to a 1×1 transparent pixel.
     * </p>
     */
    public Item_partKey1() {
        name = "Key1";
        try {
            BufferedImage tempImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key_1.png")));
            if (tempImage == null) {
                GameLogger.error("Failed to load silver_key_1.png for Item_partKey1");
                // Create a 1x1 transparent pixel as fallback
                tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            image = tempImage; // Assign to the image field
        } catch (Exception e) {
            GameLogger.error("Error loading Item_partKey1 image: " + e.getMessage());
        }
    }

}