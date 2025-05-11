package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Třída {@code Item_Blubbery} reprezentuje bobule (blubbery),
 * což je spotřební léčivý předmět, který hráči obnovuje malé množství zdraví.
 * <p>
 * Tento předmět dědí z {@link GameObject}, a proto jej lze přidat do inventáře
 * a vykreslit ve hře pomocí obrázku.
 * </p>
 */
public class Item_Blubbery extends GameObject {

    /**
     * Počet jednotek zdraví, které tento předmět obnoví při použití.
     */
    private final float healAmount;

    /**
     * Konstruktor nastaví název předmětu, jeho léčivý účinek a načte obrázek z resource složky.
     */
    public Item_Blubbery() {
        name = "blubbery";
        healAmount = 1f;
        try {
            image = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(
                            "/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/bluberry.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vrací počet jednotek zdraví, které bobule při použití obnoví.
     *
     * @return hodnota regenerace zdraví
     */
    public float getHealAmount() {
        return healAmount;
    }
}
