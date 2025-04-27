package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

public class leather_bib extends GameObject {
    private final float defensAmount;

    public  leather_bib() {
        name = " leather_bib";
        defensAmount = 4;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_bib.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
