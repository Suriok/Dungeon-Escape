package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
/**
 * Třída {@code Item_partKey3} reprezentuje třetí a poslední část stříbrného klíče,
 * která je vyžadována pro úspěšné sestavení klíče {@link Item_SilverKey}.
 * <p>
 * Bez této části není možné odemknout speciální dveře.
 * </p>
 */

public class Item_partKey3 extends GameObject {
    public Item_partKey3() {
        name = "Key3";
        try {
            BufferedImage tempImage = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key_3.png"));
            if (tempImage == null) {
                System.err.println("Failed to load silver_key_3.png for Item_partKey3");
                // Create a 1x1 transparent pixel as fallback
                tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            image = tempImage; // Assign to the image field
        } catch (Exception e) {
            System.err.println("Error loading Item_partKey1 image: " + e.getMessage());
            e.printStackTrace();
        }
    }
}