package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

public class iron_helmet extends GameObject {
    private final float defensAmount;

    public iron_helmet() {
        name = "iron_helmet";
        defensAmount = 3;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/iron_helmet.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
