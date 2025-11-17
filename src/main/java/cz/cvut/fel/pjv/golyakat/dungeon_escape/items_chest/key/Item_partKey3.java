package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * The {@code Item_partKey3} class represents the third and final part of the silver key,
 * which is required for successful assembly of the key {@link Item_SilverKey}.
 * <p>
 * Without this part, it is not possible to unlock special doors.
 * </p>
 */

public class Item_partKey3 extends GameObject {
    /**
     * Constructs a new key fragment item ("Key3").
     * <p>
     * Sets the item name and attempts to load its image from the resource path
     * <code>/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key_3.png</code>.
     * If the image cannot be found or loaded, logs an error via {@link GameLogger} and
     * falls back to a 1×1 transparent pixel.
     * </p>
     */
    public Item_partKey3() {
        name = "Key3";
        try {
            BufferedImage tempImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/key_part/silver_key_3.png")));
            if (tempImage == null) {
                GameLogger.error("Failed to load silver_key_3.png for Item_partKey3");
                image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

            } else {
                int newWidth = 48;
                int newHeight = 48;

                BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(tempImage, 0, 0, newWidth, newHeight, null);
                g2d.dispose();

                image = scaledImage;

            }
        } catch (Exception e) {
            GameLogger.error("Error loading Item_partKey3 image: " + e.getMessage());
            image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        }
    }
}