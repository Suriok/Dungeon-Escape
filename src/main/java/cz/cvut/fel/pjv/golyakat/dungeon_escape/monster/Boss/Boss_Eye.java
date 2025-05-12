package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Key; // Import the correct Item_Key class

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * The {@code Boss_Eye} class represents one of the main bosses in the game - the Eye.
 * <p>
 * This boss has higher health, deals strong damage, and tracks the player within a certain range.
 * When defeated, it drops a key necessary for further progress in the game.
 * </p>
 */
public class Boss_Eye extends Entity {

    /** Reference to the main game panel. */
    private gamePanel gp;

    /** Counter preventing frequent direction changes. */
    public int actionLockCounter = 0;

    /** Distance at which the boss detects the player. */
    private static final int DETECTION_RANGE = 5 * 48;

    /** Distance at which the boss attacks the player. */
    private static final int ATTACK_RANGE = 48;

    /** Number of frames that must pass between attacks (cooldown). */
    private static final int ATTACK_COOLDOWN = 60;

    /** Internal counter for attack cooldown. */
    private int attackCounter = 0;

    /** Damage dealt by the attack. */
    private static final int ATTACK_DAMAGE = 15;

    /** Flag indicating that the boss has already dropped a key (prevents multiple drops). */
    private boolean hasDroppedKey = false;

    /**
     * Constructor initializes the boss, sets health, speed, images, and collisions.
     *
     * @param gp reference to {@link gamePanel}
     */
    public Boss_Eye(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Boss_Eye";
        speed = 2;
        maxLife = 15;
        life = maxLife;
        direction = "down";

        solidArea = new Rectangle(3, 10, 20, 50);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    /**
     * Loads sprite images for different boss movement directions.
     */
    public void getImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Eye.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/eye_1.png")));
            down1 = up1;
            down2 = up2;
            left1 = up1;
            left2 = up2;
            right1 = up1;
            right2 = up2;
        } catch (Exception e) {
            GameLogger.error("Error loading Eye boss sprite images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Determines the boss's direction based on player position or randomly if the player is out of range.
     */
    public void setAction() {
        actionLockCounter++;

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= DETECTION_RANGE && !isDead) {
            direction = (Math.abs(dx) > Math.abs(dy)) ? (dx > 0 ? "right" : "left") : (dy > 0 ? "down" : "up");
        } else if (actionLockCounter >= 120) {
            Random random = new Random();
            int i = random.nextInt(100) + 1;
            direction = (i <= 25) ? "up" : (i <= 50) ? "down" : (i <= 75) ? "left" : "right";
            actionLockCounter = 0;
        }
    }

    /**
     * Main update method - called every game frame.
     * Includes movement, attack, collisions, animations, and boss death.
     */
    public void update() {
        if (isDead) {
            super.update(); // fade effect
            return;
        }

        setAction();
        if (direction == null) direction = "down";

        int oldX = worldX;
        int oldY = worldY;

        switch (direction) {
            case "up"    -> worldY -= speed;
            case "down"  -> worldY += speed;
            case "left"  -> worldX -= speed;
            case "right" -> worldX += speed;
        }

        collisionOn = false;
        gp.collisionChecker.checkTiles(this);
        if (collisionOn) {
            worldX = oldX;
            worldY = oldY;
            actionLockCounter = 120;
        }

        attackCounter++;
        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance <= ATTACK_RANGE && attackCounter >= ATTACK_COOLDOWN) {
            gp.player.receiveDamage(ATTACK_DAMAGE);
            attackCounter = 0;
            GameLogger.info(name + " attacked the player! Player HP: " + gp.player.life);
        }

        spriteCounter++;
        if (spriteCounter > 15) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }

        if (life <= 0 && !isDead) {
            isDead = true;
            fadeAlpha = 1.0f;
            fadeCounter = 0;

            if (!hasDroppedKey) {
                ChestInventoryManager.ItemData keyItem = new ChestInventoryManager.ItemData("Key", 1);
                keyItem.setItem(new Item_Key());
                gp.player.addItem(keyItem);
                hasDroppedKey = true;
                GameLogger.info(name + " was defeated and dropped a key!");
            }
        }
    }

    /**
     * Renders the Eye boss on screen according to direction and state.
     *
     * @param g2d graphics context
     */
    @Override
    public void draw(Graphics2D g2d) {
        if (isDead && fadeAlpha <= 0) return;

        if (direction == null) direction = "down";

        BufferedImage imageToDraw = switch (direction) {
            case "up"    -> (spriteNum == 1) ? up1 : up2;
            case "down"  -> (spriteNum == 1) ? down1 : down2;
            case "left"  -> (spriteNum == 1) ? left1 : left2;
            case "right" -> (spriteNum == 1) ? right1 : right2;
            default      -> down1;
        };

        this.image = imageToDraw;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        int scaledSize = gp.tileSize * 2;

        if (screenX > -scaledSize && screenX < gp.screenWidth &&
                screenY > -scaledSize && screenY < gp.screenHeight && !isDead) {
            g2d.drawImage(image, screenX, screenY, scaledSize, scaledSize, null);
        }
    }
}
