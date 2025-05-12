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
 * Třída {@code Player} reprezentuje hlavní postavu ovládanou hráčem.
 * <p>
 * Obsahuje logiku pohybu, útoku, vybavení, interakcí a vykreslování hráče,
 * včetně podpory drag-and-drop mechanismu a výměny map.
 * </p>
 */
public class Player extends Entity {

    /** Odkaz na hlavní herní panel. */
    gamePanel gp;

    /** Ovladač klávesnice pro pohyb a akce. */
    KeyHandler keyH;

    /** Souřadnice hráče na obrazovce (vždy uprostřed). */
    public final int screenX;
    public final int screenY;

    /** Sprite obrázky pro animaci pohybu ve všech směrech. */
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;

    /** Aktuální směr pohybu (např. "up", "down"). */
    public String direction;

    /** Počítadlo snímků pro střídání sprite animace. */
    public int spriteCounter = 0;

    /** Index aktuálně zobrazeného sprite obrázku. */
    public int spriteNum = 1;

    // === BOJ & ANIMACE ===

    /** Příznak, zda hráč právě útočí. */
    private boolean isAttacking = false;

    /** Počítadlo snímků útoku. */
    private int attackAnimationCounter = 0;

    /** Délka trvání útoku v počtu snímků. */
    private static final int ATTACK_ANIMATION_DURATION = 20;

    // === INVENTÁŘ & VYBAVENÍ ===

    /** Inventář hráče – seznam předmětů s množstvím. */
    private List<ChestInventoryManager.ItemData> inventory;

    /** Pole vybaveného brnění (helmet, bib, pants, boots). */
    private GameObject[] equippedArmor;

    /** Aktuálně vybavená zbraň hráče. */
    private GameObject equippedWeapon;

    /** Speciální slot pro vylepšení (grade). */
    private GameObject equippedGrade;

    /** Maximální vzdálenost, na kterou hráč může zaútočit. */
    private static final int ATTACK_RANGE = 96; // 2 tiles

    /** Příznak, zda hráč právě obdržel poškození. */
    public boolean isHit = false;

    /** Počítadlo trvání zobrazení efektu zásahu. */
    private int hitEffectCounter = 0;

    /** Číselné označení dlaždice s žebříkem pro přechod mezi mapami. */
    private static final int LADDER_TILE = 10;

    // === KONSTRUKTOR ===

    /**
     * Vytváří nového hráče s referencí na panel a ovladač klávesnice.
     * <p>
     * Nastavuje výchozí hodnoty, načítá obrázky a inicializuje inventář a vybavení.
     * </p>
     *
     * @param gp   herní panel
     * @param keyH ovladač klávesnice
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
     * Nastaví výchozí pozici, rychlost a zdraví hráče podle aktuální mapy.
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
     * Načte obrázky (sprite) pro animace pohybu ve čtyřech směrech.
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
     * Aktualizuje stav hráče – pohyb, kolize, animace, interakce a stav zdraví.
     * <p>
     * Tato metoda se volá v hlavní herní smyčce při každém snímku.
     * </p>
     */
    public void update() {
        collisionOn = false;
        int oldX = worldX;
        int oldY = worldY;

        // === Pohyb podle stisknutých kláves ===
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

            // === Přechod na jinou mapu, pokud hráč vstoupí na žebřík ===
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

            // === Detekce kolizí ===
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

        // === Animace útoku ===
        attackCounter++;
        if (isAttacking) {
            attackAnimationCounter++;
            if (attackAnimationCounter >= ATTACK_ANIMATION_DURATION) {
                isAttacking = false;
                attackAnimationCounter = 0;
            }
        }

        // === Interakce s objekty pomocí klávesy E ===
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

        // === Použití předmětu k léčení pomocí klávesy F ===
        if (keyH.fPressed && !inventory.isEmpty()) {
            ChestInventoryManager.ItemData item = inventory.get(0);
            consumeHealingItem(item);
            inventory.remove(0);
            keyH.fPressed = false;
        }

        // === Efekt zásahu ===
        if (isHit) {
            hitEffectCounter--;
            if (hitEffectCounter <= 0) {
                isHit = false;
            }
        }

        // === Konec hry, pokud hráč ztratil všechny životy ===
        if (life <= 0) {
            gp.gameState = gp.gameOverState;
        }
    }

    /**
     * Provede útok hráče – aplikuje zranění nejbližšímu monstru v dosahu.
     * <p>
     * Používá aktuálně vybavenou zbraň a spustí animaci útoku.
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