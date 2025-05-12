package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Player;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.iron_bib;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.iron_boots;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.iron_helmet;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.iron_pants;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_bib;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_boots;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_helmet;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_pants;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.bars.DefensBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.bars.HealthBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Apple;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Blubbery;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_HealthePotion;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Key;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss.Boss_Eye;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss.Boss_Goblin;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Skeleton;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Slime;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Zombie;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_CraftingTable;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.tile.TileManger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.Emerald_sword;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.Iron_sword;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * The {@code gamePanel} class serves as the main game panel that manages game logic, rendering,
 * game loop, interactions, UI, and game saving/loading.
 * <p>
 * It uses components such as {@link Player}, {@link GameObject}, {@link HealthBar}, {@link MonsterUI}, {@link TileManger}, etc.
 * Contains its own game loop using the {@link Runnable} interface, tracks game state, and ensures transitions between scenes.
 * </p>
 */

public class gamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS

    /** The base tile size in pixels before scaling. */
    final int originalTileSize = 16;

    /** The factor by which the base tile size is multiplied for rendering. */
    final int scale = 3;

    /** The size of a single tile in pixels after scaling. */
    public final int tileSize = originalTileSize * scale;

    /** The number of horizontal tiles visible on screen. */
    public final int maxScreenCol = 20;

    /** The number of vertical tiles visible on screen. */
    public final int maxScreenRow = 12;

    /** The total width of the game panel in pixels. */
    public final int screenWidth = tileSize * maxScreenCol;

    /** The total height of the game panel in pixels. */
    public final int screenHeight = tileSize * maxScreenRow;

    //WORLD SETTINGS

    /** The number of columns in the world map grid. */
    public final int maxWorldCol = 60;

    /** The number of rows in the world map grid. */
    public final int maxWorldRow = 60;

    /** The total number of distinct maps (levels) in the game. */
    public final int maxMap = 2; // number of all levels

    /** The index of the currently active map (0-based). */
    public int currentMap = 0; // current map index

    // FPS

    /** The target frames per second for the game loop. */
    int FPS = 60;

    // SYSTEM

    /** Manages tile loading and rendering logic. */
    public TileManger tileH = new TileManger(this);

    /** Handles keyboard input events. */
    public KeyHandler keyH = new KeyHandler();

    /** The main thread running the game loop. */
    Thread gameThread;

    /** Checks for and resolves collisions between entities and tiles. */
    public Collision collisionChecker = new Collision(this);

    /** Responsible for placing objects and monsters in the world. */
    public AssetSetter assetSetter;

    /** UI handler for the title screen overlay. */
    private final TitleScreenUI titleUi;

    /** Manages game audio playback. */
    Sound sound = new Sound();

    // ENTITY AND OBJECT

    /** The player character instance. */
    public Player player = new Player(this, keyH);

    /** Array of interactive world objects per map. */
    public GameObject[][] obj = new GameObject[maxMap][11];


    /** UI element showing the player's health bar. */
    public HealthBar healthBar;

    /** UI element showing the player's defense bar. */
    public DefensBar defensBar;

    /** Array of monsters per map. */
    public Entity[][] monster = new Entity[maxMap][20];

    /** UI element displaying monster health/info. */
    public MonsterUI monsterUi;
    /**
     * Determines whether a given map has already been initialized (monsters, objects).
     * Used to ensure spawning occurs only once.
     */
    public boolean[] levelSpawned = new boolean[maxMap];

    // GAME STATE

    /** The current primary state of the game (title, playing, game over). */
    public int gameState;

    /** State constant for the title screen. */
    public final int titleState = 0;

    /** State constant for active gameplay. */
    public final int playerState = 1;

    /** State constant for the game-over screen. */
    public final int gameOverState = 2;

    // UI MANAGERS

    /** UI for displaying and interacting with chests. */
    public ChestUI chestUI;

    /** UI for the crafting table. */
    public CraftingTableUI craftingTableUI;

    /** UI for the player inventory and equipped items. */
    public PlayerUI playerUI;

    /** Manages chest inventory data and drag-and-drop operations. */
    public ChestInventoryManager chestInventoryManager;

    // COMBAT

    /** Counter to enforce attack cooldown between clicks. */
    private int attackCounter = 0;

    /** Number of update ticks required between attacks. */
    private static final int ATTACK_COOLDOWN = 30;

    // Drag-and-drop variables

    /** The item currently being dragged by the player. */
    public ChestInventoryManager.ItemData draggedItem = null;

    /** The source container (player, chest, or crafting table) of the dragged item. */
    private Object sourceInventory = null; // Player, Chest, CraftingTableUI

    /** The index of the dragged item within its source container. */
    private int draggedItemIndex = -1;

    /** X offset between mouse pointer and top-left of dragged item sprite. */
    private int dragOffsetX, dragOffsetY;

    /** Whether the collision event has been logged this drag operation. */
    private boolean collisionLogged = false;

    /** Whether the drop event has been logged this drag operation. */
    private boolean dragDroppedLogged = false;

    /** Whether the dragged item originated from an armor slot. */
    private boolean draggedFromArmor = false;

    // MESSAGES
    /** Hint message shown when near a door. */
    public HintMessage doorMessage = new HintMessage();

    /** Hint message shown specifically for door unlock hints. */
    public HintMessage doorHintMessage = new HintMessage();

    /** Hint message shown when near a chest. */
    public HintMessage chestMessage = new HintMessage();

    /** Hint message shown when near a healing station or item. */
    public HintMessage healingHintMessage = new HintMessage();

    /** Hint message shown when near the crafting table. */
    public HintMessage craftingHintMessage = new HintMessage();

    // UI MESSAGE SETTINGS

    /** Font size used for on-screen messages. */
    private final int messageFontSize = 20;

    /** Line height (vertical spacing) between message lines. */
    private final int messageLineHeight = 30;

    /** X coordinate at which messages are drawn (from the left). */
    private final int messageX = screenWidth - tileSize * 7;

    /** Y coordinate at which the first line of messages is drawn. */
    private final int messageY = 25;

    // HINT MESSAGE

    /**
     * Represents a transient on-screen hint message with a timer and proximity flag.
     */
    public class HintMessage {
        /** The text content of the hint. */
        public String text = "";

        /** Remaining ticks before the hint is cleared. */
        public int counter = 0;

        /** Whether the related object (door, chest, etc.) is near the player. */
        public boolean near = false;

        /**
         * Shows a new hint message for a specified duration.
         *
         * @param message the hint text to display
         * @param duration number of ticks the hint remains visible
         * @param isNear whether the player is near the related object
         */
        public void show(String message, int duration, boolean isNear) {
            this.text = message;
            this.counter = duration;
            this.near = isNear;
        }

        /**
         * Updates the visibility timer and clears text when expired.
         */
        public void update() {
            if (counter > 0) counter--;
            if (counter <= 0) text = "";
        }

        /**
         * Checks if the hint message is currently visible.
         *
         * @return true if the text is non-empty; false otherwise
         */
        public boolean isVisible() {
            return !text.isEmpty();
        }
    }

    // ---------- SAVE / LOAD ----------
    private static final Path SAVE_PATH = Path.of("saved_game.xml");
    private final XmlMapper xml = new XmlMapper();

    /**
     * Creates and returns an instance of the appropriate game item or equipment based on the name.
     * <p>
     * Used when restoring the game from a saved state to reconstruct the correct {@link GameObject} type from the name (saved in XML).
     * </p>
     *
     * @param name item name according to save file
     * @return instance of the object corresponding to the name or {@code null} if not recognized
     */
    private GameObject makeItem(String name) {
        switch (name) {
            case "Apple":
                return new Item_Apple();
            case "blubbery":
                return new Item_Blubbery();
            case "potion":
                return new Item_HealthePotion();
            case "Key":
            case "Key1":
            case "Key2":
            case "Key3":
            case "SilverKey":
                return new Item_Key();
            case "leather_helmet":
                return new leather_helmet();
            case "leather_bib":
                return new leather_bib();
            case "leather_pants":
                return new leather_pants();
            case "leather_boots":
                return new leather_boots();
            case "iron_helmet":
                return new iron_helmet();
            case "iron_bib":
                return new iron_bib();
            case "iron_pants":
                return new iron_pants();
            case "iron_boots":
                return new iron_boots();
            case "iron_sword":
                return new Iron_sword(2);
            case "emerald_sword":
                return new Emerald_sword(3);
            default:
                GameLogger.error("Unknown item: " + name);
                return null;
        }
    }

    /**
     * Creates a {@link SaveData} object containing the complete game state for saving to XML.
     * The output object stores:
     * <ul>
     *     <li>Player position and health</li>
     *     <li>Inventory, armor, weapon, and upgrades</li>
     *     <li>Monster states on the current map</li>
     *     <li>Chest contents</li>
     *     <li>Information about which maps have already been spawned</li>
     * </ul>
     *
     * @return prepared {@link SaveData} object for XML serialization
     */
    public SaveData buildSaveData() {
        SaveData data = new SaveData();

        data.player.worldX = player.worldX;
        data.player.worldY = player.worldY;
        data.player.life = player.life;
        GameLogger.info("Saving player position: (" + player.worldX + ", " + player.worldY + "), HP: " + player.life);

        List<ChestInventoryManager.ItemData> inventory = player.getInventory();
        if (inventory != null && !inventory.isEmpty()) {
            inventory.forEach(it -> {
                if (it != null && it.getName() != null) {
                    data.player.backpack.add(new SaveData.ItemData(it.getName(), it.getQuantity()));
                    GameLogger.info("Saving inventory item: " + it.getName() + " x" + it.getQuantity());
                }
            });
        } else {
            GameLogger.info("Player inventory is null or empty");
        }

        data.currentMap   = currentMap;
        data.levelSpawned = levelSpawned.clone();

        GameObject[] equippedArmor = player.getEquippedArmor();
        if (equippedArmor != null) {
            for (int i = 0; i < equippedArmor.length; i++) {
                if (equippedArmor[i] != null && equippedArmor[i].name != null) {
                    data.player.armor.add(new SaveData.ItemData(equippedArmor[i].name, 1));
                    GameLogger.info("Saving armor in slot " + i + ": " + equippedArmor[i].name);
                }
            }
        } else {
            GameLogger.info("Equipped armor array is null");
        }
        if (data.player.armor.isEmpty()) {
            GameLogger.info("No equipped armor to save");
        }

        GameObject weapon = player.getEquippedWeapon();
        if (weapon != null && weapon.name != null) {
            data.player.weapon = new SaveData.ItemData(weapon.name, 1);
            GameLogger.info("Saving weapon: " + weapon.name);
        } else {
            GameLogger.info("No equipped weapon to save");
        }

        GameObject grade = player.getEquippedGrade();
        if (grade != null && grade.name != null) {
            data.player.grade = new SaveData.ItemData(grade.name, 1);
            GameLogger.info("Saving grade: " + grade.name);
        } else {
            GameLogger.info("No equipped grade to save");
        }

        for (Entity m : monster[currentMap]) {
            if (m != null) {
                SaveData.MonsterData md = new SaveData.MonsterData();
                md.type = m.getClass().getSimpleName();
                md.worldX = m.worldX;
                md.worldY = m.worldY;
                md.life = m.life;
                md.dead = m.isDead;
                data.monsters.add(md);
                GameLogger.info("Saving monster " + md.type + " at (" + md.worldX + ", " + md.worldY + ")");
            }
        }

        chestInventoryManager.forEachChest((id, list) -> {
            SaveData.ChestData cd = new SaveData.ChestData();
            cd.id = id;
            if (list != null) {
                list.forEach(it -> {
                    if (it != null && it.getName() != null) {
                        cd.items.add(new SaveData.ItemData(it.getName(), it.getQuantity()));
                        GameLogger.info("Saving chest " + cd.id + " item: " + it.getName() + " x" + it.getQuantity());
                    }
                });
            }
            data.chests.add(cd);
        });

        return data;
    }

    /**
     * Restores the entire game state from the given {@link SaveData} object.
     * <p>
     * Performs the following steps:
     * <ol>
     *     <li>Restores current map and environment initialization</li>
     *     <li>Sets player position and health</li>
     *     <li>Reconstructs inventory, equipment, weapon, and upgrades</li>
     *     <li>Loads monsters and their positions/states</li>
     *     <li>Restores contents of individual chests</li>
     * </ol>
     * If any object or type cannot be restored, an error message is printed.
     * </p>
     *
     * @param d {@link SaveData} object loaded from XML file
     */
    private void restoreFromSave(SaveData d) {
        currentMap = d.currentMap;                           // default 0 if the XML attribute is missing
        if (d.levelSpawned != null && d.levelSpawned.length == levelSpawned.length) {
            levelSpawned = d.levelSpawned.clone();
        } else {
            Arrays.fill(levelSpawned, false);
        }

        /* 2. Set up the environment for the required map */
        assetSetter.setObg();                 // re-place doors/chests/etc.
        tileH.findWalkableRegions();          // recalculate walkable areas

        if (!levelSpawned[currentMap]) {      // if monsters have not yet been spawned on this map
            assetSetter.setMonster();
            levelSpawned[currentMap] = true;
        }

        /* 3. Reset player entity based on the new currentMap */
        player.reset();                       // set starting coordinates for the map

        player.reset();
        player.worldX = d.player.worldX;
        player.worldY = d.player.worldY;
        player.life = d.player.life;
        GameLogger.info("Restored player position: (" + player.worldX + ", " + player.worldY + "), HP: " + player.life);

        // Restore inventory
        if (d.player.backpack != null && !d.player.backpack.isEmpty()) {
            d.player.backpack.forEach(it -> {
                if (it != null && it.name != null) {
                    player.addItem(new ChestInventoryManager.ItemData(it.name, it.qty));
                    GameLogger.info("Restored inventory item: " + it.name + " x" + it.qty);
                }
            });
        } else {
            GameLogger.info("No backpack data to restore");
        }

        // Restore armor
        if (d.player.armor != null && !d.player.armor.isEmpty()) {
            for (int i = 0; i < d.player.armor.size() && i < 4; i++) {
                SaveData.ItemData armorData = d.player.armor.get(i);
                if (armorData != null && armorData.name != null) {
                    GameObject armorObj = makeItem(armorData.name);
                    if (armorObj != null) {
                        player.equipArmor(armorObj, i);
                        GameLogger.info("Restored armor in slot " + i + ": " + armorData.name);
                    } else {
                        GameLogger.error("Failed to restore armor: " + armorData.name);
                    }
                }
            }
        } else {
            GameLogger.info("No armor data to restore");
        }

        // Restore weapon
        if (d.player.weapon != null && d.player.weapon.name != null) {
            GameObject weaponObj = makeItem(d.player.weapon.name);
            if (weaponObj != null) {
                player.equipWeapon(weaponObj);
                GameLogger.info("Restored weapon: " + d.player.weapon.name);
            } else {
                GameLogger.error("Failed to restore weapon: " + d.player.weapon.name);
            }
        } else {
            GameLogger.info("No weapon data to restore");
        }

        // Restore grade
        if (d.player.grade != null && d.player.grade.name != null) {
            GameObject gradeObj = makeItem(d.player.grade.name);
            if (gradeObj != null) {
                player.equipGrade(gradeObj);
                GameLogger.info("Restored grade: " + d.player.grade.name);
            } else {
                GameLogger.error("Failed to restore grade: " + d.player.grade.name);
            }
        } else {
            GameLogger.info("No grade data to restore");
        }

        // Clear existing monsters
        for (Entity[] row : monster) {
            Arrays.fill(row, null);
        }
        // Restore monsters
        if (d.monsters != null && !d.monsters.isEmpty()) {
            for (int i = 0; i < d.monsters.size() && i < monster.length; i++) {
                SaveData.MonsterData md = d.monsters.get(i);
                if (md != null && md.type != null) {
                    switch (md.type) {
                        case "Boss_Goblin":
                            monster[0][i] = new Boss_Goblin(this);
                            break;
                        case "Boss_Eye":
                            monster[1][i] = new Boss_Eye(this);    // map index 1
                            break;
                        case "Monster_Slime":
                            monster[currentMap][i] = new Monster_Slime(this);
                            break;
                        case "Monster_Zombie":
                            monster[currentMap][i] = new Monster_Zombie(this);
                            break;
                        case "Monster_Skeleton":
                            monster[currentMap][i] = new Monster_Skeleton(this);
                            break;
                        default:
                            GameLogger.error("Unknown monster type: " + md.type);
                            continue;
                    }
                    monster[currentMap][i].worldX = md.worldX;
                    monster[currentMap][i].worldY = md.worldY;
                    monster[currentMap][i].life = md.life;
                    monster[currentMap][i].isDead = md.dead;
                    GameLogger.info("Restored monster " + md.type + " at (" + md.worldX + ", " + md.worldY + ")");
                }
            }
        } else {
            GameLogger.info("No monster data to restore");
        }

        chestInventoryManager.resetChestData();
        // Restore chests
        if (d.chests != null && !d.chests.isEmpty()) {
            d.chests.forEach(cd -> {
                if (cd != null && cd.items != null) {
                    chestInventoryManager.overrideChest(cd.id, toItemList(cd.items));
                    GameLogger.info("Restored chest ID " + cd.id + " with " + cd.items.size() + " items");
                }
            });
        } else {
            GameLogger.info("No chest data to restore");
        }
    }


    /**
     * Helper method for converting a list of saved items from {@link SaveData} to internal {@link ChestInventoryManager.ItemData} structure.
     *
     * @param list list of items loaded from XML
     * @return list of {@link ChestInventoryManager.ItemData} objects ready for use in the game
     */
    private List<ChestInventoryManager.ItemData> toItemList(List<SaveData.ItemData> list) {
        List<ChestInventoryManager.ItemData> result = new ArrayList<>();
        if (list != null) {
            list.forEach(e -> {
                if (e != null && e.name != null) {
                    result.add(new ChestInventoryManager.ItemData(e.name, e.qty));
                }
            });
        }
        return result;
    }



    public gamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));// velikost panelu
        this.setBackground(Color.BLACK); // pozadí
        this.setDoubleBuffered(true);// plynulé vykreslování
        this.addKeyListener(keyH);// ovládání přes klávesy
        this.setFocusable(true);  // povolit focus
        this.requestFocusInWindow(); // zaměřit okno pro vstup

        playMusic(0);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        chestInventoryManager = new ChestInventoryManager();
        assetSetter = new AssetSetter(this);
        healthBar = new HealthBar(this);
        defensBar = new DefensBar(this);
        chestUI = new ChestUI(this);
        craftingTableUI = new CraftingTableUI(this);
        playerUI = new PlayerUI(this);
        monsterUi = new MonsterUI(this);
        titleUi = new TitleScreenUI(this);
        gameState = titleState;

        /**
         * Central mouse click processing – initialization of dragging and interaction.
         *
         * Handles:
         * <ul>
         *   <li>Clicking on player inventory and item selection</li>
         *   <li>Equipment selection (armor, weapon)</li>
         *   <li>Clicking on chest and items in it</li>
         *   <li>Dragging from crafting table</li>
         *   <li>Crafting button (creating SilverKey)</li>
         * </ul>
         * Right button is used for attack.
         *
         */
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();

                if (gameState == titleState) {
                    titleUi.mousePressed(e.getPoint());
                    return;
                }

                if (gameState == titleState || gameState == gameOverState) {
                    titleUi.mousePressed(e.getPoint());
                    return;
                }


                if (e.getButton() == MouseEvent.BUTTON3) {
                    if (!chestUI.isShowingInventory() && !craftingTableUI.isShowing()) {
                        if (attackCounter >= ATTACK_COOLDOWN) {
                            player.attack();
                            attackCounter = 0;
                        }
                    }
                    return;
                }

                if (e.getButton() != MouseEvent.BUTTON1) return;

                // Player inventory
                Rectangle inventoryBounds = playerUI.getPlayerInventoryBounds();
                if (inventoryBounds != null && inventoryBounds.contains(e.getPoint())) {
                    int gridCols = 8;
                    int cellWidth = inventoryBounds.width / gridCols;
                    int cellHeight = inventoryBounds.height;
                    int offsetX = inventoryBounds.x + 30;
                    int offsetY = inventoryBounds.y - 3;
                    int itemSize = Math.min(cellWidth, cellHeight);

                    int col = (e.getX() - offsetX) / cellWidth;
                    int row = (e.getY() - offsetY) / cellHeight;
                    int index = row * gridCols + col;

                    java.util.List<ChestInventoryManager.ItemData> expandedItems = new ArrayList<>();
                    for (ChestInventoryManager.ItemData item : player.getInventory()) {
                        int quantity = item.getQuantity();
                        for (int i = 0; i < quantity; i++) {
                            expandedItems.add(new ChestInventoryManager.ItemData(item.getName(), 1));
                        }
                    }

                    if (index >= 0 && index < expandedItems.size()) {
                        draggedItem = new ChestInventoryManager.ItemData(expandedItems.get(index).getName(), 1);
                        sourceInventory = player;
                        draggedFromArmor = false;
                        draggedItemIndex = player.getInventory().indexOf(
                                player.getInventory().stream()
                                        .filter(item -> item.getName().equals(draggedItem.getName()))
                                        .findFirst()
                                        .orElse(null)
                        );
                        dragOffsetX = e.getX() - (offsetX + col * cellWidth + (cellWidth - itemSize) / 2);
                        dragOffsetY = e.getY() - (offsetY + row * cellHeight + (cellHeight - itemSize) / 2);
                    }
                    repaint();
                    return;
                }

                // Armor slots
                Rectangle[] armorBounds = playerUI.getArmorSlotBounds();
                for (int i = 0; i < armorBounds.length; i++) {
                    if (armorBounds[i] != null && armorBounds[i].contains(e.getPoint())) {
                        GameObject armor = player.getEquippedArmor()[i];
                        if (armor != null) {
                            draggedItem = new ChestInventoryManager.ItemData(armor.name, 1);
                            sourceInventory = player;
                            draggedFromArmor = true;
                            draggedItemIndex = i; // Armor slot index
                            dragOffsetX = e.getX() - armorBounds[i].x;
                            dragOffsetY = e.getY() - armorBounds[i].y;
                        }
                        repaint();
                        return;
                    }
                }

                // Weapon slot
                Rectangle weaponBounds = playerUI.getWeaponSlotBounds();
                if (weaponBounds != null && weaponBounds.contains(e.getPoint())) {
                    GameObject weapon = player.getEquippedWeapon();
                    if (weapon != null) {
                        draggedItem = new ChestInventoryManager.ItemData(weapon.name, 1);
                        sourceInventory = player;
                        draggedItemIndex = -2; // Special index for weapon
                        dragOffsetX = e.getX() - weaponBounds.x;
                        dragOffsetY = e.getY() - weaponBounds.y;
                    }
                    repaint();
                    return;
                }

                // Chest inventory
                if (chestUI.isShowingInventory()) {
                    Rectangle[] chestItemBounds = chestUI.getItemBounds();
                    Object_Small_Chest activeChest = chestUI.getActiveChest();
                    if (activeChest != null) {
                        for (int i = 0; i < chestItemBounds.length; i++) {
                            if (chestItemBounds[i] != null && chestItemBounds[i].contains(e.getPoint())) {
                                if (i < activeChest.getItems().size()) {
                                    ChestInventoryManager.ItemData originalItem = activeChest.getItems().get(i);
                                    draggedItem = new ChestInventoryManager.ItemData(originalItem.getName(), 1);
                                    sourceInventory = activeChest;
                                    draggedItemIndex = i;
                                    dragOffsetX = e.getX() - chestItemBounds[i].x;
                                    dragOffsetY = e.getY() - chestItemBounds[i].y;
                                    repaint();
                                    return;
                                }
                            }
                        }
                    }
                }

                // Crafting table slots
                if (craftingTableUI.isShowing()) {
                    Rectangle[] craftSlotBounds = craftingTableUI.getSlotBounds();
                    for (int i = 0; i < craftSlotBounds.length; i++) {
                        if (craftSlotBounds[i] != null && craftSlotBounds[i].contains(e.getPoint())) {
                            ChestInventoryManager.ItemData item = craftingTableUI.getCraftingSlot(i);
                            if (item != null) {
                                draggedItem = item;
                                sourceInventory = craftingTableUI;
                                draggedItemIndex = i;
                                dragOffsetX = e.getX() - craftSlotBounds[i].x;
                                dragOffsetY = e.getY() - craftSlotBounds[i].y;
                                craftingTableUI.setCraftingSlot(i, null);
                                repaint();
                                return;
                            }
                        }
                    }

                    // Craft button
                    Rectangle craftButtonBounds = craftingTableUI.getCraftButtonBounds();
                    if (craftButtonBounds != null && craftButtonBounds.contains(e.getPoint())) {
                        craftingTableUI.craftSilverKey();
                        repaint();
                    }
                }
            }

            /**
             * Processes the mouse release event, especially when working with dragged items (drag-and-drop).
             * <p>
             * This method determines where the item was dropped – into player inventory, onto equipment, into chest, into crafting table, or onto the map (e.g., onto doors).
             * </p>
             *
             * @param e {@link MouseEvent} object containing click coordinates
             */
            @Override
            public void mouseReleased(MouseEvent e) {

                // Pokud jsme na titulní obrazovce nebo v Game Over stavu, předáváme ovládání do UI titulní obrazovky
                if (gameState == titleState || gameState == gameOverState) {
                    titleUi.mouseReleased(e.getPoint());
                    return;
                }

                if (gameState == titleState) {
                    titleUi.mouseReleased(e.getPoint());
                    return;
                }
                if (draggedItem == null) {
                    return;
                }
                boolean isKeyPart = draggedItem.getName().equals("Key1") ||
                        draggedItem.getName().equals("Key2") ||
                        draggedItem.getName().equals("Key3");

                // Player inventory
                Rectangle inventoryBounds = playerUI.getPlayerInventoryBounds();
                // Pokud hráč pustí předmět do svého inventáře (a není to armor), přidáme ho tam
                if (inventoryBounds != null && inventoryBounds.contains(e.getPoint()) && !playerUI.isArmor(draggedItem)) {
                    player.addItem(draggedItem);
                    removeDraggedItem();
                    if (!dragDroppedLogged) {
                        GameLogger.info("Item " + draggedItem.getName() + " dropped to player inventory");
                        dragDroppedLogged = true;
                    }
                    clearDrag();
                    repaint();
                    return;
                }
                if (sourceInventory == player && inventoryBounds != null && inventoryBounds.contains(e.getPoint())) {
                    clearDrag();
                    repaint();
                    return;
                }

                // Armor slots
                Rectangle[] armorBounds = playerUI.getArmorSlotBounds();
                // Pokud je přetahovaný předmět armor a slot odpovídá, nasadí se – původní kus se vrátí zpět
                for (int i = 0; i < armorBounds.length; i++) {
                    if (armorBounds[i] != null && armorBounds[i].contains(e.getPoint()) &&
                            !isKeyPart && playerUI.isArmor(draggedItem) && playerUI.getArmorSlotIndex(draggedItem) == i) {
                        GameObject[] equippedArmor = player.getEquippedArmor();
                        if (equippedArmor[i] != null) {
                            ChestInventoryManager.ItemData prevArmor = new ChestInventoryManager.ItemData(equippedArmor[i].name, 1);
                            if (sourceInventory instanceof Object_Small_Chest) {
                                ((Object_Small_Chest) sourceInventory).getItems().add(prevArmor);
                            } else if (sourceInventory == craftingTableUI && !playerUI.isArmor(prevArmor)) {
                                player.addItem(prevArmor);
                            } else if (sourceInventory == player) {
                                player.addItem(prevArmor);
                            }
                            player.unequipArmor(i);
                        }
                        player.equipArmor(draggedItem.getItem(), i);
                        removeDraggedItem();
                        if (!dragDroppedLogged) {
                            GameLogger.info("Item " + draggedItem.getName() + " dropped to armor slot");
                            dragDroppedLogged = true;
                        }
                        clearDrag();
                        repaint();
                        return;
                    }
                }

                // Weapon slot
                Rectangle weaponBounds = playerUI.getWeaponSlotBounds();
                // Pokud je to zbraň a hráč ji pustí do zbraňového slotu, nasadí se
                if (weaponBounds != null && weaponBounds.contains(e.getPoint()) &&
                        playerUI.isWeapon(draggedItem) && !isKeyPart) {
                    GameObject equippedWeapon = player.getEquippedWeapon();
                    if (equippedWeapon != null) {
                        ChestInventoryManager.ItemData prevWeapon = new ChestInventoryManager.ItemData(equippedWeapon.name, 1);
                        if (sourceInventory == player) {
                            player.addItem(prevWeapon);
                        } else if (sourceInventory instanceof Object_Small_Chest) {
                            ((Object_Small_Chest) sourceInventory).getItems().add(prevWeapon);
                        } else if (sourceInventory == craftingTableUI) {
                            player.addItem(prevWeapon);
                        }
                        player.unequipWeapon();
                    }
                    player.equipWeapon(draggedItem.getItem());
                    removeDraggedItem();
                    if (!dragDroppedLogged) {
                        GameLogger.info("Item " + draggedItem.getName() + " dropped to weapon slot");
                        dragDroppedLogged = true;
                    }
                    clearDrag();
                    repaint();
                    return;
                }

                // Chest inventory
                // Pokud hráč pustí předmět do inventáře truhly, přidá se do jejího seznamu
                if (chestUI.isShowingInventory()) {
                    Object_Small_Chest activeChest = chestUI.getActiveChest();
                    Rectangle chestBounds = chestUI.getChestBounds();
                    if (activeChest != null && chestBounds.contains(e.getPoint()) && !isKeyPart) {
                        activeChest.getItems().add(draggedItem);
                        removeDraggedItem();
                        chestInventoryManager.updateChestData(activeChest.getId(), new ChestInventoryManager.ChestData(activeChest.isOpen(), activeChest.getItems()));
                        if (!dragDroppedLogged) {
                            GameLogger.info("Item " + draggedItem.getName() + " dropped to chest");
                            dragDroppedLogged = true;
                        }
                        clearDrag();
                        repaint();
                        return;
                    }
                }

                // Crafting table slots
                if (craftingTableUI.isShowing()) {
                    Rectangle[] craftSlotBounds = craftingTableUI.getSlotBounds();
                    for (int i = 0; i < craftSlotBounds.length; i++) {
                        // Pokud se jedná o klíčovou část a je volný slot v crafting table, nastavíme ho
                        if (craftSlotBounds[i] != null && craftSlotBounds[i].contains(e.getPoint()) && craftingTableUI.getCraftingSlot(i) == null
                                && craftingTableUI.isKeyPart(draggedItem.getName())
                                && !craftingTableUI.containsPart(draggedItem.getName())
                                && sourceInventory == player) {
                            craftingTableUI.setCraftingSlot(i, draggedItem);
                            removeDraggedItem();
                            if (!dragDroppedLogged) {
                                GameLogger.info("Item " + draggedItem.getName() + " dropped to crafting slot");
                                dragDroppedLogged = true;
                            }
                            clearDrag();
                            repaint();
                            return;
                        }
                    }
                }

                // Player consumption or door unlock
                // Pokud je předmět léčivý a puštěn na hráče, použije se
                Rectangle playerBounds = new Rectangle(player.screenX, player.screenY, tileSize, tileSize);
                if (playerBounds.contains(e.getPoint()) &&
                        (draggedItem.getName().equals("Apple") ||
                                draggedItem.getName().equals("blubbery") ||
                                draggedItem.getName().equals("potion"))) {
                    player.consumeHealingItem(draggedItem);
                    removeDraggedItem();
                    if (!dragDroppedLogged) {
                        GameLogger.info("Item " + draggedItem.getName() + " dropped to player");
                        dragDroppedLogged = true;
                    }
                    clearDrag();
                    repaint();
                    return;

                    // Key
                    // Pokud hráč přetáhne Key nebo SilverKey na dveře, odemknou se
                } else if (draggedItem.getName().equals("Key") ||
                        draggedItem.getName().equals("SilverKey")) {

                    boolean silver   = draggedItem.getName().equals("SilverKey");
                    GameObject target = null;
                    String     label  = "";

                    if (currentMap == 0) {                     // ------- карта 0 -------
                        if (silver) {                          // SilverKey → передняя
                            if (obj[0][5] instanceof Object_DoorFront d && d.requiresKey && !d.isOpen()) {
                                target = obj[0][5];  label = "Front door";
                            }
                        } else {                               // Key → боковая
                            if (obj[0][6] instanceof Object_DoorSide  d && d.requiresKey && !d.isOpen()) {
                                target = obj[0][6];  label = "Side door";
                            }
                        }
                    } else if (currentMap == 1) {              // ------- карта 1 -------
                        if (silver) {                          // SilverKey → боковая
                            if (obj[1][3] instanceof Object_DoorSide  d && d.requiresKey && !d.isOpen()) {
                                target = obj[1][3];  label = "Side door";
                            }
                        } else {                               // Key → передняя
                            if (obj[1][4] instanceof Object_DoorFront d && d.requiresKey && !d.isOpen()) {
                                target = obj[1][4];  label = "Front door";
                            }
                        }
                    }

                    if (target != null) {
                        int sx = target.worldX - player.worldX + player.screenX;
                        int sy = target.worldY - player.worldY + player.screenY;
                        Rectangle doorBounds = new Rectangle(sx, sy, tileSize, tileSize);

                        if (doorBounds.contains(e.getPoint())) {
                            if (target instanceof Object_DoorSide  sd) sd.unlock();
                            if (target instanceof Object_DoorFront fd) fd.unlock();

                            removeDraggedItem();
                            doorHintMessage.show("", 0, false);
                            doorMessage.show(label + " unlocked!", 40, true);
                            GameLogger.info("Unlocked " + label + " with " + draggedItem.getName());
                            clearDrag();
                            repaint();
                            return;
                        }
                    }
                }}
        });


/**
 * Responds to mouse movement and dragging on the game panel.
 * <p>
 * Using {@link MouseMotionAdapter}, two events are captured:
 * <ul>
 *   <li>{@code mouseMoved} – used for interactive highlighting of elements in the title screen</li>
 *   <li>{@code mouseDragged} – updates rendering during item dragging (drag-and-drop)</li>
 * </ul>
 */
        this.addMouseMotionListener(new MouseMotionAdapter() {
            // Detekuje pohyb kurzoru – slouží k hover efektům v titulní obrazovce nebo Game Over menu
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameState == titleState){
                    titleUi.mouseMoved(e.getPoint());
                }

                if (gameState == titleState || gameState == gameOverState) {
                    titleUi.mouseMoved(e.getPoint());
                    return;
                }
            }

            // Pokud uživatel přetahuje položku (draggedItem != null), překreslí panel (položka "letí" za myší)
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedItem != null) {
                    repaint();
                }
            }
        });
    }

    /**
     * Clears the dragged item and resets the drag state.
     */
    private void clearDrag() {
        draggedItem = null;
        sourceInventory = null;
        draggedItemIndex = -1;
        dragDroppedLogged = false;
        draggedFromArmor = false;
    }

    /**
     * Removes the item from inventory or chest according to the source (sourceInventory),
     * respecting quantity – if quantity > 1, only decreases the count.
     */
    private void removeDraggedItem() {
        if (sourceInventory == player) {
            if (draggedItemIndex >= 0 && draggedItemIndex < player.getInventory().size()) {
                ChestInventoryManager.ItemData sourceItem = player.getInventory().get(draggedItemIndex);
                if (sourceItem.getQuantity() > 1) {
                    sourceItem.setQuantity(sourceItem.getQuantity() - 1);
                } else {
                    player.getInventory().remove(draggedItemIndex);
                }
            } else if (draggedItemIndex >= 0 && draggedItemIndex < 4) {
                player.unequipArmor(draggedItemIndex);
            } else if (draggedItemIndex == -2) {
                player.unequipWeapon();
            }
        } else if (sourceInventory instanceof Object_Small_Chest) {
            Object_Small_Chest chest = (Object_Small_Chest) sourceInventory;
            for (int i = 0; i < chest.getItems().size(); i++) {
                ChestInventoryManager.ItemData sourceItem = chest.getItems().get(i);
                if (sourceItem.getName().equals(draggedItem.getName())) {
                    if (sourceItem.getQuantity() > 1) {
                        sourceItem.setQuantity(sourceItem.getQuantity() - 1);
                    } else {
                        chest.getItems().remove(i);
                    }
                    chestInventoryManager.updateChestData(chest.getId(), new ChestInventoryManager.ChestData(chest.isOpen(), chest.getItems()));
                    break;
                }
            }
        }
    }

    /**
     * Initializes objects and monsters in the game and sets the default state to the title screen.
     * <p>
     * This method is used for initial game setup – calls {@link AssetSetter#setObg()},
     * which places objects (e.g., chests, doors, crafting table) and then {@link AssetSetter#setMonster()},
     * which spawns monsters on the map. Then sets the game state to {@code titleState}, displaying the intro menu.
     * </p>
     */
    public void setUpObjects() {
        assetSetter.setObg();
        assetSetter.setMonster();
        gameState = titleState;
    }

    /**
     * Starts the game thread – called after window initialization.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Main game loop running in a separate thread.
     * <p>
     * The loop tries to maintain stable FPS (frames per second) and calls update and repaint.
     * </p>
     */
    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Main game update logic – processes player state, interactions,
     * message rendering, chest opening, doors, and crafting table.
     * <p>
     * This method is called every frame and responds to object proximity and key presses.
     * </p>
     */
    public void update() {

        // === 1) Pokud hra není ve stavu ovládání hráče, aktualizace se neprovede ===
        if (gameState != playerState) {
            return;
        }

        // === 2) Aktualizace stavu hráče a kolizní zpráva ===
        player.update();
        if (player.collisionOn && !collisionLogged) {
            GameLogger.info("Player collided at (" + (player.worldX / tileSize) + ", " + (player.worldY / tileSize) + ")");
            collisionLogged = true;
        } else if (!player.collisionOn) {
            collisionLogged = false;
        }

        // === 3) Aktualizace UI ukazatelů (zdraví + obrana) ===
        healthBar.update(player.life);
        defensBar.update(player.getTotalDefense());

        // === 4) Inicializace proměnných pro detekci blízkosti ===
        boolean nearDoor = false;
        Object_DoorSide closestSideDoor = null;
        Object_DoorFront closestFrontDoor = null;
        int closestDoorDistance = Integer.MAX_VALUE;

        boolean nearCraftingTable = false;
        Object_CraftingTable closestTable = null;

        int closestTableDistance = Integer.MAX_VALUE;

        boolean nearChest = false;
        Object_Small_Chest closestChest = null;
        int closestChestDistance = Integer.MAX_VALUE;

        // === 5) Procházení všech objektů na mapě a detekce interakcí ===
        for (int i = 0; i < obj[currentMap].length; i++) {
            // 5.1) Dveře – boční i čelní
            if (obj[currentMap][i] instanceof Object_DoorSide) {
                Object_DoorSide door = (Object_DoorSide) obj[currentMap][i];
                if (door != null) {
                    int dx = Math.abs(player.worldX - door.worldX);
                    int dy = Math.abs(player.worldY - door.worldY);
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    int interactDistance = tileSize * 2;

                    if (distance <= interactDistance && distance < closestDoorDistance) {
                        nearDoor = true;
                        closestSideDoor = door;
                        closestFrontDoor = null;
                        closestDoorDistance = distance;
                        if (door.isOpen()) {
                            doorHintMessage.show("Door opened!", 40, true);
                        } else if (door.requiresKey) {
                            doorHintMessage.show("This door requires a Key to open.", 40, true);
                        } else {
                            doorHintMessage.show("Press E to open the door", 40, true);
                        }
                    }
                }
            } else if (obj[currentMap][i] instanceof Object_DoorFront) {
                Object_DoorFront door = (Object_DoorFront) obj[currentMap][i];
                if (door != null) {
                    int dx = Math.abs(player.worldX - door.worldX);
                    int dy = Math.abs(player.worldY - door.worldY);
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    int interactDistance = tileSize * 2;

                    if (distance <= interactDistance && distance < closestDoorDistance) {
                        nearDoor = true;
                        closestSideDoor = null;
                        closestFrontDoor = door;
                        closestDoorDistance = distance;
                        if (door.isOpen()) {
                            doorHintMessage.show("Door opened!", 40, true);
                        } else if (door.requiresKey) {
                            doorHintMessage.show("This door requires a Key to open.", 40, true);
                        } else {
                            doorHintMessage.show("Press E to open the door", 40, true);
                        }
                    }
                }
            }
            // 5.2) Craftingový stůl
            if (obj[currentMap][i] instanceof Object_CraftingTable) {
                Object_CraftingTable table = (Object_CraftingTable) obj[currentMap][i];
                if (table != null) {
                    int dx = Math.abs(player.worldX - table.worldX);
                    int dy = Math.abs(player.worldY - table.worldY);
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    int interactDistance = tileSize * 4;

                    if (distance <= interactDistance && distance < closestTableDistance) {
                        nearCraftingTable = true;
                        closestTable = table;
                        closestTableDistance = distance;
                        craftingHintMessage.show("Press Q to open the crafting table", 40, true);
                    }
                }
            }

            // 5.3) Truhla
            if (obj[currentMap][i] instanceof Object_Small_Chest) {
                Object_Small_Chest chest = (Object_Small_Chest) obj[currentMap][i];
                if (chest != null) {
                    int dx = Math.abs(player.worldX - chest.worldX);
                    int dy = Math.abs(player.worldY - chest.worldY);
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    int interactDistance = tileSize * 2;

                    if (distance <= interactDistance && distance < closestChestDistance) {
                        nearChest = true;
                        closestChest = chest;
                        closestChestDistance = distance;

                        if (chest.isOpen()) {
                            chestMessage.show("Press E to close the chest", 40, true);
                        } else {
                            chestMessage.show("Press E to open the chest", 40, true);
                        }

                    }
                }


            }
        }

        // === 6) Ovládání interakcí pomocí kláves E a Q ===
        if (keyH.ePressed || keyH.qPressed) {
            if (nearCraftingTable && !chestUI.isShowingInventory() && keyH.qPressed && closestTableDistance <= closestDoorDistance && closestTableDistance <= closestChestDistance) {
                if (craftingTableUI.isShowing()) {
                    craftingTableUI.close();
                    GameLogger.info("Crafting table closed");
                } else {
                    craftingTableUI.open();
                    GameLogger.info("Crafting table opened");
                }
                keyH.qPressed = false;
            }else if (nearDoor && !chestUI.isShowingInventory() && !craftingTableUI.isShowing()
                    && keyH.ePressed) {
                if (closestSideDoor != null) {
                    closestSideDoor.interact();
                } else if (closestFrontDoor != null) {
                    closestFrontDoor.interact();
                }
                keyH.ePressed = false;
            } else if (nearChest && !craftingTableUI.isShowing() && keyH.ePressed) {
                chestUI.openChest(closestChest);
                GameLogger.info("Chest opened at (" + (closestChest.worldX / tileSize) + ", " + (closestChest.worldY / tileSize) + ")");
                keyH.ePressed = false;
            } else {
                keyH.ePressed = false;
                keyH.qPressed = false;
            }
        }

        // === 7) Zobrazení nápovědy k léčitelným předmětům ===
        boolean hasHealingItem = player.getInventory().stream().anyMatch(item ->
                item.getName().equals("Apple") || item.getName().equals("blubbery") || item.getName().equals("potion"));

        if (hasHealingItem) {
            healingHintMessage.show("Drag item onto the player to restore HP", 40, true);
        }

        // === 8) Aktualizace všech monster na mapě ===
        if (gameState == playerState) {
            for (int i = 0; i < monster[currentMap].length; i++) {
                if (monster[currentMap][i] != null) {
                    monster[currentMap][i].update();
                    if (monster[currentMap][i].isDead && monster[currentMap][i].fadeAlpha <= 0) {
                        monster[currentMap][i] = null;
                    }
                }
            }
        }

        // === 9) Čítač útoků
        attackCounter++;

        // === 10) Aktualizace zpráv na obrazovce ===
        doorMessage.update();
        doorHintMessage.update();
        chestMessage.update();
        healingHintMessage.update();
        craftingHintMessage.update();


        // === 11) Kontrola konce hry (smrt hráče) ===
        if (player.life <= 0) {
            GameLogger.info("PLAYER DIED!");
            gameState = gameOverState;
            return;
        }
    }

/**
 * Redraws all visual game elements on the screen.
 * <p>
 * This method is automatically called by Swing whenever the panel needs to be refreshed or redrawn.
 * Ensures display of the title screen, map, objects, monsters, player,
 * user interface, drag-and-drop icon, and messages.
 * </p>
 *
 * @param g {@link Graphics} context used for drawing
 */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        // === 1) TITULNÍ OBRAZOVKA ===
        if (gameState == titleState) {
            titleUi.draw(g2d);
            g2d.dispose();
            return;
        }

        // === 2) HERNÍ OBRAZOVKA ===
        else {
            // 2.1) Vykreslení mapy (dlaždic)
            tileH.draw(g2d);

            // 2.2) Vykreslení objektů (např. dveře, truhly, crafting stůl)
            for (int i = 0; i < obj[currentMap].length; i++) {
                if (obj[currentMap][i] != null) {
                    int screenX = obj[currentMap][i].worldX - player.worldX + player.screenX;
                    int screenY = obj[currentMap][i].worldY - player.worldY + player.screenY;
                    if (screenX + tileSize > 0 && screenX < screenWidth &&
                            screenY + tileSize > 0 && screenY < screenHeight) {
                        obj[currentMap][i].draw(g2d, this);
                    }
                }
            }

            // 2.3) Vykreslení monster (včetně animace smrti a UI)
            for (int i = 0; i < monster[currentMap].length; i++) {
                if (monster[currentMap][i] != null) {
                    int screenX = monster[currentMap][i].worldX - player.worldX + player.screenX;
                    int screenY = monster[currentMap][i].worldY - player.worldY + player.screenY;
                    if (screenX + tileSize > 0 && screenX < screenWidth &&
                            screenY + tileSize > 0 && screenY < screenHeight) {

                        monsterUi.draw(g2d, monster[currentMap][i]); // Zdravotní panel nad monstrem

                        // Vykreslení samotného monstra (pokud není mrtvé nebo fade-out neskončil)
                        if (!monster[currentMap][i].isDead || monster[currentMap][i].fadeAlpha > 0) {
                            monster[currentMap][i].draw(g2d); // Отрисовка самого монстра
                        }
                    }
                    // Vymazání monstra po dokončení fade animace
                    if (monster[currentMap][i].isDead && monster[currentMap][i].fadeAlpha <= 0) {
                        monster[currentMap][i] = null;
                    }
                }
            }
            // 2.4) Vykreslení hráče
            player.draw(g2d);

            // 2.5) Vykreslení UI prvků (zdraví, obrana, truhla, crafting, inventář)
            healthBar.draw(g2d);
            defensBar.draw(g2d);
            chestUI.draw(g2d);
            craftingTableUI.draw(g2d);
            playerUI.draw(g2d);

            // 2.6) Vykreslení přetahovaného předmětu (drag and drop)
            if (draggedItem != null) {
                Point mousePos = getMousePosition();
                if (mousePos != null) {
                    int itemSize = tileSize;
                    boolean isKeyPart = draggedItem.getName().equals("Key1") ||
                            draggedItem.getName().equals("Key2") ||
                            draggedItem.getName().equals("Key3");
                    if (isKeyPart) {
                        itemSize = (int) (tileSize * 0.6667f);
                    }
                    g2d.drawImage(draggedItem.getItem().image,
                            mousePos.x - dragOffsetX,
                            mousePos.y - dragOffsetY,
                            itemSize, itemSize, null);
                }
            }
            // 2.7) Vykreslení dynamických zpráv (např. nápovědy k truhle, dveřím, craftingu...)
            int fontSize = messageFontSize;
            int lineHeight = messageLineHeight;
            int baseX = messageX;
            int baseY = messageY;

            g2d.setFont(new Font("Arial", Font.PLAIN, fontSize));
            g2d.setColor(Color.WHITE);

            ArrayList<String> lines = new ArrayList<>();
            if (chestMessage.isVisible()) lines.add("Chest: " + chestMessage.text);
            if (doorMessage.isVisible()) lines.add("Door: " + doorMessage.text);
            if (doorHintMessage.isVisible()) lines.add("Hint: " + doorHintMessage.text);
            if (healingHintMessage.isVisible()) lines.add("Heal: " + healingHintMessage.text);
            if (craftingHintMessage.isVisible()) lines.add("Craft: " + craftingHintMessage.text);

            FontMetrics fm = g2d.getFontMetrics();
            int maxWidth = screenWidth - baseX;

            int currentY = baseY;

            for (String originalLine : lines) {
                String[] words = originalLine.split(" ");
                StringBuilder currentLine = new StringBuilder();

                for (String word : words) {
                    String testLine = currentLine + word + " ";
                    if (fm.stringWidth(testLine) > maxWidth) {
                        g2d.drawString(currentLine.toString(), baseX, currentY);
                        currentY += lineHeight;
                        currentLine = new StringBuilder(word + " ");
                    } else {
                        currentLine.append(word).append(" ");
                    }
                }

                if (!currentLine.toString().isEmpty()) {
                    g2d.drawString(currentLine.toString(), baseX, currentY);
                    currentY += lineHeight;
                }


            }

            // 2.8) Pokud je aktivní obrazovka Game Over, vykreslí se přes ostatní
            if (gameState == gameOverState) {
                titleUi.drawGameOverScreen(g2d);
                return;
            }

            g2d.dispose();
        }

    }

    /**
     * Plays background music according to the selected sound file index.
     * <p>
     * This music will play repeatedly.
     * </p>
     *
     * @param i sound index in the sound array (e.g., 0 = menu, 1 = dungeon...)
     */
    public void playMusic(int i) {
        sound.setFile(i);
        sound.playSound();
        sound.loopSound();
    }


    /**
     * Plays a short sound effect (e.g., attack, menu selection, chest opening).
     * <p>
     * The effect plays once without looping.
     * </p>
     *
     * @param i sound effect index in the array
     */
    public void playSE(int i) {
        sound.setFile(i);
        sound.playSound();
    }



    //NEW GAME
    /**
     * Initializes a new game: starts map 0, resets everything, and sets default states.
     */
    public void startNewGame() {
        currentMap = 0;
        Arrays.fill(levelSpawned, false);
        levelSpawned[currentMap] = true;
        player.reset();
        chestInventoryManager.resetChestData();

        setUpObjects();

        gameState = playerState;
        repaint();
    }

    /**
     * Saves the current game state (player, map, inventory...).
     */
    public void saveGame() {
        try {
            SaveData d = buildSaveData();
            Path parent = SAVE_PATH.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            xml.writerWithDefaultPrettyPrinter().writeValue(SAVE_PATH.toFile(), d);
            GameLogger.info("Game saved to " + SAVE_PATH.toAbsolutePath());
            if (Files.exists(SAVE_PATH)) {
                GameLogger.info("Save file confirmed at " + SAVE_PATH.toAbsolutePath());
            } else {
                GameLogger.error("Save file not found after saving!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            GameLogger.error("Failed to save game: " + ex.getMessage());
        }
    }

    /**
     * Loads the last saved game state from file.
     */
    public void loadSavedGame() {
        if (!Files.exists(SAVE_PATH)) {
            GameLogger.error("No save file found at " + SAVE_PATH.toAbsolutePath() + " – starting new game.");
            doorMessage.show("No saved game found. Starting a new game.", 120, false);
            startNewGame();
            return;
        }
        try {
            SaveData d = xml.readValue(SAVE_PATH.toFile(), SaveData.class);
            restoreFromSave(d);
            gameState = playerState;
            repaint();
            GameLogger.info("Save loaded successfully from " + SAVE_PATH.toAbsolutePath() + ":");
            GameLogger.info(" - Player HP: " + player.life);
            GameLogger.info(" - Inventory items: " + d.player.backpack.size());
            GameLogger.info(" - Armor slots: " + d.player.armor.size());
            GameLogger.info(" - Weapon: " + (d.player.weapon != null ? d.player.weapon.name : "none"));
            GameLogger.info(" - Grade: " + (d.player.grade != null ? d.player.grade.name : "none"));
            GameLogger.info(" - Chests: " + d.chests.size());
            doorMessage.show("Game loaded successfully!", 120, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            GameLogger.error("Failed to load save from " + SAVE_PATH.toAbsolutePath() + " – starting new game.");
            doorMessage.show("Failed to load saved game. Starting a new game.", 120, false);
            startNewGame();
        }
    }
}