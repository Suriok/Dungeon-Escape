package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

public class Item_HealthePotion extends GameObject {
    private final float healAmount;

    public Item_HealthePotion() {
        name = "potion";
        healAmount = 2;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/healing.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для использования яблока (восстановление здоровья)
    public float getHealAmount() {
        return healAmount;
    }
}
