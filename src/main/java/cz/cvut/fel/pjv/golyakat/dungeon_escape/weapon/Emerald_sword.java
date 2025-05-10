package cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.Weapon;

import javax.imageio.ImageIO;
import java.io.IOException;

public class Emerald_sword extends GameObject implements Weapon {
    private final int attack;

    public Emerald_sword(int attackValue) {
        this.attack = attackValue;
        name = "emerald_sword";
        try {
            image = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/sword/emerald_sword.png"));
        } catch (IOException e) {
            System.err.println("Error loading image for iron_sword: " + e.getMessage());
        }
    }

    @Override
    public int getAttack() {
        return attack;
    }
}