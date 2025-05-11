package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss;

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

public class Boss_Eye extends Entity {
    private gamePanel gp;
    public int actionLockCounter = 0;
    private static final int DETECTION_RANGE = 5 * 48; // 5 tiles (assuming tileSize = 48)
    private static final int ATTACK_RANGE = 48; // 1 tile
    private static final int ATTACK_COOLDOWN = 60; // 1 second at 60 FPS
    private int attackCounter = 0;
    private static final int ATTACK_DAMAGE = 15;

    private boolean hasDroppedKey = false; // Flag to ensure the key is dropped only once

    public Boss_Eye(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Boss_Eye";
        speed = 2;
        maxLife = 15;
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
    }


    public void getImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Eye.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/eye_1.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Eye.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/eye_1.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Eye.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/eye_1.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Eye.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/eye_1.png")));
        } catch (Exception e) {
            System.err.println("Error loading skeleton sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
            System.out.println(name + " attacked player! Player HP: " + gp.player.life);
        }

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
                System.out.println(name + " has been defeated and dropped a key!");
            }
        }
    }

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

        // Draw the eye at double size (2 * gp.tileSize)
        int scaledSize = gp.tileSize * 2;
        if (screenX > -scaledSize && screenX < gp.screenWidth && screenY > -scaledSize && screenY < gp.screenHeight) {
            if (!isDead) {
                g2d.drawImage(image, screenX, screenY, scaledSize, scaledSize, null);
            }
        }
    }

}