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
 * The {@code Boss_Goblin} class represents the main Goblin-type boss in the game.
 * <p>
 * The Goblin combines physical attacks and fireball shooting.
 * Tracks the player, responds to their position, and drops a key upon death for progression.
 * </p>
 *
 * Key features:
 * <ul>
 *   <li>Has movement and attack animations.</li>
 *   <li>Shoots fireballs at range.</li>
 *   <li>Triggers fade-out effect on death and adds a key to the player's inventory.</li>
 * </ul>
 */

public class Boss_Goblin extends Entity {
    /** Reference to the main game panel for access to player, map, and sounds. */
    private gamePanel gp;

    /** Counter that determines when the goblin can change direction. */
    public int actionLockCounter = 0;

    /** Maximum distance at which the goblin detects the player (in pixels). */
    private static final int DETECTION_RANGE = 5 * 48;

    /** Range of the goblin's physical attack (in pixels). */
    private static final int ATTACK_RANGE = 48;

    /** Number of frames between attacks (60 = 1 second). */
    private static final int ATTACK_COOLDOWN = 60;

    /** Frame counter since the last attack. */
    private int attackCounter = 0;

    /** Amount of damage dealt by the goblin's direct attack. */
    private static final int ATTACK_DAMAGE = 9;

    /** Cooldown between two fireballs (e.g., 600 = 10 seconds). */
    private static final int FIREBALL_COOLDOWN = 600;

    /** Counter for fireball cooldown. */
    private int fireballCounter = 0;

    /** Movement speed of the fireball. */
    private static final int FIREBALL_SPEED = 5;

    /** Damage dealt by the fireball on hit. */
    private static final int FIREBALL_DAMAGE = 2;

    /**
     * List of all active fireballs shot by the boss.
     * <p>Each fireball has its own position and velocity.</p>
     */
    private List<Fireball> fireballs = new ArrayList<>();

    /** Image representing the fireball graphics. */
    private BufferedImage fireballImage;

    /** Flag indicating whether the goblin has already dropped a key (to prevent multiple drops). */
    private boolean hasDroppedKey = false;

    /**
     * Constructs a new Boss Goblin enemy.
     * <p>
     * Initializes position, movement speed, health values, collision bounds,
     * and loads the sprite and fireball images.
     * </p>
     *
     * @param gp the main game panel instance for context and asset loading
     */
    public Boss_Goblin(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Boss Goblin";
        speed = 2;
        maxLife = 6;
        life = maxLife;

        direction = "down";

        solidArea = new Rectangle();
        solidArea.x = 3;
        solidArea.y = 10;
        solidArea.width = 20;
        solidArea.height = 50;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
        loadFireballImage();
    }

    /**
     * Loads the fireball projectile image for the Boss Goblin's attack.
     * <p>
     * Attempts to read the image resource at
     * <code>/cz/cvut/fel/pjv/golyakat/dungeon_escape/Fireball/FB001.png</code>.
     * Logs an error via {@link GameLogger} if loading fails.
     * </p>
     */
    private void loadFireballImage() {
        try {
            fireballImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Fireball/FB001.png")));
        } catch (Exception e) {
            GameLogger.error("Error loading fireball image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads sprite images for the goblin based on direction and animation.
     */
    public void getImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin_1.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin_1.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin_1.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin_1.png")));
        } catch (Exception e) {
            GameLogger.error("Error loading skeleton sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Determines the boss's behavior based on distance from the player.
     * <ul>
     *   <li>When player is nearby: tracks the player.</li>
     *   <li>Otherwise: random movement.</li>
     * </ul>
     */
    public void setAction() {
        actionLockCounter++;

        // Calculate distance to player
        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= DETECTION_RANGE && !isDead) {
            // Move toward player
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? "right" : "left";
            } else {
                direction = dy > 0 ? "down" : "up";
            }
        } else {
            // Random movement if player is out of range
            if (actionLockCounter >= 120) {
                Random random = new Random();
                int i = random.nextInt(100) + 1;
                if (i <= 25) {
                    direction = "up";
                } else if (i <= 50) {
                    direction = "down";
                } else if (i <= 75) {
                    direction = "left";
                } else {
                    direction = "right";
                }
                actionLockCounter = 0;
            }
        }
    }

    /**
     * Shoots a fireball towards the player if they are in range.
     */
    private void shootFireball() {
        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance > 0) {
            double speedX = (dx / distance) * FIREBALL_SPEED;
            double speedY = (dy / distance) * FIREBALL_SPEED;
            fireballs.add(new Fireball(worldX + gp.tileSize, worldY + gp.tileSize, speedX, speedY));
            GameLogger.info(name + " shot a fireball!");
        }
    }

    /**
     * Main boss update method - called every game frame.
     * <p>
     * Handles: movement, collisions, attacks, shooting, animation, death, key dropping.
     * </p>
     */
    public void update() {
        if (isDead) {
            super.update(); // Handle fade-out
            return;
        }

        setAction();

        if (direction == null) {
            direction = "down";
        }

        int oldX = worldX;
        int oldY = worldY;

        // Move toward player or randomly
        switch (direction) {
            case "up":
                worldY -= speed;
                break;
            case "down":
                worldY += speed;
                break;
            case "left":
                worldX -= speed;
                break;
            case "right":
                worldX += speed;
                break;
        }

        // Check collision with tiles and objects
        collisionOn = false;
        gp.collisionChecker.checkTiles(this);
        if (collisionOn) {
            worldX = oldX;
            worldY = oldY;
            actionLockCounter = 120;
        }

        // Check for player collision and attack
        attackCounter++;
        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance <= ATTACK_RANGE && attackCounter >= ATTACK_COOLDOWN) {
            gp.player.receiveDamage(ATTACK_DAMAGE);
            attackCounter = 0;
            GameLogger.info(name + " attacked player! Player HP: " + gp.player.life);
        }

        // Handle fireball attack
        fireballCounter++;
        if (fireballCounter >= FIREBALL_COOLDOWN) {
            shootFireball();
            fireballCounter = 0;
        }

        // Update fireballs
        List<Fireball> fireballsToRemove = new ArrayList<>();
        for (Fireball fireball : fireballs) {
            fireball.update();
            // Check collision with player
            int fireballScreenX = (int) fireball.x - gp.player.worldX + gp.player.screenX;
            int fireballScreenY = (int) fireball.y - gp.player.worldY + gp.player.screenY;
            int playerScreenCenterX = gp.player.screenX + gp.tileSize / 2;
            int playerScreenCenterY = gp.player.screenY + gp.tileSize / 2;
            double fireballDistance = Math.sqrt(Math.pow(fireballScreenX - playerScreenCenterX, 2) + Math.pow(fireballScreenY - playerScreenCenterY, 2));
            if (fireballDistance <= gp.tileSize / 2) {
                gp.player.receiveDamage(FIREBALL_DAMAGE);
                fireballsToRemove.add(fireball);
                GameLogger.info(name + "'s fireball hit player! Player HP: " + gp.player.life);
                continue;
            }
            // Remove fireballs that go off-screen
            if (fireball.x < gp.player.worldX - gp.screenWidth / 2 ||
                    fireball.x > gp.player.worldX + gp.screenWidth / 2 ||
                    fireball.y < gp.player.worldY - gp.screenHeight / 2 ||
                    fireball.y > gp.player.worldY + gp.screenHeight / 2) {
                fireballsToRemove.add(fireball);
            }
        }
        fireballs.removeAll(fireballsToRemove);

        // Animation
        spriteCounter++;
        if (spriteCounter > 15) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }

        // Check if dead and drop key
        if (life <= 0 && !isDead) {
            isDead = true;
            fadeAlpha = 1.0f;
            fadeCounter = 0;
            // Drop the key and add it to the player's inventory
            if (!hasDroppedKey) {
                Item_Key key = new Item_Key(); // Use Item_Key instead of Object_Key
                ChestInventoryManager.ItemData keyItem = new ChestInventoryManager.ItemData("Key", 1);
                keyItem.setItem(key);
                gp.player.addItem(keyItem);
                hasDroppedKey = true;
                GameLogger.info(name + " has been defeated and dropped a key!");
            }
        }
    }


    /**
     * Renders the goblin on screen and then its fireballs.
     *
     * @param g2d graphics context
     */
    @Override
    public void draw(Graphics2D g2d) {
        if (isDead && fadeAlpha <= 0) {
            return; // Skip drawing if fully faded
        }

        if (direction == null) {
            direction = "down";
        }

        BufferedImage imageToDraw = null;
        switch (direction) {
            case "up":
                imageToDraw = (spriteNum == 1) ? up1 : up2;
                break;
            case "down":
                imageToDraw = (spriteNum == 1) ? down1 : down2;
                break;
            case "left":
                imageToDraw = (spriteNum == 1) ? left1 : left2;
                break;
            case "right":
                imageToDraw = (spriteNum == 1) ? right1 : right2;
                break;
            default:
                imageToDraw = down1;
                break;
        }

        this.image = imageToDraw;

        // Calculate screen position
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Draw the goblin at double size (2 * gp.tileSize)
        int scaledSize = gp.tileSize * 2;
        if (screenX > -scaledSize && screenX < gp.screenWidth && screenY > -scaledSize && screenY < gp.screenHeight) {
            if (!isDead) {
                g2d.drawImage(image, screenX, screenY, scaledSize, scaledSize, null);
            }
        }

        // Draw fireballs
        for (Fireball fireball : fireballs) {
            int fireballScreenX = (int) fireball.x - gp.player.worldX + gp.player.screenX;
            int fireballScreenY = (int) fireball.y - gp.player.worldY + gp.player.screenY;
            if (fireballScreenX > -gp.tileSize && fireballScreenX < gp.screenWidth && fireballScreenY > -gp.tileSize && fireballScreenY < gp.screenHeight) {
                g2d.drawImage(fireballImage, fireballScreenX, fireballScreenY, gp.tileSize / 2, gp.tileSize / 2, null);
            }
        }
    }

    /**
     * Inner class {@code Fireball} represents a single fireball shot by the boss.
     * <p>
     * Stores position and velocity and has a simple {@code update()} method for movement.
     * </p>
     */

    private class Fireball {
        double x, y;
        double speedX, speedY;

        Fireball(double x, double y, double speedX, double speedY) {
            this.x = x;
            this.y = y;
            this.speedX = speedX;
            this.speedY = speedY;
        }

        /** Moves the ball by its velocity (1 frame) */
        void update() {
            x += speedX;
            y += speedY;
        }
    }
}