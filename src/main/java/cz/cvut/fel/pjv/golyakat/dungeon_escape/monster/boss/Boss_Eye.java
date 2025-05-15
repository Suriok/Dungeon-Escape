package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.boss;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

public class Boss_Eye extends Monster {

    private boolean keyDropped = false;

    public Boss_Eye(gamePanel gp) {
        super(gp, "Boss_Eye", 2, 15,7,
                new Rectangle(3, 10, 20, 50));// collision box
    }

    @Override
    protected void loadImages() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(
                            "/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Eye.png")));
            up2 = ImageIO.read(Objects.requireNonNull(
                    getClass().getResourceAsStream(
                            "/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/eye_1.png")));
            down1 = up1;   down2 = up2;
            left1 = up1;   left2 = up2;
            right1 = up1;  right2 = up2;
        } catch (Exception e) {
            GameLogger.error("Boss_Eye sprites: " + e.getMessage());
        }
    }

    @Override
    protected void onDeath() {
        if (keyDropped) return;


        ChestInventoryManager.ItemData key =
                new ChestInventoryManager.ItemData("Key", 1);

        gp.player.addItem(key);
        keyDropped = true;

        GameLogger.info(name + " dropped a key!");
    }

}
