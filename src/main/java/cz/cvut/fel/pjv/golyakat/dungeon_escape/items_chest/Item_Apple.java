package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Třída {@code Item_Apple} reprezentuje léčivý předmět – jablko,
 * který obnovuje malé množství zdraví (1/4 srdce, tedy 0.5 HP).
 * <p>
 * Jablko je graficky reprezentováno ikonou a slouží jako spotřební položka
 * v hráčově inventáři. Dědí od {@link GameObject}, což umožňuje jeho použití
 * v herním světě i inventáři.
 * </p>
 */
public class Item_Apple extends GameObject {

    /**
     * Množství zdraví, které jablko při použití obnoví.
     */
    private final float healAmount;

    /**
     * Vytvoří nové jablko, nastaví jeho název, účinek a načte obrázek z resource složky.
     */
    public Item_Apple() {
        name = "Apple";
        healAmount = 0.5f; // odpovídá 1/4 srdce (1 srdce = 2 HP)

        try {
            image = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(
                            "/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/apple.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vrací počet jednotek zdraví, které jablko při použití obnoví.
     *
     * @return hodnota regenerace zdraví (např. 0.5)
     */
    public float getHealAmount() {
        return healAmount;
    }
}
