package cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * The {@code Iron_sword} class represents an iron sword
 * that the player can find and use as a weapon.
 * <p>
 * This class implements the {@link Weapon} interface, thus providing attack strength,
 * and also inherits from {@link GameObject}, allowing it to exist in the world as an object.
 * </p>
 */
public class Iron_sword extends GameObject implements Weapon {

    /**
     * The attack value that the iron sword inflicts.
     */
    private final int attack;

    /**
     * Creates a new iron sword with the specified attack strength.
     * <p>
     * During construction, the sword's image is also loaded from the resources folder.
     * </p>
     *
     * @param attackValue the attack value of the weapon
     */
    public Iron_sword(int attackValue) {
        this.attack = attackValue;
        name = "iron_sword";

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/sword/Sword.png")));
        } catch (IOException e) {
            GameLogger.error("Error loading image for iron_sword: " + e.getMessage());
        }
    }

    /**
     * Returns the attack strength of this weapon.
     *
     * @return the attack value
     */
    @Override
    public int getAttack() {
        return attack;
    }
}