package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

/**
 * The {@code Monster_Zombie} class represents an enemy zombie entity.
 * <p>
 * Contains logic for movement, attack, player detection, animations, and death visual effects.
 * Inherits from the {@link Entity} class, gaining basic entity capabilities in the game.
 * </p>
 */
public class Monster_Zombie extends Entity {

    /** Reference to the main game panel. */
    private gamePanel gp;

    /** Action counter for controlling direction changes. */
    public int actionLockCounter = 0;

    /**
     * Maximum distance at which the zombie can detect the player.
     * Units correspond to pixels (5 tile = 240 px).
     */
    private static final int DETECTION_RANGE = 5 * 48;

    /** Distance at which the zombie can attack the player. */
    private static final int ATTACK_RANGE = 32;

    /** Time between attacks in frames (at 60 FPS = 1s). */
    private static final int ATTACK_COOLDOWN = 60;

    /** Counter for attack cooldown. */
    private int attackCounter = 4;

    /** Amount of damage the zombie deals to the player. */
    private static final int ATTACK_DAMAGE = 5;

    /**
     * Constructor creates a new zombie and sets default attributes including images and collision.
     *
     * @param gp main game panel
     */
    public Monster_Zombie(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Zombie";
        speed = 2;
        maxLife = 5;
        life = maxLife;
        direction = "down";

        solidArea = new Rectangle();
        solidArea.x = 3;
        solidArea.y = 10;
        solidArea.width = 20;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    /**
     * Loads zombie sprite images for movement animations in all directions.
     */
    public void getImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_up1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_up2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_front1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_front2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_left1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_left2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_rigth1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_right2.png")));
        } catch (Exception e) {
            GameLogger.error("Error loading zombie sprite images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets the zombie's action - either random movement or chasing the player
     * if within detection range.
     */
    public void setAction() {
        actionLockCounter++;

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= DETECTION_RANGE && !isDead) {
            // Direction towards player
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? "right" : "left";
            } else {
                direction = dy > 0 ? "down" : "up";
            }
        } else {
            // Random movement
            if (actionLockCounter >= 120) {
                Random random = new Random();
                int i = random.nextInt(100) + 1;
                if (i <= 25) direction = "up";
                else if (i <= 50) direction = "down";
                else if (i <= 75) direction = "left";
                else direction = "right";
                actionLockCounter = 0;
            }
        }
    }

    /**
     * Updates the zombie's state - movement, collision, attack, and animations.
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

        if (life <= 0) {
            isDead = true;
            fadeAlpha = 1.0f;
            fadeCounter = 0;
        }
    }

    /**
     * Renders the zombie using the correct sprite based on direction and animation phase.
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

        if (!isDead) {
            super.draw(g2d);
        }
    }
}
