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
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
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

    // SCREEN SETTINGS
    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 20;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public final int maxWorldCol = 60;
    public final int maxWorldRow = 60;

    // FPS
    int FPS = 60;

    // SYSTEM
    TileManger tileH = new TileManger(this);
    public KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    public Collision collisionChecker = new Collision(this);
    public AssetSetter assetSetter;
    Sound sound = new Sound();




    // ENTITY AND OBJECT
    public Player player = new Player(this, keyH);
    public GameObject obj[] = new GameObject[11];
    public HealthBar healthBar;
    public DefensBar defensBar;
    public Entity monster[] = new Entity[20];
    public MonsterUI monsterUi;

    public int gameState;
    public final int playerState = 1;

    // MESSEGES
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
    public boolean objectsLogged = false;
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
        assetSetter = new AssetSetter(this);
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
                } else if (draggedItem.getName().equals("Key")) {
                    if (obj[6] != null && obj[6] instanceof Object_DoorSide) {
                        Object_DoorSide door = (Object_DoorSide) obj[6];
                        if (door.requiresKey && !door.isOpen()) {
                            int doorScreenX = obj[6].worldX - player.worldX + player.screenX;
                            int doorScreenY = obj[6].worldY - player.worldY + player.screenY;
                            Rectangle doorBounds = new Rectangle(doorScreenX, doorScreenY, tileSize, tileSize);
                            if (doorBounds.contains(e.getPoint())) {
                                door.unlock();
                                removeDraggedItem();
                                doorHintMessage = "";
                                doorHintMessageCounter = 0;
                                doorMessage = "Side door unlocked!";
                                doorMessageCounter = 120;
                                if (!dragDroppedLogged) {
                                    System.out.println("Item " + draggedItem.getName() + " dropped to side door");
                                    dragDroppedLogged = true;
                                }
                                clearDrag();
                                repaint();
                                return;
                            }
                        }
                    }
                } else if (draggedItem.getName().equals("SilverKey")) {
                    if (obj[5] != null && obj[5] instanceof Object_DoorFront) {
                        Object_DoorFront door = (Object_DoorFront) obj[5];
                        if (door.requiresKey && !door.isOpen()) {
                            int doorScreenX = obj[5].worldX - player.worldX + player.screenX;
                            int doorScreenY = obj[5].worldY - player.worldY + player.screenY;
                            Rectangle doorBounds = new Rectangle(doorScreenX, doorScreenY, tileSize, tileSize);
                            if (doorBounds.contains(e.getPoint())) {
                                door.unlock();
                                removeDraggedItem();
                                doorHintMessage = "";
                                doorHintMessageCounter = 0;
                                doorMessage = "Front door unlocked!";
                                doorMessageCounter = 120;
                                if (!dragDroppedLogged) {
                                    System.out.println("Item " + draggedItem.getName() + " dropped to front door");
                                    dragDroppedLogged = true;
                                }
                                clearDrag();
                                repaint();
                                return;
                            }
                        }
                    }
                }

                // Return to source if invalid drop
                if (sourceInventory == player) {
                    if (draggedItemIndex == -2) {
                        player.equipWeapon(draggedItem.getItem());
                    } else if (draggedItemIndex >= 0 && draggedItemIndex < 4) {
                        player.equipArmor(draggedItem.getItem(), draggedItemIndex);
                    } else {
                        if (!playerUI.isArmor(draggedItem)) {
                            player.addItem(draggedItem);
                        }
                    }
                } else if (sourceInventory instanceof Object_Small_Chest) {
                    ((Object_Small_Chest) sourceInventory).getItems().add(draggedItem);
                    chestInventoryManager.updateChestData(((Object_Small_Chest) sourceInventory).getId(),
                            new ChestInventoryManager.ChestData(((Object_Small_Chest) sourceInventory).isOpen(), ((Object_Small_Chest) sourceInventory).getItems()));
                } else if (sourceInventory == craftingTableUI) {
                    if (!playerUI.isArmor(draggedItem)) {
                        player.addItem(draggedItem);
                    }
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
        dragDroppedLogged = false;
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
        // For craftingTableUI, the item is already removed via setCraftingSlot(null)
    }

    public void setUpObjects() {
        assetSetter.setObg();
        assetSetter.setMonster();
        playMusic(0);
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
        Object_DoorSide closestSideDoor = null;
        Object_DoorFront closestFrontDoor = null;
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
                        closestSideDoor = door;
                        closestFrontDoor = null;
                        closestDoorDistance = distance;
                        if (door.isOpen()) {
                            doorHintMessage = "Door opened!";
                            doorHintMessageCounter = 80;
                        } else if (door.requiresKey) {
                            doorHintMessage = "This door requires a Key to open.";
                            doorHintMessageCounter = 80;
                        } else {
                            doorHintMessage = "Press E to open the door";
                            doorHintMessageCounter = 80;
                        }
                    }
                }
            } else if (obj[i] instanceof Object_DoorFront) {
                Object_DoorFront door = (Object_DoorFront) obj[i];
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
                            doorHintMessage = "Door opened!";
                            doorHintMessageCounter = 80;
                        } else if (door.requiresKey) {
                            doorHintMessage = "This door requires a Silver Key to open.";
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
    public void playMusic(int i) {
        sound.setFile(i);
        sound.playSound();
        sound.loopSound();
    }

    public void stopMusic() {
        sound.StopSound();
    }
    public void playSE(int i){
        sound.setFile(i);
        sound.playSound();
    }
}