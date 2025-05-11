package cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.Weapon;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Třída {@code Iron_sword} reprezentuje železný meč,
 * který hráč může najít a použít jako zbraň.
 * <p>
 * Tato třída implementuje rozhraní {@link Weapon}, takže poskytuje útočnou sílu,
 * a zároveň dědí od {@link GameObject}, což jí umožňuje existovat ve světě jako objekt.
 * </p>
 */
public class Iron_sword extends GameObject implements Weapon {

    /**
     * Hodnota útoku, kterou železný meč uděluje.
     */
    private final int attack;

    /**
     * Vytváří nový železný meč s určenou silou útoku.
     * <p>
     * Při konstrukci se také načítá obrázek meče ze složky resources.
     * </p>
     *
     * @param attackValue útočná hodnota zbraně
     */
    public Iron_sword(int attackValue) {
        this.attack = attackValue;
        name = "iron_sword";

        try {
            image = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/sword/Sword.png"));
        } catch (IOException e) {
            GameLogger.error("Error loading image for iron_sword: " + e.getMessage());
        }
    }

    /**
     * Vrací útočnou sílu této zbraně.
     *
     * @return hodnota útoku
     */
    @Override
    public int getAttack() {
        return attack;
    }
}
