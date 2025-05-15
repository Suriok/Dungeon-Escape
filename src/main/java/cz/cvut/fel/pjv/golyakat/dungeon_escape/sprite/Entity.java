package cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The {@code Entity} class serves as a base class for all game entities
 * that move and interact - including the player and monsters.
 * <p>
 * Contains attributes for movement, collisions, health, attacks, and animations.
 * </p>
 */
public class Entity extends GameObject {

    /** Reference to the main game panel. */
    protected final gamePanel gp;

    /** Movement speed of the entity (in pixels per update). */
    public int speed;

    /** Sprite images for movement in four directions (two frames per direction). */
    public BufferedImage up1, up2, down1, down2, right1, right2, left1, left2;

    /** Movement direction ("up", "down", "left", "right"). */
    public String direction = "down";

    /** Frame counter for animation. */
    public int spriteCounter = 0;

    /** Number of the sprite frame currently being rendered (1 or 2). */
    public int spriteNum = 1;

    /**
     * Rectangle representing the collision area of the entity.
     * Used for detecting contact with environment and objects.
     */
    public Rectangle solidArea;

    /** Default X coordinate of the collision area. */
    public int solidAreaDefaultX;

    /** Default Y coordinate of the collision area. */
    public int solidAreaDefaultY;

    /** Flag indicating whether a collision is detected in the current frame. */
    public boolean collisionOn = false;

    /** Maximum number of entity's health points. */
    public int maxLife;

    /** Current number of entity's health points. */
    public int life;

    // === Fade-out effect on death ===

    /** Flag indicating whether the entity is dead. */
    public boolean isDead = false;

    /** Entity transparency during fade-out (0.0 to 1.0). */
    public float fadeAlpha = 1.0f;

    /** Counter for fade effect duration. */
    public int fadeCounter = 0;

    /** Maximum number of frames for fade effect. */
    public static final int FADE_DURATION = 60;

    // === Monster attack parameters ===

    /** Damage dealt by this entity's attack. */
    protected final int attackDamage = 1;

    /** Maximum distance at which the entity can attack. */
    protected final int attackRange = 48;

    /** Number of frames before the entity can attack again. */
    protected final int attackCooldown = 60;

    /** Counter since the last attack. */
    protected int attackCounter = 0;

    /**
     * Constructor for the base entity.
     *
     * @param gp instance of the main panel
     */
    public Entity(gamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle();
    }

    /**
     * Updates the entity's state - attack, movement, and fade effect if applicable.
     */
    public void update() {
        if (!isDead) {
            attackCounter++;

            // Attempt to attack the player
            if (attackCounter >= attackCooldown) {
                double distance = Math.sqrt(Math.pow(gp.player.worldX - worldX, 2)
                        + Math.pow(gp.player.worldY - worldY, 2));
                if (distance <= attackRange) {
                    attack();
                    attackCounter = 0;
                }
            }

            // (Monster AI movement could be here)
        }

        // Fade effect after death
        if (isDead && fadeAlpha > 0) {
            fadeCounter++;
            fadeAlpha = 1.0f - ((float) fadeCounter / FADE_DURATION);
            if (fadeAlpha < 0) {
                fadeAlpha = 0;
            }
        }
    }

    /**
     * Performs the entity's attack - by default attacks the player.
     */
    public void attack() {
        GameLogger.info("DEBUG: " + name + " attacks player with damage " + attackDamage);
        gp.player.receiveDamage(attackDamage);
    }

    /**
     * Renders the entity based on its current position and state.
     *
     * @param g2d graphics context
     */
    public void draw(Graphics2D g2d) {
        super.draw(g2d, gp);
    }

    /**
     * Vrátí aktuální snímek sprite podle směru a čísla snímku.
     *
     * @return příslušný obrázek animace
     */
    public BufferedImage getCurrentSprite() {
        return switch (direction) {
            case "up"    -> (spriteNum == 1) ? up1 : up2;
            case "down"  -> (spriteNum == 1) ? down1 : down2;
            case "left"  -> (spriteNum == 1) ? left1 : left2;
            case "right" -> (spriteNum == 1) ? right1 : right2;
            default      -> down1;
        };
    }

}
