package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import javax.imageio.ImageIO;
import java.util.Objects;

public class Object_DoorSide extends GameObject {

    public Object_DoorSide() {
        name = "DoorSide";
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/sec.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

