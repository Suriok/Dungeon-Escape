package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Třída {@code Item_HealthePotion} reprezentuje léčivý předmět – lektvar zdraví,
 * který může hráč použít k obnovení určitého počtu životů.
 * <p>
 * Tato položka je vizuálně reprezentována ikonou a její účinek je pevně daný.
 * </p>
 */
public class Item_HealthePotion extends GameObject {

    /**
     * Množství zdraví (v jednotkách), které lektvar obnoví při použití.
     */
    private final float healAmount;

    /**
     * Vytvoří nový léčivý lektvar a nastaví jeho název, účinek a obrázek.
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
     * Vrací množství zdraví, které lektvar obnoví při použití.
     *
     * @return počet jednotek zdraví
     */
    public float getHealAmount() {
        return healAmount;
    }
}
