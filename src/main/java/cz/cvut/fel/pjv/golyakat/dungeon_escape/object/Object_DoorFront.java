package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import javax.imageio.ImageIO;
import java.util.Objects;

public class Object_DoorFront extends GameObject {

    public Object_DoorFront() {
        name = "DoorFront";
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_front.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

