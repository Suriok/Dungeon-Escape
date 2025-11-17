package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.boss;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster;
import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.AlphaComposite;

/**
 * Represents the boss monster "Eye" in the dungeon.
 * <p>
 * This boss appears in a specific level and drops a key upon death,
 * allowing the player to progress to the next stage.
 * </p>
 */
public class Boss_Eye extends Monster {

    /**
     * Indicates whether the boss has already dropped the key after death.
     */
    private boolean keyDropped = false;

    /**
     * Constructs the {@code Boss_Eye} boss monster with specified parameters.
     *
     * @param gp reference to the main game panel
     */
    public Boss_Eye(gamePanel gp) {
        super(gp, "Boss_Eye", 2, 15, 7,
                new Rectangle(3, 10, 20, 50)); // sets the collision box
    }

    /**
     * Loads all directional sprite images used for the boss.
     * <p>
     * Since the boss is static in appearance regardless of direction,
     * all directional sprites are assigned the same images.
     * </p>
     */
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

    /**
     * Defines the behavior when the boss is defeated.
     * <p>
     * Upon death, the boss drops a key which is added directly
     * to the player's inventory. The key is only dropped once.
     * </p>
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

    @Override
    public void draw(Graphics2D g2d) {

        BufferedImage image = null;

        if (spriteNum == 1) {
            image = up1;
        } else {
            image = up2;
        }

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        int drawWidth = gp.tileSize + 24;
        int drawHeight = gp.tileSize + 24;

        int drawX = screenX - 1;
        int drawY = screenY - 1;


        if (image != null) {
            g2d.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
        }
    }
}
