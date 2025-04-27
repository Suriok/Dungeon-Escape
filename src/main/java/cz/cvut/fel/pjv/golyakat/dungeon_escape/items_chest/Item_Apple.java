package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

public class Item_Apple extends GameObject {
    private final float healAmount; // Количество восстанавливаемого здоровья (в единицах HP)

    public Item_Apple() {
        name = "Apple";
        healAmount = 0.5f; // Четверть сердца (1 сердце = 2 HP, значит четверть = 0.5 HP)
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/apple.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для использования яблока (восстановление здоровья)
    public float getHealAmount() {
        return healAmount;
    }
}