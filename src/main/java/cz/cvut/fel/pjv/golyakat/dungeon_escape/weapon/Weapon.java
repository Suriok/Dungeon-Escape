package cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon;

/**
 * The {@code Weapon} interface defines the basic properties of all weapons in the game.
 * <p>
 * Every class that implements this interface (e.g., swords, axes, etc.)
 * must provide the {@link #getAttack()} method, which returns the attack strength of the given weapon.
 * </p>
 */
public interface Weapon {

    /**
     * Returns the attack value that this weapon inflicts.
     *
     * @return the numerical attack value
     */
    int getAttack();
}