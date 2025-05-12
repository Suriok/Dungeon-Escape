package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * The {@code leather_boots} class represents leather boots,
 * which are equipped on the player's feet and provide basic protection.
 * <p>
 * The defense value is 1 point.
 * </p>
 */

public class leather_boots extends GameObject implements Armor {
    private final float defensAmount;

    /**
     * Constructs a new {@code leather_boots} armor piece.
     * <p>
     * Initializes the item name, defense amount, and loads the corresponding image resource.
     * </p>
     */
    public leather_boots() {
        name = "leather_boots";
        defensAmount = 1;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_boots.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the defense amount provided by this armor piece.
     *
     * @return the defense value of the leather boots
     */
    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}