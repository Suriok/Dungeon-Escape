package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import java.awt.*;
import java.util.Random;

public abstract class Monster extends Entity {
    protected int actionLockCounter = 0;
    protected int attackCounter     = 0;

    protected final Random rng = new Random();

    protected final int ATTACK_DAMAGE;
    protected final int DETECTION_RANGE = 5 * 48;
    protected final int ATTACK_COOLDOWN = 60;

    protected Monster(gamePanel gp, String name, int speed, int maxLife, int attackDamage,
                      Rectangle solid) {

        super(gp);
        this.name  = name;
        this.speed = speed;
        this.maxLife = maxLife;
        this.life    = maxLife;

        this.ATTACK_DAMAGE    = attackDamage;

        this.direction = "down";
        this.solidArea = solid;
        this.solidAreaDefaultX = solid.x;
        this.solidAreaDefaultY = solid.y;

        loadImages();                // реализует подкласс
    }

    /* Подкласс подгружает спрайты */
    protected abstract void loadImages();

    /* Подкласс может переопределить дроп/лут */
    protected void onDeath() { }

    /* ------ AI: выбор направления ------ */
    protected void setAction() {
        actionLockCounter++;

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double dist = Math.hypot(dx, dy);

        if (dist <= DETECTION_RANGE && !isDead) {
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

    /* ------ главный update ------ */
    public void update() {
        if (isDead) {                   // fade
            super.update();
            return;
        }

        setAction();
        if (direction == null) direction = "down";

        int oldX = worldX, oldY = worldY;
        switch (direction) {
            case "up"    -> worldY -= speed;
            case "down"  -> worldY += speed;
            case "left"  -> worldX -= speed;
            case "right" -> worldX += speed;
        }

        collisionOn = false;
        gp.collisionChecker.checkTiles(this);
        if (collisionOn) {              // откат при стене
            worldX = oldX;
            worldY = oldY;
            actionLockCounter = 120;
        }


        // === Player attack ===
        attackCounter++;
        int playerCenterX = gp.player.worldX + gp.player.solidArea.x + gp.player.solidArea.width / 2;
        int playerCenterY = gp.player.worldY + gp.player.solidArea.y + gp.player.solidArea.height / 2;
        int monsterCenterX = worldX + solidArea.x + solidArea.width / 2;
        int monsterCenterY = worldY + solidArea.y + solidArea.height / 2;

        double distance = Math.hypot(playerCenterX - monsterCenterX, playerCenterY - monsterCenterY);

        if (attackCounter >= ATTACK_COOLDOWN && distance <= 2 * gp.tileSize) {
            gp.player.receiveDamage(ATTACK_DAMAGE);
            attackCounter = 0;
            GameLogger.info(name + " hits player (" + ATTACK_DAMAGE + " dmg)");
        }

        /* --- анимация шага --- */
        spriteCounter = (spriteCounter + 1) % 30;
        spriteNum     = (spriteCounter < 15) ? 1 : 2;

        /* --- смерть --- */
        if (life <= 0 && !isDead) {
            onDeath();
            removeSelf();             // единственный вызов
        }
    }

    private void removeSelf() {
        for (int i = 0; i < gp.monster[gp.currentMap].length; i++) {
            if (gp.monster[gp.currentMap][i] == this) {
                gp.monster[gp.currentMap][i] = null;
                break;
            }
        }
    }

    public void draw(Graphics2D g2d) {
        this.image = getCurrentSprite();
        if (image == null) return;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }

}
