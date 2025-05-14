package cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ItemType;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * The {@code Emerald_sword} class represents a specific type of sword – the emerald sword,
 * which provides players with a certain attack strength.
 * <p>
 * The sword implements the {@link Weapon} interface and extends {@link GameObject}, allowing it
 * to exist in the world as a game object while also providing combat properties.
 * </p>
 */
public class Emerald_sword extends GameObject implements Weapon {

    /**
     * The attack value that this sword inflicts.
     */
    private final int attack;

    /**
     * Creates a new emerald sword with the specified attack value.
     * <p>
     * During construction, the corresponding texture is also loaded from the resource package.
     * </p>
     *
     * @param attackValue the attack value that the sword inflicts
     */
    public Emerald_sword(int attackValue) {
        this.attack = attackValue;
        name = "emerald_sword";

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/sword/emerald_sword.png")));
        } catch (IOException e) {
            GameLogger.error("Error loading image for emerald_sword: " + e.getMessage());
        }
    }

    /**
     * Returns the attack value of the given sword.
     *
     * @return the numerical attack value
     */
    @Override
    public int getAttack() {
        return attack;
    }

    public ItemType getType() {
        return ItemType.WEAPON;
    }
}