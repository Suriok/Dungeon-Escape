package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * The {@code Item_HealthePotion} class represents a healing item – a health potion,
 * which the player can use to restore a certain amount of health.
 * <p>
 * This item is visually represented by an icon and its effect is fixed.
 * </p>
 */
public class Item_HealthePotion extends GameObject {

    /**
     * Amount of health (in units) that the potion restores when used.
     */
    private final float healAmount;

    /**
     * Creates a new healing potion and sets its name, effect, and image.
     */
    public Item_HealthePotion() {
        name = "potion";
        healAmount = 2;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/healing.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the amount of health that the potion restores when used.
     *
     * @return number of health units
     */
    public float getHealAmount() {
        return healAmount;
    }
}
