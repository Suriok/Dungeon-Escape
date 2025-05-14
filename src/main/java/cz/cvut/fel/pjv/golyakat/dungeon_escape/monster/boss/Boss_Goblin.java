package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.boss;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

/**
 * The {@code Boss_Goblin} class represents the main Goblin-type boss in the game.
 * The Goblin has high HP, tracks the player, and drops a key upon death.
 */
public class Boss_Goblin extends Monster {

    private boolean keyDropped = false;

    public Boss_Goblin(gamePanel gp) {
        super(gp, "Boss Goblin", 2, 6, 5 * 48,
                5,
                new Rectangle(3, 10, 20, 50));// collision box
    }

    /**
     * Loads sprite images for the goblin based on direction and animation.
     */
    @Override
    protected void loadImages() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin_1.png")));

            // All directions use same frames
            down1 = up1;  down2 = up2;
            left1 = up1;  left2 = up2;
            right1 = up1; right2 = up2;
        } catch (Exception e) {
            GameLogger.error("Error loading goblin sprites: " + e.getMessage());
        }
    }

    /**
     * Called once after boss death – drops a key for progression.
     */
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
