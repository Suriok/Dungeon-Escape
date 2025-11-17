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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The {@code Player} class represents the main controllable character in the game.
 * <p>
 * Handles movement, combat, inventory management, equipment, healing, and map transitions.
 * </p>
 */
public class Player extends Entity {

    final gamePanel gp;
    final KeyHandler keyH;

    /** Screen position of the player (always centered). */
    public final int screenX;
    public final int screenY;

    /** Backpack inventory for consumables and keys. */
    private final List<ChestInventoryManager.ItemData> inventory;

    /** Array for equipped armor (e.g. helmet, chestplate, boots, etc.). */
    private final GameObject[] equippedArmor;

    /** Equipped weapon item (e.g. sword). */
    private GameObject equippedWeapon;

    /** Equipped grade or rank item. */
    private GameObject equippedGrade;

    /** Damage flicker logic. */
    public boolean isHit = false;
    private int hitEffectCounter = 0;

    /** Attack wave animation logic. */
    public boolean isAttacking = false;
    private int attackEffectCounter = 0;
    private final int ATTACK_EFFECT_DURATION = 20;

    /** Tile ID used for ladder tile (map transition). */
    private static final int LADDER_TILE = 10;

    /**
     * Constructs a new {@code Player} instance.
     *
     * @param gp    reference to the game panel
     * @param keyH  input handler for movement and actions
     */
    public Player(gamePanel gp, KeyHandler keyH) {
        super(gp);
        this.gp = gp;
        this.keyH = keyH;

        // === Set up collision box and screen position ===
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

    // === Initialize player starting position and stats based on map ===
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

    // === Load player walking sprites ===
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

    /**
     * Updates player state each frame: movement, collision, interaction, animation, damage effect.
     */
    public void update() {
        collisionOn = false;
        int oldX = worldX;
        int oldY = worldY;

        // === Movement input ===
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

            // === Collision & object interaction ===
            gp.collisionChecker.checkTiles(this);
            gp.collisionChecker.checkObject(this);

            // === Ladder tile: switch map ===
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

            // === Sprite animation ===
            spriteCounter++;
            if (spriteCounter > 15) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }

        // === Interaction key (E) ===
        if (keyH.ePressed) {
            keyH.ePressed = false;
            int i = gp.collisionChecker.checkObjectForInteraction(this, true);
            if (i != 999 && gp.obj[gp.currentMap][i] != null) {
                gp.collisionChecker.handleObjectInteraction(i);
            }
        }

        // === Hit effect timer ===
        if (isHit) {
            hitEffectCounter--;
            if (hitEffectCounter <= 0) {
                isHit = false;
            }
        }

        // === Attack effect timer ===
        if (isAttacking) {
            attackEffectCounter--;
            if (attackEffectCounter <= 0) {
                isAttacking = false;
            }
        }
    }

    /**
     * Executes an attack on monsters within 1 tile of the player (3x3 area).
     */
    public void attack() {
        if (equippedWeapon == null) return;


        isAttacking = true;
        attackEffectCounter = ATTACK_EFFECT_DURATION;

        int attackDamage = (equippedWeapon instanceof Weapon)
                ? ((Weapon) equippedWeapon).getAttack()
                : 1;

        int playerTileX = worldX / gp.tileSize;
        int playerTileY = worldY / gp.tileSize;

        for (Entity monster : gp.monster[gp.currentMap]) {
            if (monster != null && !monster.isDead) {
                int monsterTileX = monster.worldX / gp.tileSize;
                int monsterTileY = monster.worldY / gp.tileSize;

                boolean inAttackRange = Math.abs(playerTileX - monsterTileX) <= 1 &&
                        Math.abs(playerTileY - monsterTileY) <= 1;

                if (inAttackRange) {
                    monster.life -= attackDamage;
                    if (monster.life < 0) monster.life = 0;
                }
            }
        }
    }

    /**
     * Applies damage to the player, reduced by total defense.
     *
     * @param damage raw incoming damage
     */
    public void receiveDamage(int damage) {
        float totalDefense = getTotalDefense();
        int reducedDamage = Math.max(0, damage - (int) totalDefense);
        life -= reducedDamage;
        if (life < 0) life = 0;
        isHit = true;
        hitEffectCounter = 60;
    }

    /**
     * Removes one unit of item at given index in inventory.
     *
     * @param index index of item in inventory
     */
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

    /**
     * Removes one unit of the specified item by name.
     *
     * @param name item name
     * @return true if item was found and removed
     */
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

    /**
     * Heals the player based on the healing amount of a consumed item.
     *
     * @param item healing item to consume
     */
    public void consumeHealingItem(ChestInventoryManager.ItemData item) {
        float healAmountFloat;
        switch (item.getName()) {
            case "Apple" -> healAmountFloat = ((Item_Apple) item.getItem()).getHealAmount();
            case "blubbery" -> healAmountFloat = ((Item_Blubbery) item.getItem()).getHealAmount();
            case "potion" -> healAmountFloat = ((Item_HealthePotion) item.getItem()).getHealAmount();
            default -> { return; }
        }
        int healAmount = Math.round(healAmountFloat);
        life = Math.min(life + healAmount, maxLife);
    }

    /**
     * Draws the player sprite and damage effect overlay if hit.
     *
     * @param g2d the graphics context
     */
    @Override
    public void draw(Graphics2D g2d) {
        super.draw(g2d);

        if (isHit) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setColor(Color.RED);
            g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        if (isAttacking) {
            float progress = 1.0f - ((float) attackEffectCounter / ATTACK_EFFECT_DURATION);

            float opacity = 0.8f * (1.0f - progress);
            if (opacity < 0) opacity = 0;

            int currentSize = (int) (gp.tileSize * 2.5 * progress);

            int x = screenX + (gp.tileSize / 2) - (currentSize / 2);
            int y = screenY + (gp.tileSize / 2) - (currentSize / 2);

            int startAngle = 0;
            int arcAngle = 90;

            switch (direction) {
                case "up" -> startAngle = 45;
                case "down" -> startAngle = 225;
                case "left" -> startAngle = 135;
                case "right" -> startAngle = 315;
            }

            g2d.setColor(Color.WHITE);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g2d.setStroke(new BasicStroke(4)); // Толщина волны 4 пикселя

            g2d.drawArc(x, y, currentSize, currentSize, startAngle, arcAngle);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setStroke(new BasicStroke(1));
        }
    }

    /** Equips armor to a specific armor slot. */
    public void equipArmor(GameObject armor, int slot) {
        if (slot >= 0 && slot < equippedArmor.length) {
            equippedArmor[slot] = armor;
        }
    }

    /** @return array of all equipped armor pieces */
    public GameObject[] getEquippedArmor() {
        return equippedArmor;
    }

    /** @return total defense value from equipped armor */
    public float getTotalDefense() {
        float total = 0;
        for (GameObject obj : equippedArmor) {
            if (obj instanceof Armor armor) {
                total += armor.getDefensAmount();
            }
        }
        return total;
    }

    /** Equips a weapon to the player. */
    public void equipWeapon(GameObject weapon) {
        this.equippedWeapon = weapon;
    }

    /** @return currently equipped weapon */
    public GameObject getEquippedWeapon() {
        return equippedWeapon;
    }

    /** Equips a grade (badge or token) to the player. */
    public void equipGrade(GameObject grade) {
        this.equippedGrade = grade;
    }

    /** @return currently equipped grade */
    public GameObject getEquippedGrade() {
        return equippedGrade;
    }

    /**
     * Resets the player's state and inventory.
     */
    public void reset() {
        setDefaulteValues();
        inventory.clear();
        life = maxLife;
        Arrays.fill(equippedArmor, null);
        equippedWeapon = null;
        equippedGrade = null;
    }

    /**
     * Adds an item to the player's inventory.
     *
     * @param item item to add
     */
    public void addItem(ChestInventoryManager.ItemData item) {
        ItemType t = item.getType();
        if (t == ItemType.HEALING || t == ItemType.KEY || t == ItemType.KEY_PART) {
            inventory.add(new ChestInventoryManager.ItemData(item.getName(), 1));
        }
    }

    /**
     * @return list of all items in the player's inventory
     */
    public List<ChestInventoryManager.ItemData> getInventory() {
        return inventory;
    }
}

