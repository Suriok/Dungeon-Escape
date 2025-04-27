package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

public class iron_pants extends GameObject {
    private final float defensAmount;

    public iron_pants() {
        name = "iron_pants";
        defensAmount = 2;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/iron_pants.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
