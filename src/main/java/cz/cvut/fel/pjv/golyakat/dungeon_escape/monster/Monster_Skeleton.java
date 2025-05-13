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
 * The {@code Monster_Skeleton} class represents an enemy unit of type skeleton.
 * <p>
 * The skeleton moves around the map, detects the player within range and attacks when approached.
 * Contains logic for movement, collisions, attacks, and basic animations.
 * </p>
 */
public class Monster_Skeleton extends Entity {

    /** Reference to the game panel that contains the map and player. */
    private gamePanel gp;

    /** Counter that determines when it can change direction or start a new action. */
    public int actionLockCounter = 0;

    /** Maximum player detection range (in pixels). */
    private static final int DETECTION_RANGE = 5 * 48;

    /** Distance at which the monster can attack. */
    private static final int ATTACK_RANGE = 32;

    /** Number of frames that must pass between two attacks. */
    private static final int ATTACK_COOLDOWN = 60;

    /** Counter since the last attack (for cooldown). */
    private int attackCounter = 0;

    /** Damage dealt by the attack. */
    private static final int ATTACK_DAMAGE = 5;

    /**
     * Creates a skeleton instance with default settings and loads graphics.
     *
     * @param gp game panel
     */
    public Monster_Skeleton(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Skeleton";
        speed = 2;
        maxLife = 4;
        life = maxLife;
        direction = "down";

        solidArea = new Rectangle(3, 10, 20, 30);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    /**
     * Loads skeleton sprite images for all movement directions.
     */
    public void getImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_up1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_up2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_down1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_down2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_right1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_right2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_left1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_left2.png")));
        } catch (Exception e) {
            GameLogger.error("Error loading skeleton sprite images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Determines the skeleton's next action - tracks the player or moves randomly.
     */
    public void setAction() {
        actionLockCounter++;

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= DETECTION_RANGE && !isDead) {
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? "right" : "left";
            } else {
                direction = dy > 0 ? "down" : "up";
            }
        } else {
            if (actionLockCounter >= 120) {
                Random random = new Random();
                int i = random.nextInt(100) + 1;
                direction = switch (i / 25) {
                    case 0 -> "up";
                    case 1 -> "down";
                    case 2 -> "left";
                    default -> "right";
                };
                actionLockCounter = 0;
            }
        }
    }

    /**
     * Updates the skeleton's behavior: movement, collision, attack, animation, and death effect.
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
            case "up" -> worldY -= speed;
            case "down" -> worldY += speed;
            case "left" -> worldX -= speed;
            case "right" -> worldX += speed;
        }

        collisionOn = false;
        gp.collisionChecker.checkTiles(this);
        if (collisionOn) {
            worldX = oldX;
            worldY = oldY;
            actionLockCounter = 120;
        }

        // Attack on player
        attackCounter++;
        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance <= ATTACK_RANGE && attackCounter >= ATTACK_COOLDOWN) {
            gp.player.receiveDamage(ATTACK_DAMAGE);
            attackCounter = 0;
            GameLogger.info(name + " attacked the player! Player HP: " + gp.player.life);
        }

        // Animation
        spriteCounter++;
        if (spriteCounter > 15) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }

        // Death
        if (life <= 0) {
            isDead = true;
            fadeAlpha = 1.0f;
            fadeCounter = 0;
        }
    }

    /**
     * Renders the skeleton using the current sprite image and direction.
     *
     * @param g2d graphics context
     */
    @Override
    public void draw(Graphics2D g2d) {
        if (isDead && fadeAlpha <= 0) return;

        if (direction == null) direction = "down";

        BufferedImage imageToDraw = switch (direction) {
            case "up" -> (spriteNum == 1) ? up1 : up2;
            case "down" -> (spriteNum == 1) ? down1 : down2;
            case "left" -> (spriteNum == 1) ? left1 : left2;
            case "right" -> (spriteNum == 1) ? right1 : right2;
            default -> down1;
        };

        this.image = imageToDraw;

        if (!isDead) {
            super.draw(g2d);
        }
    }
}
