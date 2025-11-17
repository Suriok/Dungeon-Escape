package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.saveData.GameSaveManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite.Player;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ui.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.bars.DefensBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.bars.HealthBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.tile.TileManger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;

import static cz.cvut.fel.pjv.golyakat.dungeon_escape.ItemType.*;

/**
 * Main game panel managing game logic, rendering, and interactions.
 */
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
    public final int maxMap = 2;
    public int currentMap = 0;
    final int FPS = 60;
    public final TileManger tileH = new TileManger(this);
    public final KeyHandler keyH = new KeyHandler();
    Thread gameThread;
    public final Collision collisionChecker = new Collision(this);
    public AssetSetter assetSetter;
    private final TitleScreenUI titleUi;
    public final Player player = new Player(this, keyH);
    public final GameObject[][] obj = new GameObject[maxMap][11];
    public final HealthBar healthBar;
    public final DefensBar defensBar;
    public final Entity[][] monster = new Entity[maxMap][20];
    public final MonsterUI monsterUi;
    public boolean[] levelSpawned = new boolean[maxMap];
    public int gameState;
    // === UI States ===
    public final int titleState = 0;
    public final int playerState = 1;
    public final int gameOverState = 2;
    public final int winState = 3;
    public final ChestUI chestUI;
    public final CraftingTableUI craftingTableUI;
    public final PlayerUI playerUI;
    // === Inventory & Save Management ===
    public final ChestInventoryManager chestInventoryManager;
    public final GameSaveManager saveManager;
    // === UI Message Overlay ===
    public final HintMessage doorHintMessage = new HintMessage();
    public final HintMessage chestMessage = new HintMessage();
    public final HintMessage healingHintMessage = new HintMessage();
    public final HintMessage craftingHintMessage = new HintMessage();

    /**
     * Represents a short-duration on-screen hint message.
     * Used to guide the player with interaction prompts (e.g. "Press E to open").
     */
    static public class HintMessage {
        public String text = "";
        public int counter = 0;

        /**
         * Displays a hint message for a specified duration.
         *
         * @param message  the text to display
         * @param duration the number of frames the message will be visible
         */
        public void show(String message, int duration) {
            this.text = message;
            this.counter = duration;
        }

        /**
         * Updates the visibility counter of the hint message.
         */
        public void update() {
            if (counter > 0) counter--;
            if (counter <= 0) text = "";
        }

        /**
         * Checks if the hint message is currently visible.
         *
         * @return true if the message is visible, false otherwise
         */
        public boolean isVisible() {
            return !text.isEmpty();
        }
    }

    /**
     * Initializes the game panel with UI components and event listeners.
     */
    public gamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        addKeyListener(keyH);
        setFocusable(true);
        requestFocusInWindow();

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (gameState == titleState || gameState == gameOverState || gameState == winState) {
                    titleUi.mouseMoved(e.getPoint());
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;

                if (gameState == titleState || gameState == gameOverState || gameState == winState) {
                    titleUi.mouseReleased(e.getPoint());
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();

                // === Attack (Right Click) ===
                if (e.getButton() == MouseEvent.BUTTON3) {
                    boolean clickedOnInventory =
                            chestUI.isShowingInventory() && chestUI.getClickedItemIndex(e.getPoint()) != -1 ||
                                    playerUI.getClickedInventoryIndex(e.getPoint()) != -1 ||
                                    craftingTableUI.isShowing() && craftingTableUI.getSlotAt(e.getPoint()) != -1;

                    if (!clickedOnInventory) {
                        player.attack();
                        repaint();
                    }
                    return;
                }

                // === Interaction (Left Click) ===
                if (e.getButton() != MouseEvent.BUTTON1) return;

                if (gameState == titleState || gameState == gameOverState || gameState == winState) {
                    titleUi.mousePressed(e.getPoint());
                    return;
                }

                boolean needsRepaint = false;

                if (chestUI.isShowingInventory()) {
                    int index = chestUI.getClickedItemIndex(e.getPoint());
                    if (index != -1) {
                        ChestInventoryManager.ItemData item = chestUI.takeItem(index);
                        if (item != null) {
                            putItemToRightPlace(item);
                            needsRepaint = true;
                        }
                    }
                }

                int idx = playerUI.getClickedInventoryIndex(e.getPoint());
                if (idx != -1) {
                    ChestInventoryManager.ItemData item = player.getInventory().get(idx);
                    if (item != null) {
                        if (item.getType() == HEALING) {
                            player.consumeHealingItem(item);
                            player.removeItem(idx);
                            needsRepaint = true;
                        } else if (item.getType() == ARMOR) {
                            int slot = playerUI.getArmorSlotIndex(item);
                            if (slot != -1) {
                                player.equipArmor(item.getItem(), slot);
                                player.removeItem(idx);
                                needsRepaint = true;
                            }
                        } else if (item.getType() == WEAPON) {
                            player.equipWeapon(item.getItem());
                            player.removeItem(idx);
                            needsRepaint = true;
                        }
                    }
                }

                if (craftingTableUI.isShowing()) {
                    Point p = e.getPoint();
                    Rectangle craftButtonBounds = craftingTableUI.getCraftButtonBounds();
                    if (craftButtonBounds != null && craftButtonBounds.contains(p)) {
                        craftingTableUI.craftSilverKey();
                        needsRepaint = true;
                    } else {
                        int slot = craftingTableUI.getSlotAt(p);
                        if (slot != -1) {
                            ChestInventoryManager.ItemData item = craftingTableUI.removeFromSlot(slot);
                            if (item != null) {
                                player.addItem(item);
                                needsRepaint = true;
                            }
                        } else if (idx != -1) {
                            ChestInventoryManager.ItemData item = player.getInventory().get(idx);
                            if (item != null && item.getType() == ItemType.KEY_PART) {
                                craftingTableUI.putToFirstEmpty(item);
                                player.removeItem(idx);
                                needsRepaint = true;
                            }
                        }
                    }
                }

                if (needsRepaint) repaint();
            }
        });

        chestInventoryManager = new ChestInventoryManager();
        saveManager = new GameSaveManager(this);
        assetSetter = new AssetSetter(this);
        healthBar = new HealthBar(this);
        defensBar = new DefensBar();
        chestUI = new ChestUI(this);
        craftingTableUI = new CraftingTableUI(this);
        playerUI = new PlayerUI(this);
        monsterUi = new MonsterUI(this);
        titleUi = new TitleScreenUI(this);
        gameState = titleState;
    }

    /**
     * Places an item in the appropriate location based on its type.
     *
     * @param item the item to place
     */
    private void putItemToRightPlace(ChestInventoryManager.ItemData item) {
        if (item == null) return;
        switch (item.getType()) {
            case HEALING, KEY, KEY_PART -> player.addItem(item);
            case ARMOR -> player.equipArmor(item.getItem(), playerUI.getArmorSlotIndex(item));
            case WEAPON -> player.equipWeapon(item.getItem());
        }
    }

    /**
     * Sets up game objects and monsters for the current state.
     */
    public void setUpObjects() {
        assetSetter.setObg();
        assetSetter.setMonster();
        gameState = titleState;
    }

    /**
     * Starts the game thread to run the game loop.
     */
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Saves the current game state to a file.
     */
    public void saveGame() {
        saveManager.saveGame();
    }

    /**
     * Loads the saved game state from a file.
     */
    public void loadGame() {
        saveManager.loadGame();
        gameState = playerState;
        repaint();
    }
    /**
     * Runs the game loop to update and render the game at a fixed FPS.
     */
    @Override
    public void run() {
        double drawInterval = 1_000_000_000.0 / FPS;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            // === Game Logic Update ===
            update();
            // === Rendering ===
            repaint();

            try {
                double remainingTime = (nextDrawTime - System.nanoTime()) / 1_000_000;
                if (remainingTime < 0) remainingTime = 0;
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Updates the game state, including player, monsters, and UI elements.
     */
    public void update() {
        if (gameState != playerState) return;

        player.update();
        healthBar.update(player.life);
        defensBar.update(player.getTotalDefense());

        Object_DoorSide closestSideDoor = null;
        Object_DoorFront closestFrontDoor = null;
        Object_Small_Chest closestChest = null;
        Object_CraftingTable closestTable = null;
        int closestDoorDistance = Integer.MAX_VALUE;
        int closestChestDistance = Integer.MAX_VALUE;
        int closestTableDistance = Integer.MAX_VALUE;

        for (GameObject object : obj[currentMap]) {
            if (object == null) continue;

            int dx = Math.abs(player.worldX - object.worldX);
            int dy = Math.abs(player.worldY - object.worldY);
            int distance = (int) Math.sqrt(dx * dx + dy * dy);

            if (object instanceof Object_DoorSide door && distance < closestDoorDistance && distance <= tileSize * 2) {
                closestSideDoor = door;
                closestFrontDoor = null;
                closestDoorDistance = distance;
                if (!door.isOpen() && !doorHintMessage.isVisible()) {
                    doorHintMessage.show(door.isOpen() ? "Door opened!" : door.requiresKey ? "Requires a Key" : "Press E to open", 40);
                }
            } else if (object instanceof Object_DoorFront door && distance < closestDoorDistance && distance <= tileSize * 2) {
                closestSideDoor = null;
                closestFrontDoor = door;
                closestDoorDistance = distance;
                if (!door.isOpen() && !doorHintMessage.isVisible()) {
                    doorHintMessage.show(door.isOpen() ? "Door opened!" : door.requiresKey ? "Requires a Key" : "Press E to open", 40);
                }
            } else if (object instanceof Object_Small_Chest chest && distance < closestChestDistance && distance <= tileSize * 2) {
                closestChest = chest;
                closestChestDistance = distance;
                chestMessage.show("Press E to open/close chest", 40);
            } else if (object instanceof Object_CraftingTable table && distance < closestTableDistance && distance <= tileSize * 4) {
                closestTable = table;
                closestTableDistance = distance;
                craftingHintMessage.show("Press Q to open crafting table", 40);
            }
        }

        if (keyH.ePressed || keyH.qPressed) {
            if (closestTable != null && !chestUI.isShowingInventory() && keyH.qPressed && closestTableDistance <= closestDoorDistance && closestTableDistance <= closestChestDistance) {
                if (craftingTableUI.isShowing()) {
                    craftingTableUI.close();
                } else {
                    craftingTableUI.open();
                }
            } else if ((closestSideDoor != null || closestFrontDoor != null) && !chestUI.isShowingInventory() && !craftingTableUI.isShowing() && keyH.ePressed) {
                if (closestSideDoor != null) closestSideDoor.interact();
                else closestFrontDoor.interact();
            } else if (closestChest != null && !craftingTableUI.isShowing() && keyH.ePressed) {
                if (chestUI.isShowingInventory()) {
                    chestUI.closeInventory();
                } else {
                    chestUI.openChest(closestChest);
                }
            }
            keyH.ePressed = false;
            keyH.qPressed = false;
        }

        if (player.getInventory().stream().anyMatch(item -> item.getType() == HEALING)) {
            healingHintMessage.show("Click item to restore HP", 40);
        }

        if (gameState == playerState) {
            for (Entity monster : monster[currentMap]) {
                if (monster != null) {
                    monster.update();
                    if (monster.isDead) {
                        return;
                    }
                }
            }
        }

        doorHintMessage.update();
        chestMessage.update();
        healingHintMessage.update();
        craftingHintMessage.update();

        if (player.life <= 0) {
            gameState = gameOverState;
        }
    }

    /**
     * Renders the game components on the screen.
     *
     * @param g the Graphics context used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // --- [ИСПРАВЛЕННАЯ ЛОГИКА] ---

        // 1. ЕСЛИ МЫ В ИГРЕ (playerState)
        if (gameState == playerState) {
            // Рисуем все игровые элементы
            tileH.draw(g2d);
            for (GameObject object : obj[currentMap]) {
                if (object != null) object.draw(g2d, this);
            }
            for (Entity monster : monster[currentMap]) {
                if (monster != null) {
                    monsterUi.draw(g2d, monster);
                    monster.draw(g2d);
                }
            }
            player.draw(g2d);
            healthBar.draw(g2d);
            defensBar.draw(g2d);
            chestUI.draw(g2d);
            craftingTableUI.draw(g2d);
            playerUI.draw(g2d);

            // Рисуем подсказки (Hint Messages)
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            g2d.setColor(Color.WHITE);
            int messageX = screenWidth - tileSize * 7;
            int messageY = 25;
            int lineHeight = 30;

            String[] messages = {
                    chestMessage.isVisible() ? "Chest: " + chestMessage.text : "",
                    doorHintMessage.isVisible() ? "Door: " + doorHintMessage.text : "",
                    healingHintMessage.isVisible() ? "Heal: " + healingHintMessage.text : "",
                    craftingHintMessage.isVisible() ? "Craft: " + craftingHintMessage.text : ""
            };

            for (String message : messages) {
                if (!message.isEmpty()) {
                    g2d.drawString(message, messageX, messageY);
                    messageY += lineHeight;
                }
            }

            // 2. ЕСЛИ МЫ НЕ В ИГРЕ (titleState, gameOverState, или winState)
        } else {
            // Передаем управление titleUi, который сам решит, что рисовать
            // (на основе кода, который мы в него добавили)
            titleUi.draw(g2d);
        }

        g2d.dispose();
    }

    /**
     * Starts a new game by resetting the game state and initializing objects.
     */
    public void startNewGame() {
        currentMap = 0;
        Arrays.fill(levelSpawned, false);
        levelSpawned[currentMap] = true;
        player.reset();
        player.equipWeapon(new Iron_sword(2));
        chestInventoryManager.resetChestData();
        setUpObjects();
        gameState = playerState;
        repaint();
    }

    /**
     * Creates a game item based on its name.
     *
     * @param name the name of the item to create
     * @return the created GameObject or null if creation fails
     */
    public GameObject makeItem(String name) {
        return ItemFactory.makeItem(name);
    }
}