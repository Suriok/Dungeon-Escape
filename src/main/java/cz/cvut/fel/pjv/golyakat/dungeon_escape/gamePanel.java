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

public class gamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    //WORLD SETTINGS
    public final int maxWorldCol = 60;
    public final int maxWorldRow = 60;
    public final int maxMap = 2; // number of all levels
    public int currentMap = 0; // current map index

    // FPS
    int FPS = 60;

    // SYSTEM
    public TileManger tileH = new TileManger(this);
    public KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    public Collision collisionChecker = new Collision(this);
    public AssetSetter assetSetter;
    private final TitleScreenUI titleUi;
    Sound sound = new Sound();





    // ENTITY AND OBJECT
    public Player player = new Player(this, keyH);
    public GameObject[][] obj = new GameObject[maxMap][11];
    public HealthBar healthBar;
    public DefensBar defensBar;
    public Entity[][] monster = new Entity[maxMap][20];
    public MonsterUI monsterUi;
    public boolean[] levelSpawned = new boolean[maxMap];

    // GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playerState = 1;
    public final int gameOverState = 2;

    public ChestUI chestUI;
    public CraftingTableUI craftingTableUI;
    public PlayerUI playerUI;
    public ChestInventoryManager chestInventoryManager;

    private int attackCounter = 0;
    private static final int ATTACK_COOLDOWN = 30;

    // Drag-and-drop variables
    private ChestInventoryManager.ItemData draggedItem = null;
    private Object sourceInventory = null; // Player, Chest, CraftingTableUI
    private int draggedItemIndex = -1;
    private int dragOffsetX, dragOffsetY;
    private boolean collisionLogged = false;
    private boolean dragDroppedLogged = false;
    private boolean draggedFromArmor = false;

    // MESSAGES
    public HintMessage doorMessage = new HintMessage();
    public HintMessage doorHintMessage = new HintMessage();
    public HintMessage chestMessage = new HintMessage();
    public HintMessage healingHintMessage = new HintMessage();
    public HintMessage craftingHintMessage = new HintMessage();

    // UI MESSAGE SETTINGS
    private final int messageFontSize = 20;
    private final int messageLineHeight = 30;
    private final int messageX = screenWidth - tileSize * 7; // вправо
    private final int messageY = 25;

    // HINT MESSAGE
    public class HintMessage {
        public String text = "";
        public int counter = 0;
        public boolean near = false;

        public void show(String message, int duration, boolean isNear) {
            this.text = message;
            this.counter = duration;
            this.near = isNear;
        }

        public void update() {
            if (counter > 0) counter--;
            if (counter <= 0) text = "";
        }

        public boolean isVisible() {
            return !text.isEmpty();
        }
    }

    // ---------- SAVE / LOAD ----------
    private static final Path SAVE_PATH = Path.of("saved_game.xml");
    private final XmlMapper xml = new XmlMapper();

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
                System.err.println("Unknown item: " + name);
                return null;
        }
    }

    private SaveData buildSaveData() {
        SaveData data = new SaveData();

        data.player.worldX = player.worldX;
        data.player.worldY = player.worldY;
        data.player.life = player.life;
        System.out.println("Saving player position: (" + player.worldX + ", " + player.worldY + "), HP: " + player.life);

        List<ChestInventoryManager.ItemData> inventory = player.getInventory();
        if (inventory != null && !inventory.isEmpty()) {
            inventory.forEach(it -> {
                if (it != null && it.getName() != null) {
                    data.player.backpack.add(new SaveData.ItemData(it.getName(), it.getQuantity()));
                    System.out.println("Saving inventory item: " + it.getName() + " x" + it.getQuantity());
                }
            });
        } else {
            System.err.println("Player inventory is null or empty");
        }

        data.currentMap   = currentMap;
        data.levelSpawned = levelSpawned.clone();

        GameObject[] equippedArmor = player.getEquippedArmor();
        if (equippedArmor != null) {
            for (int i = 0; i < equippedArmor.length; i++) {
                if (equippedArmor[i] != null && equippedArmor[i].name != null) {
                    data.player.armor.add(new SaveData.ItemData(equippedArmor[i].name, 1));
                    System.out.println("Saving armor in slot " + i + ": " + equippedArmor[i].name);
                }
            }
        } else {
            System.err.println("Equipped armor array is null");
        }
        if (data.player.armor.isEmpty()) {
            System.out.println("No equipped armor to save");
        }

        GameObject weapon = player.getEquippedWeapon();
        if (weapon != null && weapon.name != null) {
            data.player.weapon = new SaveData.ItemData(weapon.name, 1);
            System.out.println("Saving weapon: " + weapon.name);
        } else {
            System.out.println("No equipped weapon to save");
        }

        GameObject grade = player.getEquippedGrade();
        if (grade != null && grade.name != null) {
            data.player.grade = new SaveData.ItemData(grade.name, 1);
            System.out.println("Saving grade: " + grade.name);
        } else {
            System.out.println("No equipped grade to save");
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
                System.out.println("Saving monster " + md.type + " at (" + md.worldX + ", " + md.worldY + ")");
            }
        }

        chestInventoryManager.forEachChest((id, list) -> {
            SaveData.ChestData cd = new SaveData.ChestData();
            cd.id = id;
            if (list != null) {
                list.forEach(it -> {
                    if (it != null && it.getName() != null) {
                        cd.items.add(new SaveData.ItemData(it.getName(), it.getQuantity()));
                        System.out.println("Saving chest " + cd.id + " item: " + it.getName() + " x" + it.getQuantity());
                    }
                });
            }
            data.chests.add(cd);
        });

        return data;
    }

    private void restoreFromSave(SaveData d) {
        currentMap = d.currentMap;                           // по умолчанию 0, если в XML нет атрибута
        if (d.levelSpawned != null && d.levelSpawned.length == levelSpawned.length) {
            levelSpawned = d.levelSpawned.clone();
        } else {
            Arrays.fill(levelSpawned, false);
        }

        /* 2.  Подготовка окружения под нужную карту */
        assetSetter.setObg();                 // заново раскладываем двери/сундуки/и т.д.
        tileH.findWalkableRegions();          // пересчёт проходных зон

        if (!levelSpawned[currentMap]) {      // если в этой карте монстры ещё не спавнились
            assetSetter.setMonster();
            levelSpawned[currentMap] = true;
        }

        /* 3.  Сбрасываем сущности игрока НА ОСНОВЕ новой currentMap */
        player.reset();                       // ставит стартовые координаты для карты

        player.reset();
        player.worldX = d.player.worldX;
        player.worldY = d.player.worldY;
        player.life = d.player.life;
        System.out.println("Restored player position: (" + player.worldX + ", " + player.worldY + "), HP: " + player.life);

        // Восстановление инвентаря
        if (d.player.backpack != null && !d.player.backpack.isEmpty()) {
            d.player.backpack.forEach(it -> {
                if (it != null && it.name != null) {
                    player.addItem(new ChestInventoryManager.ItemData(it.name, it.qty));
                    System.out.println("Restored inventory item: " + it.name + " x" + it.qty);
                }
            });
        } else {
            System.out.println("No backpack data to restore");
        }

        if (d.player.armor != null && !d.player.armor.isEmpty()) {
            for (int i = 0; i < d.player.armor.size() && i < 4; i++) {
                SaveData.ItemData armorData = d.player.armor.get(i);
                if (armorData != null && armorData.name != null) {
                    GameObject armorObj = makeItem(armorData.name);
                    if (armorObj != null) {
                        player.equipArmor(armorObj, i);
                        System.out.println("Restored armor in slot " + i + ": " + armorData.name);
                    } else {
                        System.err.println("Failed to restore armor: " + armorData.name);
                    }
                }
            }
        } else {
            System.out.println("No armor data to restore");
        }

        if (d.player.weapon != null && d.player.weapon.name != null) {
            GameObject weaponObj = makeItem(d.player.weapon.name);
            if (weaponObj != null) {
                player.equipWeapon(weaponObj);
                System.out.println("Restored weapon: " + d.player.weapon.name);
            } else {
                System.err.println("Failed to restore weapon: " + d.player.weapon.name);
            }
        } else {
            System.out.println("No weapon data to restore");
        }

        if (d.player.grade != null && d.player.grade.name != null) {
            GameObject gradeObj = makeItem(d.player.grade.name);
            if (gradeObj != null) {
                player.equipGrade(gradeObj);
                System.out.println("Restored grade: " + d.player.grade.name);
            } else {
                System.err.println("Failed to restore grade: " + d.player.grade.name);
            }
        } else {
            System.out.println("No grade data to restore");
        }

        for (Entity[] row : monster) {
            Arrays.fill(row, null);
        }
        if (d.monsters != null && !d.monsters.isEmpty()) {
            for (int i = 0; i < d.monsters.size() && i < monster.length; i++) {
                SaveData.MonsterData md = d.monsters.get(i);
                if (md != null && md.type != null) {
                    switch (md.type) {
                        case "Boss_Goblin":
                            monster[0][i] = new Boss_Goblin(this);
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
                            System.err.println("Unknown monster type: " + md.type);
                            continue;
                    }
                    monster[currentMap][i].worldX = md.worldX;
                    monster[currentMap][i].worldY = md.worldY;
                    monster[currentMap][i].life = md.life;
                    monster[currentMap][i].isDead = md.dead;
                    System.out.println("Restored monster " + md.type + " at (" + md.worldX + ", " + md.worldY + ")");
                }
            }
        } else {
            System.out.println("No monster data to restore");
        }

        chestInventoryManager.resetChestData();
        if (d.chests != null && !d.chests.isEmpty()) {
            d.chests.forEach(cd -> {
                if (cd != null && cd.items != null) {
                    chestInventoryManager.overrideChest(cd.id, toItemList(cd.items));
                    System.out.println("Restored chest ID " + cd.id + " with " + cd.items.size() + " items");
                }
            });
        } else {
            System.out.println("No chest data to restore");
        }
    }

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
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.requestFocusInWindow();

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

        // Centralized MouseListener for drag-and-drop
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow(); // для захвата фокуса клавиатуры

                // если титульный экран — отправляем событие туда
                if (gameState == titleState) {
                    titleUi.mousePressed(e.getPoint());
                    return;
                }

                if (gameState == titleState || gameState == gameOverState) {
                    titleUi.mousePressed(e.getPoint());
                    return;
                }

                // Правая кнопка мыши — атака
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

            @Override
            public void mouseReleased(MouseEvent e) {

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
                if (inventoryBounds != null && inventoryBounds.contains(e.getPoint()) && !playerUI.isArmor(draggedItem)) {
                    player.addItem(draggedItem);
                    removeDraggedItem();
                    if (!dragDroppedLogged) {
                        System.out.println("Item " + draggedItem.getName() + " dropped to player inventory");
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
                            System.out.println("Item " + draggedItem.getName() + " dropped to armor slot");
                            dragDroppedLogged = true;
                        }
                        clearDrag();
                        repaint();
                        return;
                    }
                }

                // Weapon slot
                Rectangle weaponBounds = playerUI.getWeaponSlotBounds();
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
                        System.out.println("Item " + draggedItem.getName() + " dropped to weapon slot");
                        dragDroppedLogged = true;
                    }
                    clearDrag();
                    repaint();
                    return;
                }

                // Chest inventory
                if (chestUI.isShowingInventory()) {
                    Object_Small_Chest activeChest = chestUI.getActiveChest();
                    Rectangle chestBounds = chestUI.getChestBounds();
                    if (activeChest != null && chestBounds.contains(e.getPoint()) && !isKeyPart) {
                        activeChest.getItems().add(draggedItem);
                        removeDraggedItem();
                        chestInventoryManager.updateChestData(activeChest.getId(), new ChestInventoryManager.ChestData(activeChest.isOpen(), activeChest.getItems()));
                        if (!dragDroppedLogged) {
                            System.out.println("Item " + draggedItem.getName() + " dropped to chest");
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
                        if (craftSlotBounds[i] != null && craftSlotBounds[i].contains(e.getPoint()) && craftingTableUI.getCraftingSlot(i) == null
                                && craftingTableUI.isKeyPart(draggedItem.getName())
                                && !craftingTableUI.containsPart(draggedItem.getName())
                                && sourceInventory == player) {
                            craftingTableUI.setCraftingSlot(i, draggedItem);
                            removeDraggedItem();
                            if (!dragDroppedLogged) {
                                System.out.println("Item " + draggedItem.getName() + " dropped to crafting slot");
                                dragDroppedLogged = true;
                            }
                            clearDrag();
                            repaint();
                            return;
                        }
                    }
                }

                // Player consumption or door unlock
                Rectangle playerBounds = new Rectangle(player.screenX, player.screenY, tileSize, tileSize);
                if (playerBounds.contains(e.getPoint()) &&
                        (draggedItem.getName().equals("Apple") ||
                                draggedItem.getName().equals("blubbery") ||
                                draggedItem.getName().equals("potion"))) {
                    player.consumeHealingItem(draggedItem);
                    removeDraggedItem();
                    if (!dragDroppedLogged) {
                        System.out.println("Item " + draggedItem.getName() + " dropped to player");
                        dragDroppedLogged = true;
                    }
                    clearDrag();
                    repaint();
                    return;
                    // Key
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
                            System.out.println("Unlocked " + label + " with " + draggedItem.getName());
                            clearDrag();
                            repaint();
                            return;
                        }
                    }
                }}
        });


        this.addMouseMotionListener(new MouseMotionAdapter() {
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

            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedItem != null) {
                    repaint();
                }
            }
        });
    }

    private void clearDrag() {
        draggedItem = null;
        sourceInventory = null;
        draggedItemIndex = -1;
        dragDroppedLogged = false;
        draggedFromArmor = false;
    }

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

    public void setUpObjects() {
        assetSetter.setObg();
        assetSetter.setMonster();
        gameState = titleState;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

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

    public void update() {

        if (gameState != playerState) {
            return;
        }

        player.update();
        if (player.collisionOn && !collisionLogged) {
            System.out.println("Player collided at (" + (player.worldX / tileSize) + ", " + (player.worldY / tileSize) + ")");
            collisionLogged = true;
        } else if (!player.collisionOn) {
            collisionLogged = false;
        }

        healthBar.update(player.life);
        defensBar.update(player.getTotalDefense());

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

        for (int i = 0; i < obj[currentMap].length; i++) {
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

        if (keyH.ePressed || keyH.qPressed) {
            if (nearCraftingTable && !chestUI.isShowingInventory() && keyH.qPressed && closestTableDistance <= closestDoorDistance && closestTableDistance <= closestChestDistance) {
                if (craftingTableUI.isShowing()) {
                    craftingTableUI.close();
                    System.out.println("Crafting table closed");
                } else {
                    craftingTableUI.open();
                    System.out.println("Crafting table opened");
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
                System.out.println("Chest opened at (" + (closestChest.worldX / tileSize) + ", " + (closestChest.worldY / tileSize) + ")");
                keyH.ePressed = false;
            } else {
                keyH.ePressed = false;
                keyH.qPressed = false;
            }
        }

        boolean hasHealingItem = player.getInventory().stream().anyMatch(item ->
                item.getName().equals("Apple") || item.getName().equals("blubbery") || item.getName().equals("potion"));

        if (hasHealingItem) {
            healingHintMessage.show("Drag item onto the player to restore HP", 40, true);
        }

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

        attackCounter++;

        doorMessage.update();
        doorHintMessage.update();
        chestMessage.update();
        healingHintMessage.update();
        craftingHintMessage.update();


        if (player.life <= 0) {
            System.out.println("PLAYER DIED!");
            gameState = gameOverState;
            return;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;


        // TITLE SCREEN
        if (gameState == titleState) {
            titleUi.draw(g2d);
            g2d.dispose();
            return;
        }

        // Game Screen
        else {
            tileH.draw(g2d);
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
            // Monsters
            for (int i = 0; i < monster[currentMap].length; i++) {
                if (monster[currentMap][i] != null) {
                    int screenX = monster[currentMap][i].worldX - player.worldX + player.screenX;
                    int screenY = monster[currentMap][i].worldY - player.worldY + player.screenY;
                    if (screenX + tileSize > 0 && screenX < screenWidth &&
                            screenY + tileSize > 0 && screenY < screenHeight) {
                        monsterUi.draw(g2d, monster[currentMap][i]); // Отрисовка UI монстра (например, полоска здоровья)
                        if (!monster[currentMap][i].isDead || monster[currentMap][i].fadeAlpha > 0) {
                            monster[currentMap][i].draw(g2d); // Отрисовка самого монстра
                        }
                    }
                    // Проверка смерти и обнуление
                    if (monster[currentMap][i].isDead && monster[currentMap][i].fadeAlpha <= 0) {
                        monster[currentMap][i] = null;
                    }
                }
            }

            player.draw(g2d);
            healthBar.draw(g2d);
            defensBar.draw(g2d);
            chestUI.draw(g2d);
            craftingTableUI.draw(g2d);
            playerUI.draw(g2d);

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

            // Game Over
            if (gameState == gameOverState) {
                titleUi.drawGameOverScreen(g2d);
                return;
            }

            g2d.dispose();
        }

    }

    public void playMusic(int i) {
        sound.setFile(i);
        sound.playSound();
        sound.loopSound();
    }

    public void stopMusic() {
        sound.StopSound();
    }

    public void playSE(int i) {
        sound.setFile(i);
        sound.playSound();
    }



    //NEW GAME

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

    public void saveGame() {
        try {
            SaveData d = buildSaveData();
            Path parent = SAVE_PATH.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            xml.writerWithDefaultPrettyPrinter().writeValue(SAVE_PATH.toFile(), d);
            System.out.println("Game saved to " + SAVE_PATH.toAbsolutePath());
            if (Files.exists(SAVE_PATH)) {
                System.out.println("Save file confirmed at " + SAVE_PATH.toAbsolutePath());
            } else {
                System.err.println("Save file not found after saving!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Failed to save game: " + ex.getMessage());
        }
    }

    public void loadSavedGame() {
        if (!Files.exists(SAVE_PATH)) {
            System.err.println("No save file found at " + SAVE_PATH.toAbsolutePath() + " – starting new game.");
            doorMessage.show("No saved game found. Starting a new game.", 120, false);
            startNewGame();
            return;
        }
        try {
            SaveData d = xml.readValue(SAVE_PATH.toFile(), SaveData.class);
            restoreFromSave(d);
            gameState = playerState;
            repaint();
            System.out.println("Save loaded successfully from " + SAVE_PATH.toAbsolutePath() + ":");
            System.out.println(" - Player HP: " + player.life);
            System.out.println(" - Inventory items: " + d.player.backpack.size());
            System.out.println(" - Armor slots: " + d.player.armor.size());
            System.out.println(" - Weapon: " + (d.player.weapon != null ? d.player.weapon.name : "none"));
            System.out.println(" - Grade: " + (d.player.grade != null ? d.player.grade.name : "none"));
            System.out.println(" - Chests: " + d.chests.size());
            doorMessage.show("Game loaded successfully!", 120, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Failed to load save from " + SAVE_PATH.toAbsolutePath() + " – starting new game.");
            doorMessage.show("Failed to load saved game. Starting a new game.", 120, false);
            startNewGame();
        }
    }
}