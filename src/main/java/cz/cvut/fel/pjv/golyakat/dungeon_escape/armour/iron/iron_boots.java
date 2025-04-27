package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;


import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

public class iron_boots extends GameObject {
    private final float defensAmount;

    public iron_boots() {
        name = "iron_boots";
        defensAmount = 1.5f;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/iron_boots.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
