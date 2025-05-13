package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour;

/**
 * The {@code Armor} interface defines the basic behavior of each armor type in the game.
 * <p>
 * Each object implementing this interface must provide:
 * <ul>
 *     <li>Defense level that it increases for the player</li>
 * </ul>
 */
public interface Armor {

    /**
     * Returns the defense value that this armor provides to the player.
     *
     * @return defense value as a decimal number
     */
    float getDefensAmount();

}
