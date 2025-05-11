package cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon;

/**
 * Rozhraní {@code Weapon} definuje základní vlastnosti všech zbraní ve hře.
 * <p>
 * Každá třída, která toto rozhraní implementuje (např. meče, sekery apod.),
 * musí poskytnout metodu {@link #getAttack()}, která vrací útočnou sílu dané zbraně.
 * </p>
 */
public interface Weapon {

    /**
     * Vrací hodnotu útoku, kterou tato zbraň způsobuje.
     *
     * @return číselná hodnota útoku
     */
    int getAttack();
}
