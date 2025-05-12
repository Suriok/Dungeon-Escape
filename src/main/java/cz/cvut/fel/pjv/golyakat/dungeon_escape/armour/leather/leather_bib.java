package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * The {@code leather_bib} class represents a leather chest piece,
 * which is equipped on the player's torso and provides higher level of protection.
 * <p>
 * The defense value of the chest armor is 4 points. This is the most important piece of equipment.
 * </p>
 */

public class leather_bib extends GameObject implements Armor {
    private final float defensAmount;

    public leather_bib() {
        name = "leather_bib";
        defensAmount = 4;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_bib.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the defense amount provided by this armor piece.
     *
     * @return the defense value of the leather bib
     */
    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}