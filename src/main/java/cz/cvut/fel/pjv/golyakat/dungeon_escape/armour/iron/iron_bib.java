package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

public class iron_bib extends GameObject {
    private final float defensAmount;

    public iron_bib() {
        name = "iron_bib";
        defensAmount = 6;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/iron_bib.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
