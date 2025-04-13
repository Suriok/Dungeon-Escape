package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import javax.imageio.ImageIO;
import java.util.Objects;

public class Object_key extends GameObject{
    public Object_key() {
        name = "Key";
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/golden_key.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
