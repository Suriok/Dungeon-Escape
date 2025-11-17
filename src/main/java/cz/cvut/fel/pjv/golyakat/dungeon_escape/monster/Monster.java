package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import java.awt.*;
import java.util.Random;

/**
 * Abstract base class for all monster types in the game.
 * <p>
 * Defines common behavior such as movement, AI decision-making,
 * combat interactions, and rendering logic.
 * </p>
 */
public abstract class Monster extends Entity {

    /** Counter used to time direction changes for movement AI. */
    protected int actionLockCounter = 0;

    /** Counter used to control attack cooldown intervals. */
    protected int attackCounter = 0;

    /** Random number generator used for AI movement decisions. */
    protected final Random rng = new Random();

    /** Amount of damage the monster deals per attack. */
    protected final int ATTACK_DAMAGE;

    /** Range within which the monster detects the player (in pixels). */
    protected final int DETECTION_RANGE = 5 * 48;

    /** Cooldown time (in frames) between monster attacks. */
    protected final int ATTACK_COOLDOWN = 60;

    /**
     * Constructs a monster with basic properties.
     *
     * @param gp           reference to the game panel
     * @param name         the name of the monster
     * @param speed        the movement speed
     * @param maxLife      maximum health
     * @param attackDamage damage dealt to the player
     * @param solid        rectangle used for collision detection
     */
    protected Monster(gamePanel gp, String name, int speed, int maxLife, int attackDamage,
                      Rectangle solid) {

        super(gp);
        this.name = name;
        this.speed = speed;
        this.maxLife = maxLife;
        this.life = maxLife;
        this.ATTACK_DAMAGE = attackDamage;
        this.direction = "down";

        this.solidArea = solid;
        this.solidAreaDefaultX = solid.x;
        this.solidAreaDefaultY = solid.y;

        loadImages(); // Subclass must implement sprite loading
    }

    /**
     * Subclasses must load directional sprites in this method.
     */
    protected abstract void loadImages();

    /**
     * Called once when the monster dies.
     * <p>
     * Can be overridden to drop loot or trigger effects.
     * </p>
     */
    protected void onDeath() {
    }

    /**
     * Helper method to check if a tile is a wall (has collision).
     * @param row The tile's row
     * @param col The tile's column
     * @return true if the tile is a wall or out of bounds
     */
    private boolean isWall(int row, int col) {
        // Считаем границы карты "стеной"
        if (row < 0 || row >= gp.maxWorldRow || col < 0 || col >= gp.maxWorldCol) {
            return true;
        }
        // Проверяем коллизию тайла
        int tileNum = gp.tileH.mapTileNum[gp.currentMap][row][col];
        // Убедимся, что такой тайл существует в массиве tiles
        if (tileNum < 0 || tileNum >= gp.tileH.tiles.length || gp.tileH.tiles[tileNum] == null) {
            return false; // Безопасная обработка, если тайла нет
        }
        return gp.tileH.tiles[tileNum].collision;
    }

    /**
     * Checks if there is a direct line of sight (no walls) between the monster and the player.
     * Uses a simplified "line traversal" algorithm.
     * @return true if the line of sight is clear, false if it is blocked.
     */
    protected boolean hasLineOfSight() {
        // Получаем тайловые координаты
        int monsterCol = worldX / gp.tileSize;
        int monsterRow = worldY / gp.tileSize;
        int playerCol = gp.player.worldX / gp.tileSize;
        int playerRow = gp.player.worldY / gp.tileSize;

        int dx = playerCol - monsterCol;
        int dy = playerRow - monsterRow;

        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) return true; // На том же тайле

        float x_inc = (float)dx / steps;
        float y_inc = (float)dy / steps;

        float x = monsterCol;
        float y = monsterRow;

        // Проверяем каждый тайл на пути к игроку
        for (int i = 0; i < steps; i++) {
            x += x_inc;
            y += y_inc;

            int currentCol = Math.round(x);
            int currentRow = Math.round(y);

            // Если текущий проверяемый тайл - это тайл, где стоит сам игрок, то все в порядке
            if (currentCol == playerCol && currentRow == playerRow) {
                return true;
            }

            // Если мы попали в стену до того, как достигли игрока
            if (isWall(currentRow, currentCol)) {
                return false; // Путь заблокирован
            }
        }

        return true; // Препятствий не найдено
    }

    // === AI: Determines movement direction based on player proximity or randomly ===
    protected void setAction() {
        actionLockCounter++;

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double dist = Math.hypot(dx, dy);

        if (dist <= DETECTION_RANGE && !isDead && hasLineOfSight()) {
            direction = (Math.abs(dx) > Math.abs(dy))
                    ? (dx > 0 ? "right" : "left")
                    : (dy > 0 ? "down" : "up");
        } else if (actionLockCounter >= 120) {
            direction = switch (rng.nextInt(4)) {
                case 0 -> "up";
                case 1 -> "down";
                case 2 -> "left";
                default -> "right";
            };
            actionLockCounter = 0;
        }
    }

    /**
     * Main update loop for monster behavior.
     * Handles movement, collision, combat, animation, and death.
     */
    public void update() {
        if (isDead) {
            // === Handle fade-out animation for dead monster ===
            super.update();
            return;
        }

        setAction();
        if (direction == null) direction = "down";

        int oldX = worldX, oldY = worldY;
        switch (direction) {
            case "up" -> worldY -= speed;
            case "down" -> worldY += speed;
            case "left" -> worldX -= speed;
            case "right" -> worldX += speed;
        }

        collisionOn = false;
        gp.collisionChecker.checkTiles(this);
        if (collisionOn) {
            // === Rollback movement if collision occurred ===
            worldX = oldX;
            worldY = oldY;
            actionLockCounter = 120; // force direction change
        }

        // === Player attack check ===
        attackCounter++;
        int playerCenterX = gp.player.worldX + gp.player.solidArea.x + gp.player.solidArea.width / 2;
        int playerCenterY = gp.player.worldY + gp.player.solidArea.y + gp.player.solidArea.height / 2;
        int monsterCenterX = worldX + solidArea.x + solidArea.width / 2;
        int monsterCenterY = worldY + solidArea.y + solidArea.height / 2;

        double distance = Math.hypot(playerCenterX - monsterCenterX, playerCenterY - monsterCenterY);

        if (attackCounter >= ATTACK_COOLDOWN && distance <=  gp.tileSize) {
            gp.player.receiveDamage(ATTACK_DAMAGE);
            attackCounter = 0;
            GameLogger.info(name + " hits player (" + ATTACK_DAMAGE + " dmg)");
        }

        // === Walking animation: switch sprite every 15 frames ===
        spriteCounter = (spriteCounter + 1) % 30;
        spriteNum = (spriteCounter < 15) ? 1 : 2;

        // === Death handling ===
        if (life <= 0 && !isDead) {
            onDeath();       // call loot drop or effects
            removeSelf();    // remove from monster array
        }
    }


    /**
     * Removes this monster instance from the game world.
     */
    private void removeSelf() {
        for (int i = 0; i < gp.monster[gp.currentMap].length; i++) {
            if (gp.monster[gp.currentMap][i] == this) {
                gp.monster[gp.currentMap][i] = null;
                break;
            }
        }
    }

    /**
     * Draws the current sprite of the monster on the screen relative to the player's position.
     *
     * @param g2d the graphics context
     */
    public void draw(Graphics2D g2d) {
        super.draw(g2d);
    }
}
