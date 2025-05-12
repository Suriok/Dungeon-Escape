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
 * The {@code Monster_Slime} class represents an enemy monster of type Slime.
 * <p>
 * Contains logic for random movement or player tracking, player attacks,
 * collision detection, and simple movement animation.
 * </p>
 */
public class Monster_Slime extends Entity {

    /** Reference to the main game panel. */
    private gamePanel gp;

    /** Counter to determine when to change action or movement direction. */
    public int actionLockCounter = 0;

    /** Maximum distance at which the slime detects the player (in pixels). */
    private static final int DETECTION_RANGE = 5 * 48;

    /** Maximum attack range on the player. */
    private static final int ATTACK_RANGE = 32;

    /** Number of frames to wait between attacks (cooldown). */
    private static final int ATTACK_COOLDOWN = 60;

    /** Counter for attack cooldown. */
    private int attackCounter = 0;

    /** Amount of damage the slime deals to the player. */
    private static final int ATTACK_DAMAGE = 4;

    /**
     * Creates a new instance of Slime with default settings.
     *
     * @param gp game panel to which the slime belongs
     */
    public Monster_Slime(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Slime";
        speed = 1;
        maxLife = 2;
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
     * Loads images (sprites) for different movement directions and Slime animations.
     */
    public void getImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_2.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_3.png")));
            down1 = up1;
            down2 = up2;
            left1 = up1;
            left2 = up2;
            right1 = up1;
            right2 = up2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Determines the monster's behavior based on distance from the player.
     * <ul>
     *     <li>If the player is nearby, the slime follows them.</li>
     *     <li>Otherwise, it moves randomly.</li>
     * </ul>
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
                if (i <= 25) direction = "up";
                else if (i <= 50) direction = "down";
                else if (i <= 75) direction = "left";
                else direction = "right";
                actionLockCounter = 0;
            }
        }
    }

    /**
     * Updates the Slime's state each frame: movement, collision, attack, animation, and death check.
     */
    public void update() {
        if (isDead) {
            super.update(); // Fade effect
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
            GameLogger.info(name + " attacked the player! HP: " + gp.player.life);
        }

        // Animation
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
     * Renders the Slime on screen using the correct image based on direction and animation phase.
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
