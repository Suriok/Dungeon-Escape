package cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.util.Objects;

public class Item_Key extends GameObject {
    public Item_Key() {
        name = "Key";
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/items_in_chest/golden_key.png")));
        } catch (Exception e) {
            System.err.println("Error loading key image: " + e.getMessage());
            e.printStackTrace();
        }
    }
}