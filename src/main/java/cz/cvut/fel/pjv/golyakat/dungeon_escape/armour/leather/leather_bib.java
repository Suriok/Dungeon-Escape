package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Třída {@code leather_bib} reprezentuje kožený hrudní plát,
 * který se nasazuje na trup hráče a poskytuje vyšší úroveň ochrany.
 * <p>
 * Obranná hodnota hrudního brnění je 4 body. Jedná se o nejdůležitější kus výbavy.
 * </p>
 */

public class leather_bib extends GameObject implements Armor {
    private final float defensAmount;

    public leather_bib() {
        name = "leather_bib";
        defensAmount = 4;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_bib.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}