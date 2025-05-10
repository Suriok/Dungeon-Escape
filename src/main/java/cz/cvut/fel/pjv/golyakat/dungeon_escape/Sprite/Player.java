package cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.KeyHandler;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Apple;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Blubbery;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_HealthePotion;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Key;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.Weapon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Player extends Entity {
    gamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;

    public String direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;

    // Attack animation state
    private boolean isAttacking = false;
    private int attackAnimationCounter = 0;
    private static final int ATTACK_ANIMATION_DURATION = 20; // Duration of attack animation (in frames)

    private List<ChestInventoryManager.ItemData> inventory;
    private GameObject[] equippedArmor;
    private GameObject equippedWeapon;
    private GameObject equippedGrade; // Новый слот для grade
    private static final int ATTACK_RANGE = 96; // 2 tiles
    private static final int ATTACK_COOLDOWN = 30; // 0.5 seconds at 60 FPS
    private int attackCounter = 0;
    public boolean isHit = false;
    private int hitEffectCounter = 0;

    public Player(gamePanel gp, KeyHandler keyH) {
        super(gp);
        this.gp = gp;
        this.keyH = keyH;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        screenX = gp.screenWidth / 2;
        screenY = gp.screenHeight / 2;

        solidArea = new Rectangle(8, 16, gp.tileSize - 24, gp.tileSize - 24);

        inventory = new ArrayList<>();
        equippedArmor = new GameObject[4];
        equippedWeapon = null;
        equippedGrade = null; // Инициализация слота grade

        setDefaulteValues();
        getPlayerImage();
    }

    public void setDefaulteValues() {
        if (gp.currentMap == 0){
        worldX = gp.tileSize * 15;
        worldY = gp.tileSize * 22;
        }
        if (gp.currentMap == 1){
            worldX = gp.tileSize * 12;
            worldY = gp.tileSize * 17;
        }
        speed = 4;
        direction = "down";

        maxLife = 8;
        life = maxLife;
    }

    public void getPlayerImage() {
        try {
            // Load movement sprites
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_up_1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_up_2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_down_1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_down_2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_right_1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_right_2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_left_1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_left_2.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        collisionOn = false;
        int oldX = worldX;
        int oldY = worldY;
        int interactionIndex = 999;

        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

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

            gp.collisionChecker.checkTiles(this);
            int objIndex = gp.collisionChecker.checkObject(this, true);

            if (collisionOn) {
                worldX = oldX;
                worldY = oldY;
                System.out.println("Collision detected, reverting to position: (" + oldX/gp.tileSize + ", " + oldY/gp.tileSize + ")");
            }

            spriteCounter++;
            if (spriteCounter > 15) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }

        attackCounter++;

        // Update attack animation
        if (isAttacking) {
            attackAnimationCounter++;
            if (attackAnimationCounter >= ATTACK_ANIMATION_DURATION) {
                isAttacking = false;
                attackAnimationCounter = 0;
            }
        }

        interactionIndex = gp.collisionChecker.checkObjectForInteraction(this, true);
        if (keyH.ePressed && interactionIndex != 999 && gp.obj[gp.currentMap][interactionIndex] != null) {
            // Handle DoorSide that requires a key as a fallback for testing
            if (gp.obj[gp.currentMap][interactionIndex].name.equals("DoorSide")) {
                Object_DoorSide door = (Object_DoorSide) gp.obj[gp.currentMap][interactionIndex];
                if (door.requiresKey && !door.isOpen()) {
                    // Check if the player has a key
                    ChestInventoryManager.ItemData keyItem = inventory.stream()
                            .filter(item -> item.getName().equals("Key"))
                            .findFirst()
                            .orElse(null);
                    if (keyItem != null) {
                        door.unlock();
                        inventory.remove(keyItem);
                        System.out.println("Player used the key to unlock the door at index: " + interactionIndex);
                    } else {
                        System.out.println("Player attempted to open a key-locked door but has no key.");
                    }
                } else {
                    gp.collisionChecker.handleObjectInteraction(this, interactionIndex);
                }
            } else {
                gp.collisionChecker.handleObjectInteraction(this, interactionIndex);
            }
            keyH.ePressed = false;
        } else if (keyH.ePressed) {
            keyH.ePressed = false;
        }

        if (keyH.fPressed && !inventory.isEmpty()) {
            ChestInventoryManager.ItemData item = inventory.get(0);
            float healAmountFloat = 0;
            switch (item.getName()) {
                case "Apple":
                    healAmountFloat = ((Item_Apple) item.getItem()).getHealAmount();
                    break;
                case "blubbery":
                    healAmountFloat = ((Item_Blubbery) item.getItem()).getHealAmount();
                    break;
                case "potion":
                    healAmountFloat = ((Item_HealthePotion) item.getItem()).getHealAmount();
                    break;
            }
            int healAmount = (int) Math.round(healAmountFloat);
            int newLife = life + healAmount;
            if (newLife > maxLife) {
                newLife = maxLife;
            }
            life = newLife;
            inventory.remove(0);
            keyH.fPressed = false;
        }
        if (isHit) {
            hitEffectCounter--;
            if (hitEffectCounter <= 0) {
                isHit = false;
            }
        }

        // GAMEOVER - WHEN PLAYER DO NOT HAVE HP
        if(life <= 0){
            gp.gameState = gp.gameOverState;
        }

    }

    public void attack() {
        if (equippedWeapon == null) {
            System.out.println("Player attempted to attack, but no weapon is equipped.");
            return;
        }

        // Start attack animation
        isAttacking = true;
        attackAnimationCounter = 0;

        int attackDamage = 1;
        if (equippedWeapon instanceof Weapon) {
            attackDamage = ((Weapon) equippedWeapon).getAttack();
        }

        Entity target = null;
        double minDistance = Double.MAX_VALUE;
        for (Entity monster : gp.monster[gp.currentMap]) {
            if (monster != null && !monster.isDead) {
                int dx = monster.worldX - worldX;
                int dy = monster.worldY - worldY;
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance <= ATTACK_RANGE && distance < minDistance) {
                    minDistance = distance;
                    target = monster;
                }
            }
        }

        gp.playSE(1);

        if (target != null) {
            target.life -= attackDamage;
            if (target.life < 0) {
                target.life = 0;
            }

            gp.playSE(1);

            System.out.println("Player attacked " + target.name + " for " + attackDamage + " damage. Monster HP: " + target.life);
        } else {
            System.out.println("Player attacked, but no monsters in range.");
        }
    }

    public void receiveDamage(int damage) {
        float totalDefense = getTotalDefense();
        int reducedDamage = Math.max(0, damage - (int) totalDefense);
        life -= reducedDamage;
        if (life < 0) {
            life = 0;
        }

        // Звук удара
        gp.playSE(2);

        // Визуальный эффект урона
        isHit = true;
        hitEffectCounter = 60; // 1 сек при 60 FPS

        System.out.println("Player received " + damage + " damage ");
    }


    public void pickUpObject(int i) {
    }

    public void addItem(ChestInventoryManager.ItemData item) {
        inventory.add(new ChestInventoryManager.ItemData(item.getName(), 1));
        System.out.println("Added " + item.getName() + " to player inventory. Total items: " + inventory.size());
    }

    public List<ChestInventoryManager.ItemData> getInventory() {
        return inventory;
    }

    public void removeItem(int index) {
        if (index >= 0 && index < inventory.size()) {
            ChestInventoryManager.ItemData item = inventory.get(index);
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                System.out.println("Reduced quantity of " + item.getName() + " to " + item.getQuantity());
            } else {
                inventory.remove(index);
                System.out.println("Removed " + item.getName() + " from player inventory. Total items: " + inventory.size());
            }
        } else {
            System.err.println("Invalid inventory index for removal: " + index);
        }
    }

    public void consumeHealingItem(ChestInventoryManager.ItemData item) {
        float healAmountFloat = 0;
        switch (item.getName()) {
            case "Apple":
                healAmountFloat = ((Item_Apple) item.getItem()).getHealAmount();
                break;
            case "blubbery":
                healAmountFloat = ((Item_Blubbery) item.getItem()).getHealAmount();
                break;
            case "potion":
                healAmountFloat = ((Item_HealthePotion) item.getItem()).getHealAmount();
                break;
            default:
                System.out.println("Item " + item.getName() + " is not a healing item.");
                return;
        }
        int healAmount = (int) Math.round(healAmountFloat);
        int newLife = life + healAmount;
        if (newLife > maxLife) {
            newLife = maxLife;
        }
        life = newLife;
        System.out.println("Player consumed " + item.getName() + ", restored " + healAmount + " HP. Current HP: " + life);
    }

    @Override
    public void draw(Graphics2D g2d) {
        BufferedImage imageToDraw = null;

        // Normal movement animation
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
        }

        this.image = imageToDraw;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // 1. Отрисовать спрайт
        g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        // 2. Если получен урон — накладываем красный полупрозрачный слой СВЕРХУ
        if (isHit) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setColor(Color.RED);
            g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // сброс прозрачности
        }

        // Draw attack animation (wave effect)
        if (isAttacking && equippedWeapon != null) {
            drawWaveEffect(g2d);
        }
    }

    private void drawWaveEffect(Graphics2D g2d) {
        // Calculate the progress of the animation (0.0 to 1.0)
        float animationProgress = (float) attackAnimationCounter / ATTACK_ANIMATION_DURATION;

        // Set the color and stroke for the wave effect
        g2d.setColor(new Color(255, 255, 255, (int) (255 * (1.0f - animationProgress)))); // Fade out over time
        g2d.setStroke(new BasicStroke(3));

        // Base position of the wave (center of the player)
        int waveWidth = gp.tileSize; // Width of the wave
        int waveHeight = gp.tileSize / 2; // Height of the wave
        int waveStartX = screenX + gp.tileSize / 2;
        int waveStartY = screenY + gp.tileSize / 2;

        // Adjust wave position and orientation based on direction
        int[] xPoints = new int[5]; // Points for the wave polyline
        int[] yPoints = new int[5];
        int waveOffset = (int) (animationProgress * gp.tileSize); // Move the wave over the animation duration

        switch (direction) {
            case "up":
                // Wave starts at the player and moves upward
                for (int i = 0; i < 5; i++) {
                    xPoints[i] = waveStartX - waveWidth / 2 + (i * waveWidth / 4);
                    yPoints[i] = waveStartY - waveOffset + (int) (Math.sin(i * Math.PI / 4) * waveHeight); // Sine wave for curve
                }
                break;
            case "down":
                // Wave starts at the player and moves downward
                for (int i = 0; i < 5; i++) {
                    xPoints[i] = waveStartX - waveWidth / 2 + (i * waveWidth / 4);
                    yPoints[i] = waveStartY + waveOffset - (int) (Math.sin(i * Math.PI / 4) * waveHeight); // Sine wave for curve
                }
                break;
            case "left":
                // Wave starts at the player and moves leftward
                for (int i = 0; i < 5; i++) {
                    yPoints[i] = waveStartY - waveWidth / 2 + (i * waveWidth / 4);
                    xPoints[i] = waveStartX - waveOffset + (int) (Math.sin(i * Math.PI / 4) * waveHeight); // Sine wave for curve
                }
                break;
            case "right":
                // Wave starts at the player and moves rightward
                for (int i = 0; i < 5; i++) {
                    yPoints[i] = waveStartY - waveWidth / 2 + (i * waveWidth / 4);
                    xPoints[i] = waveStartX + waveOffset - (int) (Math.sin(i * Math.PI / 4) * waveHeight); // Sine wave for curve
                }
                break;
        }

        // Draw the wave as a polyline
        g2d.drawPolyline(xPoints, yPoints, 5);

        // Reset stroke
        g2d.setStroke(new BasicStroke(1));
    }

    public void equipArmor(GameObject armor, int slot) {
        if (!(armor instanceof Armor)) return;            // допускаем ТОЛЬКО Armor
        if (slot < 0 || slot >= equippedArmor.length) return;
        equippedArmor[slot] = armor;
        System.out.println("Equipped armor: " + armor.name + " in slot " + slot);
    }

    public void unequipArmor(int slot) {
        if (slot >= 0 && slot < equippedArmor.length) {
            System.out.println("Unequipped armor from slot " + slot);
            equippedArmor[slot] = null;
        }
    }

    public GameObject[] getEquippedArmor() {
        return equippedArmor;
    }

    public float getTotalDefense() {
        float totalDefense = 0;
        for (int i = 0; i < equippedArmor.length; i++) {
            if (equippedArmor[i] instanceof Armor) {
                float defense = ((Armor) equippedArmor[i]).getDefensAmount();
                totalDefense += defense;
                System.out.println(" Armor in slot " + i + " (" + equippedArmor[i].name + ") provides " + defense + " defense");
            } else if (equippedArmor[i] != null) {
                System.out.println("Item in slot " + i + " (" + equippedArmor[i].name + ") is not an Armor");
            }
        }
        return totalDefense;
    }

    public void equipWeapon(GameObject weapon) {
        this.equippedWeapon = weapon;
        System.out.println("Equipped weapon: " + weapon.name);
    }

    public void unequipWeapon() {
        System.out.println("Unequipped weapon");
        this.equippedWeapon = null;
    }

    public GameObject getEquippedWeapon() {
        return equippedWeapon;
    }

    public void equipGrade(GameObject grade) {
        this.equippedGrade = grade;
        System.out.println("Equipped grade: " + (grade != null ? grade.name : "none"));
    }

    public void unequipGrade() {
        System.out.println("Unequipped grade");
        this.equippedGrade = null;
    }

    public GameObject getEquippedGrade() {
        return equippedGrade;
    }

    // Method to handle drag-and-drop of the key onto the door
    public void useKeyOnDoor(int doorIndex) {
        if (doorIndex != 999 && gp.obj[gp.currentMap][doorIndex] != null && gp.obj[gp.currentMap][doorIndex].name.equals("DoorSide")) {
            Object_DoorSide door = (Object_DoorSide) gp.obj[0][doorIndex];
            if (door.requiresKey && !door.isOpen()) {
                ChestInventoryManager.ItemData keyItem = inventory.stream()
                        .filter(item -> item.getName().equals("Key"))
                        .findFirst()
                        .orElse(null);
                if (keyItem != null) {
                    door.unlock();
                    inventory.remove(keyItem);
                    System.out.println("Player used the key to unlock the door at index: " + doorIndex);
                } else {
                    System.out.println("Player attempted to use a key on the door but has no key in inventory.");
                }
            }
        }
    }
    public void reset() {
        setDefaulteValues();
        getInventory().clear();
        Arrays.fill(equippedArmor, null);
        equippedWeapon = null;
        equippedGrade = null;
    }

}