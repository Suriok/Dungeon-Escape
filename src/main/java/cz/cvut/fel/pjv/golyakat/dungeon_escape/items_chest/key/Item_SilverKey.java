package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ItemType;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * The {@code Item_SilverKey} class represents a complete silver key,
 * which can be crafted by combining three parts: {@link Item_partKey1}, {@link Item_partKey2}, and {@link Item_partKey3}.
 * <p>
 * This key is used to unlock special doors in higher levels of the game.
 * </p>
 */
public class Item_SilverKey extends GameObject {

    /**
     * Constructs a new SilverKey item.
     * <p>
     * Sets the item name and attempts to load its image from the resource path
     * <code>/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key.png</code>.
     * If the image cannot be found or loaded, logs an error via {@link GameLogger} and
     * falls back to a 1×1 transparent placeholder.
     * </p>
     */
    public Item_SilverKey() {
        name = "SilverKey";
        try {
            BufferedImage img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key.png")));
            image = (img != null) ? img : new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        } catch (Exception ex) {
            GameLogger.error("Cannot load SilverKey texture: " + ex.getMessage());
            image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }

    public ItemType getType() {
        if (name.equals("Key1") || name.equals("Key2") || name.equals("Key3")) {
            return ItemType.KEY_PART;
        }
        return ItemType.KEY;
    }
}
