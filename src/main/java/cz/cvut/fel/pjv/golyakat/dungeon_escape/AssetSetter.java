package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_key;

public class AssetSetter {
    gamePanel gp;

    public AssetSetter(gamePanel gp) {
        this.gp = gp;
    }

    public void setObg(){
        gp.obj[0] = new Object_Small_Chest();
        gp.obj[0].worldX = 15 * gp.tileSize;
        gp.obj[0].worldY = 21 * gp.tileSize;

        gp.obj[2] = new Object_DoorSide();
        gp.obj[2].worldX = 20 * gp.tileSize;
        gp.obj[2].worldY = 22 * gp.tileSize;

        gp.obj[3] = new Object_DoorFront();
        gp.obj[3].worldX = 32 * gp.tileSize;
        gp.obj[3].worldY = 24 * gp.tileSize;

        gp.obj[4] = new Object_DoorFront();
        gp.obj[4].worldX = 28 * gp.tileSize;
        gp.obj[4].worldY = 20 * gp.tileSize;

        gp.obj[5] = new Object_DoorFront();
        gp.obj[5].worldX = 38 * gp.tileSize;
        gp.obj[5].worldY = 20 * gp.tileSize;


    }
}
