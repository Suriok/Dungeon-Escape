package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Player;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.ChestUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.CraftingTableUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.PlayerUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.MonsterUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.bars.DefensBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.bars.HealthBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_CraftingTable;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.tile.TileManger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class gamePanel extends JPanel implements Runnable {
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int maxWorldCol = 60;
    public final int maxWorldRow = 60;

    int FPS = 60;

    TileManger tileH = new TileManger(this);
    public KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    public Collision collisionChecker = new Collision(this);
    public AssetSetter assetSetter = new AssetSetter(this);

    public Player player = new Player(this, keyH);
    public GameObject obj[] = new GameObject[11];
    public HealthBar healthBar;
    public DefensBar defensBar;
    public Entity monster[] = new Entity[20];
    public MonsterUI monsterUi;

    public int gameState;
    public final int playerState = 1;

    public String doorMessage = "";
    public int doorMessageCounter = 0;
    public String doorHintMessage = "";
    public int doorHintMessageCounter = 0;
    public String chestMessage = "";
    public int chestMessageCounter = 0;
    public String healingHintMessage = "";
    public int healingHintMessageCounter = 0;
    public String craftingHintMessage = "";
    public int craftingHintMessageCounter = 0;

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
    private boolean objectsLogged = false;
    private boolean dragStartedLogged = false;
    private boolean dragDroppedLogged = false;

    public gamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.requestFocusInWindow();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        chestInventoryManager = new ChestInventoryManager();
        healthBar = new HealthBar(this);
        defensBar = new DefensBar(this);
        chestUI = new ChestUI(this);
        craftingTableUI = new CraftingTableUI(this);
        playerUI = new PlayerUI(this);
        monsterUi = new MonsterUI(this);
        gameState = playerState;

        // Centralized MouseListener for drag-and-drop
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
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
                        draggedItemIndex = player.getInventory().indexOf(
                                player.getInventory().stream()
                                        .filter(item -> item.getName().equals(draggedItem.getName()))
                                        .findFirst()
                                        .orElse(null)
                        );
                        dragOffsetX = e.getX() - (offsetX + col * cellWidth + (cellWidth - itemSize) / 2);
                        dragOffsetY = e.getY() - (offsetY + row * cellHeight + (cellHeight - itemSize) / 2);
                        if (!dragStartedLogged) {
                            System.out.println("Drag started: Item " + draggedItem.getName() + " from player inventory index " + draggedItemIndex);
                            dragStartedLogged = true;
                        }
                    }
                    repaint();
                    return;
                }

                // Armor slots
                Rectangle[] armorBounds = playerUI.getArmorSlotBounds();
                for (int i = 0; i < armorBounds.length; i++) {
                    if (armorBounds[i] != null  && armorBounds[i].contains(e.getPoint())) {
                        GameObject armor = player.getEquippedArmor()[i];
                        if (armor != null) {
                            draggedItem = new ChestInventoryManager.ItemData(armor.name, 1);
                            sourceInventory = player;
                            draggedItemIndex = i; // Armor slot index
                            dragOffsetX = e.getX() - armorBounds[i].x;
                            dragOffsetY = e.getY() - armorBounds[i].y;
                            if (!dragStartedLogged) {
                                System.out.println("Drag started: Item " + draggedItem.getName() + " from armor slot " + i);
                                dragStartedLogged = true;
                            }
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
                        if (!dragStartedLogged) {
                            System.out.println("Drag started: Item " + draggedItem.getName() + " from weapon slot");
                            dragStartedLogged = true;
                        }
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
                                    if (!dragStartedLogged) {
                                        System.out.println("Drag started: Item " + draggedItem.getName() + " from chest index " + i);
                                        dragStartedLogged = true;
                                    }
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
                                if (!dragStartedLogged) {
                                    System.out.println("Drag started: Item " + draggedItem.getName() + " from crafting slot " + i);
                                    dragStartedLogged = true;
                                }
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
                if (draggedItem == null) {
                    System.out.println("No item dragged, skipping mouseReleased");
                    return;
                }

                boolean isKeyPart = draggedItem.getName().equals("Key1") ||
                        draggedItem.getName().equals("Key2") ||
                        draggedItem.getName().equals("Key3");

                // Player inventory
                Rectangle inventoryBounds = playerUI.getPlayerInventoryBounds();
                if (inventoryBounds != null && inventoryBounds.contains(e.getPoint())  && !playerUI.isArmor(draggedItem)) {
                    System.out.println("Dropping " + draggedItem.getName() + " to player inventory");
                    player.addItem(draggedItem);
                    removeDraggedItem();
                    if (!dragDroppedLogged) {
                        System.out.println("Dropped: Item " + draggedItem.getName() + " to player inventory");
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
                    if (armorBounds[i].contains(e.getPoint())
                            && !isKeyPart
                            && playerUI.isArmor(draggedItem)
                            && playerUI.getArmorSlotIndex(draggedItem) == i) {
                        System.out.println("Dropping " + draggedItem.getName() + " to armor slot " + i);
                        GameObject[] equippedArmor = player.getEquippedArmor();
                        if (equippedArmor[i] != null) {
                            ChestInventoryManager.ItemData prevArmor = new ChestInventoryManager.ItemData(equippedArmor[i].name, 1);
                            if (sourceInventory instanceof Object_Small_Chest) {
                                ((Object_Small_Chest) sourceInventory).getItems().add(prevArmor);
                                System.out.println("Swapped armor: Added " + prevArmor.getName() + " back to chest");
                            } else if (sourceInventory == craftingTableUI && !playerUI.isArmor(prevArmor)) {
                                player.addItem(prevArmor);
                                System.out.println("Swapped: Added " + prevArmor.getName() + " to player inventory");
                            } else {
                                System.out.println("Cannot swap armor " + prevArmor.getName() + " to player inventory");
                            }
                            player.unequipArmor(i); // Explicitly unequip to prevent duplication
                        }
                        player.equipArmor(draggedItem.getItem(), i);
                        removeDraggedItem();
                        if (!dragDroppedLogged) {
                            System.out.println("Dropped: Item " + draggedItem.getName() + " to armor slot " + i);
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
                    System.out.println("Dropping " + draggedItem.getName() + " to weapon slot");
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
                        System.out.println("Swapped weapon: Added " + prevWeapon.getName() + " back to " +
                                (sourceInventory instanceof Object_Small_Chest ? "chest" : "player"));
                        player.unequipWeapon();
                    }
                    player.equipWeapon(draggedItem.getItem());
                    removeDraggedItem();
                    if (!dragDroppedLogged) {
                        System.out.println("Dropped: Item " + draggedItem.getName() + " to weapon slot");
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
                        System.out.println("Dropping " + draggedItem.getName() + " to chest");
                        activeChest.getItems().add(draggedItem);
                        removeDraggedItem();
                        chestInventoryManager.updateChestData(activeChest.getId(), new ChestInventoryManager.ChestData(activeChest.isOpen(), activeChest.getItems()));
                        if (!dragDroppedLogged) {
                            System.out.println("Dropped: Item " + draggedItem.getName() + " to chest");
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
                            System.out.println("Dropping " + draggedItem.getName() + " to crafting slot " + i);
                            craftingTableUI.setCraftingSlot(i, draggedItem);
                            removeDraggedItem();
                            if (!dragDroppedLogged) {
                                System.out.println("Dropped: Item " + draggedItem.getName() + " to crafting slot " + i);
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
                    System.out.println("Consuming " + draggedItem.getName() + " by player");
                    player.consumeHealingItem(draggedItem);
                    removeDraggedItem();
                    if (!dragDroppedLogged) {
                        System.out.println("Dropped: Item " + draggedItem.getName() + " consumed by player");
                        dragDroppedLogged = true;
                    }
                    clearDrag();
                    repaint();
                    return;
                } else if (draggedItem.getName().equals("SilverKey") && obj[6] != null && obj[6] instanceof Object_DoorSide) {
                    Object_DoorSide door = (Object_DoorSide) obj[6];
                    if (door.requiresKey && !door.isOpen()) {
                        int doorScreenX = obj[6].worldX - player.worldX + player.screenX;
                        int doorScreenY = obj[6].worldY - player.worldY + player.screenY;
                        Rectangle doorBounds = new Rectangle(doorScreenX, doorScreenY, tileSize, tileSize);
                        if (doorBounds.contains(e.getPoint())) {
                            System.out.println("Using " + draggedItem.getName() + " to unlock door");
                            door.unlock();
                            removeDraggedItem();
                            doorHintMessage = "";
                            doorHintMessageCounter = 0;
                            doorMessage = "Door unlocked!";
                            doorMessageCounter = 120;
                            if (!dragDroppedLogged) {
                                System.out.println("Dropped: Item " + draggedItem.getName() + " used to unlock door");
                                dragDroppedLogged = true;
                            }
                            clearDrag();
                            repaint();
                            return;
                        }
                    }
                }

                // Return to source if invalid drop
                System.out.println("Invalid drop for " + draggedItem.getName() + ", returning to source");
                if (sourceInventory == player) {
                    if (draggedItemIndex == -2) {
                        player.equipWeapon(draggedItem.getItem());
                        System.out.println("Returned " + draggedItem.getName() + " to player weapon slot");
                    } else if (draggedItemIndex >= 0 && draggedItemIndex < 4) {
                        player.equipArmor(draggedItem.getItem(), draggedItemIndex);
                        System.out.println("Returned " + draggedItem.getName() + " to player armor slot " + draggedItemIndex);
                    } else {
                        if (!playerUI.isArmor(draggedItem)) {
                            player.addItem(draggedItem);
                            System.out.println("Returned " + draggedItem.getName() + " to player inventory");
                        } else {
                            System.out.println("Cannot return armor " + draggedItem.getName() + " to player inventory");
                        }
                    }
                } else if (sourceInventory instanceof Object_Small_Chest) {
                    ((Object_Small_Chest) sourceInventory).getItems().add(draggedItem);
                    System.out.println("Returned " + draggedItem.getName() + " to chest inventory");
                    chestInventoryManager.updateChestData(((Object_Small_Chest) sourceInventory).getId(),
                            new ChestInventoryManager.ChestData(((Object_Small_Chest) sourceInventory).isOpen(), ((Object_Small_Chest) sourceInventory).getItems()));
                } else if (sourceInventory == craftingTableUI) {
                    if (!playerUI.isArmor(draggedItem)) {
                        player.addItem(draggedItem);
                        System.out.println("Returned " + draggedItem.getName() + " to player inventory from crafting table");
                    } else {
                        System.out.println("Cannot return armor " + draggedItem.getName() + " to player inventory from crafting table");
                    }
                }
                if (!dragDroppedLogged) {
                    System.out.println("Dropped: Item " + draggedItem.getName() + " returned to source");
                    dragDroppedLogged = true;
                }
                clearDrag();
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedItem != null) {
                    repaint();
                }
            }
        });

        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    chestInventoryManager.resetChestData();
                }
            });
        }
    }

    private void clearDrag() {
        draggedItem = null;
        sourceInventory = null;
        draggedItemIndex = -1;
        dragStartedLogged = false;
        dragDroppedLogged = false;
        System.out.println("Drag cleared");
    }

    private void removeDraggedItem() {
        if (sourceInventory == player) {
            if (draggedItemIndex >= 0 && draggedItemIndex < player.getInventory().size()) {
                ChestInventoryManager.ItemData sourceItem = player.getInventory().get(draggedItemIndex);
                if (sourceItem.getQuantity() > 1) {
                    sourceItem.setQuantity(sourceItem.getQuantity() - 1);
                    System.out.println("Reduced quantity of " + sourceItem.getName() + " in player inventory to " + sourceItem.getQuantity());
                } else {
                    player.getInventory().remove(draggedItemIndex);
                    System.out.println("Removed " + sourceItem.getName() + " from player inventory at index " + draggedItemIndex);
                }
            } else if (draggedItemIndex >= 0 && draggedItemIndex < 4) {
                player.unequipArmor(draggedItemIndex);
                System.out.println("Unequipped armor from slot " + draggedItemIndex);
            } else if (draggedItemIndex == -2) {
                player.unequipWeapon();
                System.out.println("Unequipped weapon");
            }
        } else if (sourceInventory instanceof Object_Small_Chest) {
            Object_Small_Chest chest = (Object_Small_Chest) sourceInventory;
            boolean removed = false;
            for (int i = 0; i < chest.getItems().size(); i++) {
                ChestInventoryManager.ItemData sourceItem = chest.getItems().get(i);
                if (sourceItem.getName().equals(draggedItem.getName())) {
                    if (sourceItem.getQuantity() > 1) {
                        sourceItem.setQuantity(sourceItem.getQuantity() - 1);
                        System.out.println("Reduced quantity of " + sourceItem.getName() + " in chest to " + sourceItem.getQuantity());
                    } else {
                        chest.getItems().remove(i);
                        System.out.println("Removed " + sourceItem.getName() + " from chest at index " + i);
                    }
                    removed = true;
                    chestInventoryManager.updateChestData(chest.getId(), new ChestInventoryManager.ChestData(chest.isOpen(), chest.getItems()));
                    break;
                }
            }
            if (!removed) {
                System.out.println("Warning: Could not remove item " + draggedItem.getName() + " from chest");
            }
        }
        // For craftingTableUI, the item is already removed via setCraftingSlot(null)
    }

    public void setUpObjects() {
        Map<String, Integer> chest0Items = new HashMap<>();
        chest0Items.put("leather_pants", 1);
        chest0Items.put("leather_helmet", 1);
        chest0Items.put("iron_sword", 1);
        obj[0] = new Object_Small_Chest(this, 0, chest0Items);
        obj[0].worldX = 15 * tileSize;
        obj[0].worldY = 21 * tileSize;

        Map<String, Integer> chest7Items = new HashMap<>();
        chest7Items.put("Key1", 1);
        obj[7] = new Object_Small_Chest(this, 7, chest7Items);
        obj[7].worldX = 40 * tileSize;
        obj[7].worldY = 30 * tileSize;

        Map<String, Integer> chest8Items = new HashMap<>();
        chest8Items.put("Key2", 1);
        obj[8] = new Object_Small_Chest(this, 8, chest8Items);
        obj[8].worldX = 25 * tileSize;
        obj[8].worldY = 13 * tileSize;

        Map<String, Integer> chest9Items = new HashMap<>();
        chest9Items.put("Key3", 1);
        obj[9] = new Object_Small_Chest(this, 9, chest9Items);
        obj[9].worldX = 50 * tileSize;
        obj[9].worldY = 21 * tileSize;

        obj[10] = new Object_CraftingTable();
        obj[10].worldX = 16 * tileSize;
        obj[10].worldY = 22 * tileSize;

        player.worldX = 15 * tileSize;
        player.worldY = 22 * tileSize;

        if (!objectsLogged) {
            System.out.println("Objects initialized:");
            System.out.println("Chest 0 at (" + (obj[0].worldX / tileSize) + ", " + (obj[0].worldY / tileSize) + "): " + chest0Items);
            System.out.println("Chest 7 at (" + (obj[7].worldX / tileSize) + ", " + (obj[7].worldY / tileSize) + "): " + chest7Items);
            System.out.println("Chest 8 at (" + (obj[8].worldX / tileSize) + ", " + (obj[8].worldY / tileSize) + "): " + chest8Items);
            System.out.println("Chest 9 at (" + (obj[9].worldX / tileSize) + ", " + (obj[9].worldY / tileSize) + "): " + chest9Items);
            System.out.println("Crafting Table at (" + (obj[10].worldX / tileSize) + ", " + (obj[10].worldY / tileSize) + ")");
            objectsLogged = true;
        }

        assetSetter.setObg();
        assetSetter.setMonster();
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
        Object_DoorSide closestDoor = null;
        int closestDoorDistance = Integer.MAX_VALUE;

        boolean nearCraftingTable = false;
        Object_CraftingTable closestTable = null;
        int closestTableDistance = Integer.MAX_VALUE;

        boolean nearChest = false;
        Object_Small_Chest closestChest = null;
        int closestChestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < obj.length; i++) {
            if (obj[i] instanceof Object_DoorSide) {
                Object_DoorSide door = (Object_DoorSide) obj[i];
                if (door != null) {
                    int dx = Math.abs(player.worldX - door.worldX);
                    int dy = Math.abs(player.worldY - door.worldY);
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    int interactDistance = tileSize * 2;

                    if (distance <= interactDistance && distance < closestDoorDistance) {
                        nearDoor = true;
                        closestDoor = door;
                        closestDoorDistance = distance;
                        if (door.isOpen()) {
                            doorHintMessage = "Door opened!";
                            doorHintMessageCounter = 80;
                        } else if (door.requiresKey) {
                            doorHintMessage = "This door requires a key to open.";
                            doorHintMessageCounter = 80;
                        } else {
                            doorHintMessage = "Press E to open the door";
                            doorHintMessageCounter = 80;
                        }
                    }
                }
            }
            if (obj[i] instanceof Object_CraftingTable) {
                Object_CraftingTable table = (Object_CraftingTable) obj[i];
                if (table != null) {
                    int dx = Math.abs(player.worldX - table.worldX);
                    int dy = Math.abs(player.worldY - table.worldY);
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    int interactDistance = tileSize * 4;

                    if (distance <= interactDistance && distance < closestTableDistance) {
                        nearCraftingTable = true;
                        closestTable = table;
                        closestTableDistance = distance;
                        craftingHintMessage = "Press Q to open the crafting table";
                        craftingHintMessageCounter = 80;
                    }
                }
            }
            if (obj[i] instanceof Object_Small_Chest) {
                Object_Small_Chest chest = (Object_Small_Chest) obj[i];
                if (chest != null) {
                    int dx = Math.abs(player.worldX - chest.worldX);
                    int dy = Math.abs(player.worldY - chest.worldY);
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    int interactDistance = tileSize * 2;

                    if (distance <= interactDistance && distance < closestChestDistance) {
                        nearChest = true;
                        closestChest = chest;
                        closestChestDistance = distance;
                        chestMessage = "Press E to open the chest";
                        chestMessageCounter = 80;
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
            } else if (nearDoor && !chestUI.isShowingInventory() && !craftingTableUI.isShowing() && keyH.ePressed && closestDoorDistance <= closestChestDistance) {
                closestDoor.interact();
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

        if (!nearDoor && doorHintMessageCounter <= 0) {
            doorHintMessage = "";
        }
        if (!nearCraftingTable && craftingHintMessageCounter <= 0) {
            craftingHintMessage = "";
        }
        if (!nearChest && chestMessageCounter <= 0) {
            chestMessage = "";
        }

        boolean hasHealingItem = player.getInventory().stream().anyMatch(item ->
                item.getName().equals("Apple") || item.getName().equals("blubbery") || item.getName().equals("potion"));
        if (hasHealingItem) {
            healingHintMessage = "Drag Apple, Blubbery, or Potion onto the player to restore HP";
            healingHintMessageCounter = 80;
        } else if (healingHintMessageCounter <= 0) {
            healingHintMessage = "";
        }

        if (gameState == playerState) {
            for (int i = 0; i < monster.length; i++) {
                if (monster[i] != null) {
                    monster[i].update();
                    if (monster[i].isDead && monster[i].fadeAlpha <= 0) {
                        monster[i] = null;
                    }
                }
            }
        }

        attackCounter++;

        if (doorMessageCounter > 0) {
            doorMessageCounter--;
            if (doorMessageCounter <= 0) {
                doorMessage = "";
            }
        }
        if (doorHintMessageCounter > 0) {
            doorHintMessageCounter--;
            if (doorHintMessageCounter <= 0 && !nearDoor) {
                doorHintMessage = "";
            }
        }
        if (chestMessageCounter > 0) {
            chestMessageCounter--;
            if (chestMessageCounter <= 0) {
                chestMessage = "";
            }
        }
        if (healingHintMessageCounter > 0) {
            healingHintMessageCounter--;
            if (healingHintMessageCounter <= 0 && !hasHealingItem) {
                healingHintMessage = "";
            }
        }
        if (craftingHintMessageCounter > 0) {
            craftingHintMessageCounter--;
            if (craftingHintMessageCounter <= 0 && !nearCraftingTable) {
                craftingHintMessage = "";
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        tileH.draw(g2d);

        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                int screenX = obj[i].worldX - player.worldX + player.screenX;
                int screenY = obj[i].worldY - player.worldY + player.screenY;
                if (screenX + tileSize > 0 && screenX < screenWidth &&
                        screenY + tileSize > 0 && screenY < screenHeight) {
                    obj[i].draw(g2d, this);
                }
            }
        }

        for (int i = 0; i < monster.length; i++) {
            if (monster[i] != null) {
                int screenX = monster[i].worldX - player.worldX + player.screenX;
                int screenY = monster[i].worldY - player.worldY + player.screenY;
                if (screenX + tileSize > 0 && screenX < screenWidth &&
                        screenY + tileSize > 0 && screenY < screenHeight) {
                    monsterUi.draw(g2d, monster[i]);
                    if (!monster[i].isDead || monster[i].fadeAlpha > 0) {
                        monster[i].draw(g2d);
                    }
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
                    itemSize = (int)(tileSize * 0.6667f);
                }
                g2d.drawImage(draggedItem.getItem().image,
                        mousePos.x - dragOffsetX,
                        mousePos.y - dragOffsetY,
                        itemSize, itemSize, null);
            }
        }

        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(Color.WHITE);

        int baseMessageY = 30;
        int rightMargin = tileSize;

        if (!chestMessage.isEmpty()) {
            int chestMessageY = baseMessageY;
            int chestMessageX = screenWidth - g2d.getFontMetrics().stringWidth(chestMessage) - rightMargin;
            g2d.drawString(chestMessage, chestMessageX, chestMessageY);
        }

        if (!doorMessage.isEmpty()) {
            int doorMessageY = baseMessageY + (chestMessage.isEmpty() ? 0 : 30);
            int doorMessageX = screenWidth - g2d.getFontMetrics().stringWidth(doorMessage) - rightMargin;
            g2d.drawString(doorMessage, doorMessageX, doorMessageY);
        }

        if (!doorHintMessage.isEmpty()) {
            int doorHintMessageY = baseMessageY + (chestMessage.isEmpty() ? 0 : 30) + (doorMessage.isEmpty() ? 0 : 30);
            int doorHintMessageX = screenWidth - g2d.getFontMetrics().stringWidth(doorHintMessage) - rightMargin;
            g2d.drawString(doorHintMessage, doorHintMessageX, doorHintMessageY);
        }

        if (!healingHintMessage.isEmpty()) {
            int healingHintMessageY = baseMessageY + (chestMessage.isEmpty() ? 0 : 30) +
                    (doorMessage.isEmpty() ? 0 : 30) + (doorHintMessage.isEmpty() ? 0 : 30);
            int healingHintMessageX = screenWidth - g2d.getFontMetrics().stringWidth(healingHintMessage) - rightMargin;
            g2d.drawString(healingHintMessage, healingHintMessageX, healingHintMessageY);
        }

        if (!craftingHintMessage.isEmpty()) {
            int craftingHintMessageY = baseMessageY + (chestMessage.isEmpty() ? 0 : 30) +
                    (doorMessage.isEmpty() ? 0 : 30) + (doorHintMessage.isEmpty() ? 0 : 30) +
                    (healingHintMessage.isEmpty() ? 0 : 30);
            int craftingHintMessageX = screenWidth - g2d.getFontMetrics().stringWidth(craftingHintMessage) - rightMargin;
            g2d.drawString(craftingHintMessage, craftingHintMessageX, craftingHintMessageY);
        }

        g2d.dispose();
    }
}