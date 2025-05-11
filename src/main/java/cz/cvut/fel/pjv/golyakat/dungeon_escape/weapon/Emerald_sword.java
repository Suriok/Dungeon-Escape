package cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.Weapon;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Třída {@code Emerald_sword} reprezentuje specifický typ meče – smaragdový meč,
 * který hráči poskytuje určitou útočnou sílu.
 * <p>
 * Meč implementuje rozhraní {@link Weapon} a rozšiřuje {@link GameObject}, takže se
 * může nacházet ve světě jako herní objekt a zároveň poskytovat bojové vlastnosti.
 * </p>
 */
public class Emerald_sword extends GameObject implements Weapon {

    /**
     * Hodnota útoku, kterou tento meč způsobuje.
     */
    private final int attack;

    /**
     * Vytváří nový smaragdový meč s danou útočnou hodnotou.
     * <p>
     * Při konstrukci se zároveň načítá příslušná textura z resource balíčku.
     * </p>
     *
     * @param attackValue hodnota útoku, kterou meč způsobí
     */
    public Emerald_sword(int attackValue) {
        this.attack = attackValue;
        name = "emerald_sword";

        try {
            image = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/sword/emerald_sword.png"));
        } catch (IOException e) {
            GameLogger.error("Error loading image for emerald_sword: " + e.getMessage());
        }
    }

    /**
     * Vrací hodnotu útoku daného meče.
     *
     * @return číselná hodnota útoku
     */
    @Override
    public int getAttack() {
        return attack;
    }
}
