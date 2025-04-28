package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

public class Item_Blubbery extends GameObject {
    private final float healAmount;

    public Item_Blubbery() {
        name = "blubbery";
        healAmount = 1f;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/bluberry.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для использования яблока (восстановление здоровья)
    public float getHealAmount() {
        return healAmount;
    }
}
