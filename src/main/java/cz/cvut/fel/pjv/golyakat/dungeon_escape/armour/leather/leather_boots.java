package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

public class leather_boots extends GameObject {
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
}
