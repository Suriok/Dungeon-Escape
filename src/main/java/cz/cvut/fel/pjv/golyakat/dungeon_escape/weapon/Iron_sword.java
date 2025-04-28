package cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.Weapon;

import javax.imageio.ImageIO;
import java.util.Objects;

public class Iron_sword extends GameObject implements Weapon {
    private final int attack;

    public Iron_sword(float attack, int attackValue) {
        this.attack = attackValue;
        name = "iron_sword";
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/sword/Sword.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getAttack() {
        return attack;
    }
}