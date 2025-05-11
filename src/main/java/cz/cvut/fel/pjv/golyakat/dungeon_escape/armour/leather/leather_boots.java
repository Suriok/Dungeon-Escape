package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Třída {@code leather_boots} reprezentuje kožené boty,
 * které se nasazují na nohy hráče a poskytují základní ochranu.
 * <p>
 * Obranná hodnota je 1 bod.
 * </p>
 */

public class leather_boots extends GameObject implements Armor {
    private final float defensAmount;

    public leather_boots() {
        name = "leather_boots";
        defensAmount = 1;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_boots.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}