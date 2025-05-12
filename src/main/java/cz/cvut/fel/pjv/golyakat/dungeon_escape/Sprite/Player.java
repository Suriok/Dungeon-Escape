package cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.KeyHandler;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Apple;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Blubbery;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_HealthePotion;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
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
 * The {@code Player} class represents the main character controlled by the player.
 * <p>
 * It contains logic for movement, attacking, equipment, interaction, and rendering,
 * including support for drag-and-drop mechanics and map switching.
 * </p>
 */
public class Player extends Entity {

    /** Reference to the main game panel. */
    gamePanel gp;

    /** Keyboard handler for movement and actions. */
    KeyHandler keyH;

    /** Player's position on the screen (always centered). */
    public final int screenX;
    public final int screenY;

    /** Sprite images for movement animation in all directions. */
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;

    /** Current movement direction (e.g., "up", "down"). */
    public String direction;

    /** Frame counter for sprite animation. */
    public int spriteCounter = 0;

    /** Index of the currently displayed sprite image. */
    public int spriteNum = 1;

    // === COMBAT & ANIMATION ===

    /** Flag indicating whether the player is currently attacking. */
    private boolean isAttacking = false;

    /** Counter for attack animation frames. */
    private int attackAnimationCounter = 0;

    /** Duration of an attack in frames. */
    private static final int ATTACK_ANIMATION_DURATION = 20;

    // === INVENTORY & EQUIPMENT ===

    /** Player's inventory – list of items with quantity. */
    private List<ChestInventoryManager.ItemData> inventory;

    /** Array of equipped armor pieces (helmet, bib, pants, boots). */
    private GameObject[] equippedArmor;

    /** Currently equipped weapon. */
    private GameObject equippedWeapon;

    /** Special slot for grade/upgrades. */
    private GameObject equippedGrade;

    /** Maximum distance the player can attack. */
    private static final int ATTACK_RANGE = 96; // 2 tiles

    /** Flag indicating whether the player has just been hit. */
    public boolean isHit = false;

    /** Counter for displaying the hit effect. */
    private int hitEffectCounter = 0;

    /** Tile ID representing the ladder used for switching maps. */
    private static final int LADDER_TILE = 10;

    // === CONSTRUCTOR ===

    /**
     * Creates a new player with references to the game panel and keyboard handler.
     * <p>
     * Sets default values, loads sprites, and initializes inventory and equipment.
     * </p>
     */
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

    /**
     * Sets the default position, speed, and health of the player based on the current map.
     */
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

    /**
     * Loads sprite images for movement animation in four directions.
     */
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the player's state – movement, collision, animation, interaction, and health.
     * <p>
     * This method is called every frame in the main game loop.
     * </p>
     */
    public void update() {
        collisionOn = false;
        int oldX = worldX;
        int oldY = worldY;

        // === Movement based on pressed keys ===
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";

            switch (direction) {
                case "up"    -> worldY -= speed;
                case "down"  -> worldY += speed;
                case "left"  -> worldX -= speed;
                case "right" -> worldX += speed;
            }

            gp.collisionChecker.checkTiles(this);
            gp.collisionChecker.checkObject(this, true);

            // === Switch to another map if the player steps on a ladder ===
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

            // === Collision detection ===
            if (collisionOn) {
                worldX = oldX;
                worldY = oldY;
                GameLogger.info("Kolize – návrat na původní pozici: (" + oldX / gp.tileSize + ", " + oldY / gp.tileSize + ")");
            }

            spriteCounter++;
            if (spriteCounter > 15) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }

        // === Attack animation ===
        attackCounter++;
        if (isAttacking) {
            attackAnimationCounter++;
            if (attackAnimationCounter >= ATTACK_ANIMATION_DURATION) {
                isAttacking = false;
                attackAnimationCounter = 0;
            }
        }

        // === Attack animation ===
        int interactionIndex = gp.collisionChecker.checkObjectForInteraction(this, true);
        if (keyH.ePressed && interactionIndex != 999 && gp.obj[gp.currentMap][interactionIndex] != null) {
            if (gp.obj[gp.currentMap][interactionIndex].name.equals("DoorSide")) {
                Object_DoorSide door = (Object_DoorSide) gp.obj[gp.currentMap][interactionIndex];
                if (door.requiresKey && !door.isOpen()) {
                    ChestInventoryManager.ItemData keyItem = inventory.stream()
                            .filter(item -> item.getName().equals("Key"))
                            .findFirst().orElse(null);
                    if (keyItem != null) {
                        door.unlock();
                        inventory.remove(keyItem);
                        GameLogger.info("Dveře odemčeny pomocí klíče.");
                    } else {
                        GameLogger.info("Chybí klíč pro otevření dveří.");
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

        // === Use item to heal using key F ===

        if (keyH.fPressed && !inventory.isEmpty()) {
            ChestInventoryManager.ItemData item = inventory.get(0);
            consumeHealingItem(item);
            inventory.remove(0);
            keyH.fPressed = false;
        }

        // === Use item to heal using key F ===

        if (isHit) {
            hitEffectCounter--;
            if (hitEffectCounter <= 0) {
                isHit = false;
            }
        }

        // === Use item to heal using key F ===

        if (life <= 0) {
            gp.gameState = gp.gameOverState;
        }
    }

    /**
     * Executes a player attack – applies damage to the nearest monster within range.
     * <p>
     * Uses the currently equipped weapon and starts the attack animation.
     * </p>
     */
    public void attack() {
        if (equippedWeapon == null) {
            GameLogger.info("Hráč se pokusil zaútočit bez vybavené zbraně.");
            return;
        }

        isAttacking = true;
        attackAnimationCounter = 0;

        int attackDamage = (equippedWeapon instanceof Weapon)
                ? ((Weapon) equippedWeapon).getAttack() : 1;

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
            if (target.life < 0) target.life = 0;
            GameLogger.info("Hráč zasáhl " + target.name + " za " + attackDamage + " HP. Zbývá: " + target.life);
        } else {
            GameLogger.info("Hráč útočil, ale nic nezasáhl.");
        }
    }

    /**
     * Zpracuje příjem poškození včetně odečtu životů, efektu a zvuku zásahu.
     *
     * @param damage hodnota poškození, které hráč obdrží
     */
    public void receiveDamage(int damage) {
        float totalDefense = getTotalDefense();
        int reducedDamage = Math.max(0, damage - (int) totalDefense);
        life -= reducedDamage;
        if (life < 0) life = 0;

        gp.playSE(2);
        isHit = true;
        hitEffectCounter = 60;

        GameLogger.info("Hráč obdržel " + damage + " poškození (po obraně " + reducedDamage + ")");
    }

    /**
     * Přidá předmět do inventáře hráče.
     *
     * @param item předmět k přidání
     */
    public void addItem(ChestInventoryManager.ItemData item) {
        inventory.add(new ChestInventoryManager.ItemData(item.getName(), 1));
    }

    /** @return seznam všech předmětů v hráčově inventáři */
    public List<ChestInventoryManager.ItemData> getInventory() {
        return inventory;
    }

    /**
     * Odebere jeden kus dané položky z inventáře nebo celou položku, pokud je množství 1.
     *
     * @param index index položky k odebrání
     */
    public void removeItem(int index) {
        if (index >= 0 && index < inventory.size()) {
            ChestInventoryManager.ItemData item = inventory.get(index);
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                GameLogger.info("Zmenšeno množství položky: " + item.getName());
            } else {
                inventory.remove(index);
                GameLogger.info("Položka \"" + item.getName() + "\" odebrána z inventáře.");
            }
        } else {
            GameLogger.error("Neplatný index při odebírání položky z inventáře: " + index);
        }
    }

    /**
     * Použije léčivý předmět – obnoví životy hráči.
     *
     * @param item léčivý předmět
     */
    public void consumeHealingItem(ChestInventoryManager.ItemData item) {
        float healAmountFloat = 0;
        switch (item.getName()) {
            case "Apple" -> healAmountFloat = ((Item_Apple) item.getItem()).getHealAmount();
            case "blubbery" -> healAmountFloat = ((Item_Blubbery) item.getItem()).getHealAmount();
            case "potion" -> healAmountFloat = ((Item_HealthePotion) item.getItem()).getHealAmount();
            default -> {
                GameLogger.info("Položka " + item.getName() + " není léčivý předmět.");
                return;
            }
        }
        int healAmount = Math.round(healAmountFloat);
        life = Math.min(life + healAmount, maxLife);
        GameLogger.info("Hráč se vyléčil pomocí " + item.getName() + " na " + life + " HP.");
    }


    /**
     * Vykreslí hráče na obrazovku v aktuální animaci a směru.
     *
     * @param g2d grafický kontext
     */
    @Override
    public void draw(Graphics2D g2d) {
        BufferedImage imageToDraw = null;

        // Výběr správného sprite podle směru a animace
        switch (direction) {
            case "up" -> imageToDraw = (spriteNum == 1) ? up1 : up2;
            case "down" -> imageToDraw = (spriteNum == 1) ? down1 : down2;
            case "left" -> imageToDraw = (spriteNum == 1) ? left1 : left2;
            case "right" -> imageToDraw = (spriteNum == 1) ? right1 : right2;
        }

        this.image = imageToDraw;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);

        if (isHit) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.setColor(Color.RED);
            g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        if (isAttacking && equippedWeapon != null) {
            drawWaveEffect(g2d);
        }
    }

    /**
     * Vykreslí efekt "vlny" při útoku (sinusová křivka) ve směru útoku.
     *
     * @param g2d grafický kontext
     */
    private void drawWaveEffect(Graphics2D g2d) {
        float animationProgress = (float) attackAnimationCounter / ATTACK_ANIMATION_DURATION;

        g2d.setColor(new Color(255, 255, 255, (int) (255 * (1.0f - animationProgress))));
        g2d.setStroke(new BasicStroke(3));

        int waveWidth = gp.tileSize;
        int waveHeight = gp.tileSize / 2;
        int waveStartX = screenX + gp.tileSize / 2;
        int waveStartY = screenY + gp.tileSize / 2;

        int[] xPoints = new int[5];
        int[] yPoints = new int[5];
        int waveOffset = (int) (animationProgress * gp.tileSize);

        switch (direction) {
            case "up" -> {
                for (int i = 0; i < 5; i++) {
                    xPoints[i] = waveStartX - waveWidth / 2 + (i * waveWidth / 4);
                    yPoints[i] = waveStartY - waveOffset + (int) (Math.sin(i * Math.PI / 4) * waveHeight);
                }
            }
            case "down" -> {
                for (int i = 0; i < 5; i++) {
                    xPoints[i] = waveStartX - waveWidth / 2 + (i * waveWidth / 4);
                    yPoints[i] = waveStartY + waveOffset - (int) (Math.sin(i * Math.PI / 4) * waveHeight);
                }
            }
            case "left" -> {
                for (int i = 0; i < 5; i++) {
                    yPoints[i] = waveStartY - waveWidth / 2 + (i * waveWidth / 4);
                    xPoints[i] = waveStartX - waveOffset + (int) (Math.sin(i * Math.PI / 4) * waveHeight);
                }
            }
            case "right" -> {
                for (int i = 0; i < 5; i++) {
                    yPoints[i] = waveStartY - waveWidth / 2 + (i * waveWidth / 4);
                    xPoints[i] = waveStartX + waveOffset - (int) (Math.sin(i * Math.PI / 4) * waveHeight);
                }
            }
        }

        g2d.drawPolyline(xPoints, yPoints, 5);
        g2d.setStroke(new BasicStroke(1));
    }

    /**
     * Vybaví hráče daným kusem brnění do příslušného slotu.
     *
     * @param armor objekt brnění
     * @param slot index slotu (0–3: helma, vesta, kalhoty, boty)
     */
    public void equipArmor(GameObject armor, int slot) {
        if (slot >= 0 && slot < equippedArmor.length) {
            equippedArmor[slot] = armor;
            GameLogger.info("Equipped armor: " + armor.name + " in slot " + slot);
        }
    }

    /**
     * Sundá brnění ze zvoleného slotu.
     *
     * @param slot index slotu (0–3)
     */
    public void unequipArmor(int slot) {
        if (slot >= 0 && slot < equippedArmor.length) {
            GameLogger.info("Unequipped armor from slot " + slot);
            equippedArmor[slot] = null;
        }
    }

    /**
     * Vrací pole aktuálně nasazeného brnění.
     *
     * @return pole {@link GameObject} pro jednotlivé části výstroje
     */
    public GameObject[] getEquippedArmor() {
        return equippedArmor;
    }

    /**
     * Spočítá celkovou obranu hráče ze všech nasazených kusů brnění.
     *
     * @return součet obranných hodnot všech brnění
     */
    public float getTotalDefense() {
        float totalDefense = 0;
        for (int i = 0; i < equippedArmor.length; i++) {
            if (equippedArmor[i] instanceof Armor) {
                float defense = ((Armor) equippedArmor[i]).getDefensAmount();
                totalDefense += defense;
            } else if (equippedArmor[i] != null) {
            }
        }
        return totalDefense;
    }

    /** @param weapon zbraň, která bude nasazena */
    public void equipWeapon(GameObject weapon) {
        this.equippedWeapon = weapon;
        GameLogger.info("Nasazena zbraň: " + weapon.name);
    }

    /** Sundá aktuálně nasazenou zbraň. */
    public void unequipWeapon() {
        GameLogger.info("Zbraň byla sundána.");
        this.equippedWeapon = null;
    }

    /** @return aktuálně nasazená zbraň */
    public GameObject getEquippedWeapon() {
        return equippedWeapon;
    }

    /** @param grade vylepšení (grade), které se má nasadit */
    public void equipGrade(GameObject grade) {
        this.equippedGrade = grade;
        GameLogger.info("Nasazeno vylepšení: " + (grade != null ? grade.name : "žádné"));
    }

    /** Sundá aktuální vylepšení (grade). */
    public void unequipGrade() {
        GameLogger.info("Vylepšení bylo sundáno.");
        this.equippedGrade = null;
    }

    /** @return aktuálně nasazené vylepšení */
    public GameObject getEquippedGrade() {
        return equippedGrade;
    }

    /**
     * Použije klíč z inventáře k odemčení zadaných dveří.
     *
     * @param doorIndex index dveří v poli objektů
     */
    public void useKeyOnDoor(int doorIndex) {
        if (doorIndex != 999 && gp.obj[gp.currentMap][doorIndex] != null &&
                gp.obj[gp.currentMap][doorIndex].name.equals("DoorSide")) {
            Object_DoorSide door = (Object_DoorSide) gp.obj[gp.currentMap][doorIndex];
            if (door.requiresKey && !door.isOpen()) {
                ChestInventoryManager.ItemData keyItem = inventory.stream()
                        .filter(item -> item.getName().equals("Key"))
                        .findFirst().orElse(null);
                if (keyItem != null) {
                    door.unlock();
                    inventory.remove(keyItem);
                    GameLogger.info("Dveře byly úspěšně odemčeny.");
                } else {
                    GameLogger.info("Hráč nemá potřebný klíč.");
                }
            }
        }
    }

    /**
     * Resetuje stav hráče do výchozího stavu.
     * <p>
     * Používá se při restartu hry nebo návratu na základní pozici.
     * </p>
     */
    public void reset() {
        setDefaulteValues();
        getInventory().clear();
        Arrays.fill(equippedArmor, null);
        equippedWeapon = null;
        equippedGrade = null;
    }
}