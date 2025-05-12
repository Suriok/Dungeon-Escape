package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * The {@code Item_partKey2} class represents the second part of the silver key,
 * which is collected like other parts and combined into {@link Item_SilverKey}.
 * <p>
 * It is essential for assembling the complete key in the crafting system.
 * </p>
 */

public class Item_partKey2 extends GameObject {
    public Item_partKey2() {
        name = "Key2";
        try {
            BufferedImage tempImage = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key_2.png"));
            if (tempImage == null) {
                GameLogger.error("Failed to load silver_key_2.png for Item_partKey2");
                // Create a 1x1 transparent pixel as fallback
                tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            image = tempImage; // Assign to the image field
        } catch (Exception e) {
            GameLogger.error("Error loading Item_partKey2 image: " + e.getMessage());
            e.printStackTrace();

        }
    }
}