package cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Apple;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Blubbery;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_HealthePotion;
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

/**
 * The Player class represents the main character controlled by the player.
 */
public class Player extends Entity {
    final gamePanel gp;
    final KeyHandler keyH;
    public final int screenX;
    public final int screenY;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    public String direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;
    private final List<ChestInventoryManager.ItemData> inventory;
    private final GameObject[] equippedArmor;
    private GameObject equippedWeapon;
    private GameObject equippedGrade;
    public boolean isHit = false;
    private int hitEffectCounter = 0;
    private static final int LADDER_TILE = 10;

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
        equippedGrade = null;
        setDefaulteValues();
        getPlayerImage();
    }

    public void setDefaulteValues() {
        if (gp.currentMap == 0) {
            worldX = gp.tileSize * 15;
            worldY = gp.tileSize * 22;
        } else if (gp.currentMap == 1) {
            worldX = gp.tileSize * 12;
            worldY = gp.tileSize * 18;
        }
        speed = 4;
        direction = "down";
        maxLife = 8;
        life = maxLife;
    }

    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_up_1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_up_2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_down_1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_down_2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_right_1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_right_2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_left_1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_left_2.png")));
        } catch (IOException | NullPointerException e) {
            GameLogger.error("Error loading player images: " + e.getMessage());
        }
    }

    public void update() {
        collisionOn = false;
        int oldX = worldX;
        int oldY = worldY;

        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else direction = "right";

            switch (direction) {
                case "up" -> worldY -= speed;
                case "down" -> worldY += speed;
                case "left" -> worldX -= speed;
                case "right" -> worldX += speed;
            }

            gp.collisionChecker.checkTiles(this);
            gp.collisionChecker.checkObject(this);

            int col = (worldX + solidArea.x) / gp.tileSize;
            int row = (worldY + solidArea.y) / gp.tileSize;
            int currentTile = gp.tileH.mapTileNum[gp.currentMap][row][col];

            if (currentTile == LADDER_TILE) {
                gp.currentMap = (gp.currentMap == 0) ? 1 : 0;
                gp.tileH.findWalkableRegions();
                if (!gp.levelSpawned[gp.currentMap]) {
                    gp.assetSetter.setMonster();
                    gp.levelSpawned[gp.currentMap] = true;
                }
                setDefaulteValues();
                worldY += gp.tileSize;
            }

            if (collisionOn) {
                worldX = oldX;
                worldY = oldY;
            }

            spriteCounter++;
            if (spriteCounter > 15) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }

        if (keyH.ePressed) {
            keyH.ePressed = false;
            int i = gp.collisionChecker.checkObjectForInteraction(this, true);
            if (i != 999 && gp.obj[gp.currentMap][i] != null) {
                gp.collisionChecker.handleObjectInteraction( i);
            }
        }

        if (isHit) {
            hitEffectCounter--;
            if (hitEffectCounter <= 0) {
                isHit = false;
                hitEffectCounter = 0;
            }
        }
    }

    public void attack() {
        if (equippedWeapon == null) return;
        int attackDamage = (equippedWeapon instanceof Weapon) ? ((Weapon) equippedWeapon).getAttack() : 1;
        int targetX = worldX;
        int targetY = worldY;

        switch (direction) {
            case "up" -> targetY -= gp.tileSize;
            case "down" -> targetY += gp.tileSize;
            case "left" -> targetX -= gp.tileSize;
            case "right" -> targetX += gp.tileSize;
        }

        for (Entity monster : gp.monster[gp.currentMap]) {
            if (monster != null && !monster.isDead &&
                    monster.worldX / gp.tileSize == targetX / gp.tileSize &&
                    monster.worldY / gp.tileSize == targetY / gp.tileSize) {
                monster.life -= attackDamage;
                if (monster.life < 0) monster.life = 0;
                break;
            }
        }
    }

    public void receiveDamage(int damage) {
        float totalDefense = getTotalDefense();
        int reducedDamage = Math.max(0, damage - (int) totalDefense);
        life -= reducedDamage;
        if (life < 0) life = 0;
        isHit = true;
        hitEffectCounter = 60;
    }

    public void removeItem(int index) {
        if (index >= 0 && index < inventory.size()) {
            ChestInventoryManager.ItemData item = inventory.get(index);
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
            } else {
                inventory.remove(index);
            }
        }
    }

    public boolean removeItemByName(String name) {
        for (int i = 0; i < inventory.size(); i++) {
            ChestInventoryManager.ItemData item = inventory.get(i);
            if (item.getName().equals(name)) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    inventory.remove(i);
                }
                return true;
            }
        }
        return false;
    }


    public void consumeHealingItem(ChestInventoryManager.ItemData item) {
        float healAmountFloat;
        switch (item.getName()) {
            case "Apple" -> healAmountFloat = ((Item_Apple) item.getItem()).getHealAmount();
            case "blubbery" -> healAmountFloat = ((Item_Blubbery) item.getItem()).getHealAmount();
            case "potion" -> healAmountFloat = ((Item_HealthePotion) item.getItem()).getHealAmount();
            default -> {
                return;
            }
        }
        int healAmount = Math.round(healAmountFloat);
        life = Math.min(life + healAmount, maxLife);
    }

    @Override
    public void draw(Graphics2D g2d) {
        this.image = getCurrentSprite();

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        if (isHit) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setColor(Color.RED);
            g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    public void equipArmor(GameObject armor, int slot) {
        if (slot >= 0 && slot < equippedArmor.length) {
            equippedArmor[slot] = armor;
        }
    }

    public GameObject[] getEquippedArmor() {
        return equippedArmor;
    }

    public float getTotalDefense() {
        float total = 0;
        for (GameObject obj : equippedArmor) {
            if (obj instanceof Armor armor) {
                total += armor.getDefensAmount();
            }
        }
        return total;
    }

    public void equipWeapon(GameObject weapon) {
        this.equippedWeapon = weapon;
    }

    public GameObject getEquippedWeapon() {
        return equippedWeapon;
    }

    public void equipGrade(GameObject grade) {
        this.equippedGrade = grade;
    }

    public GameObject getEquippedGrade() {
        return equippedGrade;
    }

    public void reset() {
        setDefaulteValues();
        inventory.clear();
        life = maxLife;
        Arrays.fill(equippedArmor, null);
        equippedWeapon = null;
        equippedGrade = null;
    }

    public void addItem(ChestInventoryManager.ItemData item) {
        ItemType t = item.getType();
        if (t == ItemType.HEALING || t == ItemType.KEY || t == ItemType.KEY_PART) {
            inventory.add(new ChestInventoryManager.ItemData(item.getName(), 1));
        }
    }
    public List<ChestInventoryManager.ItemData> getInventory() {
        return inventory;
    }
}
