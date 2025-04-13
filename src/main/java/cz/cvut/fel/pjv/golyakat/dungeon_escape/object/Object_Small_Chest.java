package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import javax.imageio.ImageIO;
import java.util.Objects;

public class Object_Small_Chest extends GameObject {

    public Object_Small_Chest() {
        name = "small_chest";
        try{
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/small_chest.png")));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
