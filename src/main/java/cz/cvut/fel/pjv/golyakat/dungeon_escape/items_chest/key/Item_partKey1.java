package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
/**
 * Třída {@code Item_partKey1} reprezentuje první část stříbrného klíče,
 * kterou hráč musí najít pro pozdější vytvoření celého klíče {@link Item_SilverKey}.
 * <p>
 * Tato část klíče je vizuálně zobrazena ikonou a lze ji vložit do craftingového stolu.
 * </p>
 */


public class Item_partKey1 extends GameObject {
    public Item_partKey1() {
        name = "Key1";
        try {
            BufferedImage tempImage = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key_1.png"));
            if (tempImage == null) {
                System.err.println("Failed to load silver_key_1.png for Item_partKey1");
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