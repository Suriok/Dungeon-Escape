package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * Třída {@code Item_partKey2} reprezentuje druhou část stříbrného klíče,
 * která se sbírá stejně jako ostatní části a spojuje se do {@link Item_SilverKey}.
 * <p>
 * Je nezbytná pro sestavení kompletního klíče v systému craftingu.
 * </p>
 */

public class Item_partKey2 extends GameObject {
    public Item_partKey2() {
        name = "Key2";
        try {
            BufferedImage tempImage = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key_2.png"));
            if (tempImage == null) {
                System.err.println("Failed to load silver_key_2.png for Item_partKey2");
                // Create a 1x1 transparent pixel as fallback
                tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            image = tempImage; // Assign to the image field
        } catch (Exception e) {
            System.err.println("Error loading Item_partKey2 image: " + e.getMessage());
            e.printStackTrace();

        }
    }
}