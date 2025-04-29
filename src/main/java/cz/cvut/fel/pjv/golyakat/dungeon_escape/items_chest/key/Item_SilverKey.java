package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * Готовый серебряный ключ, собираемый из трёх частей.
 */
public class Item_SilverKey extends GameObject {

    public Item_SilverKey() {
        name = "SilverKey";
        try {
            BufferedImage img = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key.png"));
            image = (img != null) ? img : new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        } catch (Exception ex) {
            System.err.println("Cannot load SilverKey texture: " + ex.getMessage());
            image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }
}
