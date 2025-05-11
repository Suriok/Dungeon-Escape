package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour;

/**
 * Rozhraní {@code Armor} definuje základní chování každého typu brnění ve hře.
 * <p>
 * Každý objekt, který implementuje toto rozhraní, musí poskytovat:
 * <ul>
 *     <li>Úroveň obrany, kterou zvyšuje hráči</li>
 * </ul>
 */
public interface Armor {

    /**
     * Vrací obrannou hodnotu, kterou toto brnění poskytuje hráči.
     *
     * @return hodnota obrany jako desetinné číslo
     */
    float getDefensAmount();

}
