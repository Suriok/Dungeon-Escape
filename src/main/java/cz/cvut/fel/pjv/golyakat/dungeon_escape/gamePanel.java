package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Player;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.ChestUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.PlayerUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.MonsterUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.bars.DefensBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.bars.HealthBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.tile.TileManger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    public GameObject obj[] = new GameObject[10];
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

    public ChestUI chestUI;
    public PlayerUI playerUI;
    public ChestInventoryManager chestInventoryManager;

    private int attackCounter = 0;
    private static final int ATTACK_COOLDOWN = 30;

    // Drag-and-drop variables
    private ChestInventoryManager.ItemData draggedItem = null;
    private int draggedItemIndex = -1;
    private int dragOffsetX, dragOffsetY;

    public gamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        chestInventoryManager = new ChestInventoryManager();
        healthBar = new HealthBar(this);
        defensBar = new DefensBar(this);
        chestUI = new ChestUI(this);
        playerUI = new PlayerUI(this);
        monsterUi = new MonsterUI(this);
        gameState = playerState;

        // Mouse listener for attack and starting drag
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Mouse pressed: Button=" + e.getButton() + ", ChestUI showing=" + chestUI.isShowingInventory());
                if (e.getButton() == MouseEvent.BUTTON3) { // Right-click to attack
                    if (!chestUI.isShowingInventory()) {
                        System.out.println("Attempting to attack. Attack counter: " + attackCounter);
                        if (attackCounter >= ATTACK_COOLDOWN) {
                            player.attack();
                            attackCounter = 0;
                            System.out.println("Attack executed.");
                        } else {
                            System.out.println("Attack on cooldown. Need " + ATTACK_COOLDOWN + " frames, current: " + attackCounter);
                        }
                    } else {
                        System.out.println("Cannot attack: Chest inventory is open.");
                    }
                } else if (e.getButton() == MouseEvent.BUTTON1) { // Left-click to start dragging
                    if (!chestUI.isShowingInventory()) {
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

                            // Expand inventory for drag logic
                            java.util.List<ChestInventoryManager.ItemData> expandedItems = new java.util.ArrayList<>();
                            for (ChestInventoryManager.ItemData item : player.getInventory()) {
                                int quantity = item.getQuantity();
                                for (int i = 0; i < quantity; i++) {
                                    expandedItems.add(new ChestInventoryManager.ItemData(item.getName(), 1));
                                }
                            }

                            if (index >= 0 && index < expandedItems.size()) {
                                draggedItem = expandedItems.get(index);
                                draggedItemIndex = player.getInventory().indexOf(
                                        player.getInventory().stream()
                                                .filter(item -> item.getName().equals(draggedItem.getName()))
                                                .findFirst()
                                                .orElse(null)
                                );
                                dragOffsetX = e.getX() - (offsetX + col * cellWidth + (cellWidth - itemSize) / 2);
                                dragOffsetY = e.getY() - (offsetY + row * cellHeight + (cellHeight - itemSize) / 2);
                                System.out.println("Started dragging item: " + draggedItem.getName() + " at index: " + draggedItemIndex);
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggedItem != null) {
                    // Check if dropped on the key-locked door (gp.obj[6])
                    if (draggedItem.getName().equals("Key") && obj[6] != null && obj[6] instanceof Object_DoorSide) {
                        Object_DoorSide door = (Object_DoorSide) obj[6];
                        if (door.requiresKey && !door.isOpen()) {
                            int doorScreenX = obj[6].worldX - player.worldX + player.screenX;
                            int doorScreenY = obj[6].worldY - player.worldY + player.screenY;
                            Rectangle doorBounds = new Rectangle(doorScreenX, doorScreenY, tileSize, tileSize);
                            if (doorBounds.contains(e.getPoint())) {
                                door.unlock();
                                // Remove the key from inventory
                                ChestInventoryManager.ItemData itemToRemove = player.getInventory().stream()
                                        .filter(item -> item.getName().equals(draggedItem.getName()))
                                        .findFirst()
                                        .orElse(null);
                                if (itemToRemove != null) {
                                    int index = player.getInventory().indexOf(itemToRemove);
                                    player.removeItem(index);
                                }
                                // Clear doorHintMessage to avoid overlap
                                doorHintMessage = "";
                                doorHintMessageCounter = 0;
                                // Set doorMessage to indicate the door is unlocked
                                doorMessage = "Door unlocked!";
                                doorMessageCounter = 120;
                                System.out.println("Player used the key to unlock the door at index: 6");
                            }
                        }
                    }
                    draggedItem = null;
                    draggedItemIndex = -1;
                    repaint();
                }
            }
        });

        // Mouse motion listener for dragging
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
                    System.out.println("Reset chest inventory XML on window close.");
                }
            });
        }
    }

    public void setUpObjects() {
        Map<String, Integer> chest0Items = new HashMap<>();
        chest0Items.put("leather_pants", 1);
        chest0Items.put("leather_helmet", 1);
        chest0Items.put("iron_sword", 1);
        obj[0] = new Object_Small_Chest(this, 0, chest0Items);
        obj[0].worldX = 15 * tileSize;
        obj[0].worldY = 21 * tileSize;

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
        healthBar.update(player.life);
        defensBar.update(player.getTotalDefense());

        // Check proximity to doors and update doorHintMessage
        boolean nearDoor = false;
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] instanceof Object_DoorSide) {
                Object_DoorSide door = (Object_DoorSide) obj[i];
                if (door != null) {
                    int dx = Math.abs(player.worldX - door.worldX);
                    int dy = Math.abs(player.worldY - door.worldY);
                    int distance = (int) Math.sqrt(dx * dx + dy * dy);
                    int interactDistance = tileSize * 2; // Within 2 tiles

                    if (distance <= interactDistance) {
                        nearDoor = true;
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
                        break; // Only show message for the closest door
                    }
                }
            }
        }

        if (!nearDoor && doorHintMessageCounter <= 0) {
            doorHintMessage = "";
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
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        tileH.draw(g2d);

        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                obj[i].draw(g2d, this);
            }
        }

        for (int i = 0; i < monster.length; i++) {
            if (monster[i] != null) {
                monsterUi.draw(g2d, monster[i]);
                if (!monster[i].isDead || monster[i].fadeAlpha > 0) {
                    monster[i].draw(g2d);
                }
            }
        }

        player.draw(g2d);

        healthBar.draw(g2d);
        defensBar.draw(g2d);

        chestUI.draw(g2d);

        playerUI.draw(g2d);

        // Draw the dragged item
        if (draggedItem != null) {
            Point mousePos = getMousePosition();
            if (mousePos != null) {
                int itemSize = tileSize;
                g2d.drawImage(draggedItem.getItem().image,
                        mousePos.x - dragOffsetX,
                        mousePos.y - dragOffsetY,
                        itemSize, itemSize, null);
            }
        }

        // --- Message Display Section ---
        // Set the font and color for all messages
        // You can change the font family, style (e.g., Font.BOLD), and size here
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        // You can change the text color here (e.g., Color.YELLOW, new Color(255, 0, 0))
        g2d.setColor(Color.WHITE);

        // Define the base position for the top-right corner
        // Change this value to adjust the vertical starting position (smaller = closer to top)
        int baseMessageY = 30;
        // Change this value to adjust the horizontal margin from the right edge
        int rightMargin = tileSize;

        // Draw chest message
        if (!chestMessage.isEmpty()) {
            int chestMessageY = baseMessageY;
            int chestMessageX = screenWidth - g2d.getFontMetrics().stringWidth(chestMessage) - rightMargin;
            g2d.drawString(chestMessage, chestMessageX, chestMessageY);
        }


        // Draw door hint message (e.g., "Press E to open the door")
        if (!doorHintMessage.isEmpty()) {
            int doorHintMessageY = baseMessageY + (chestMessage.isEmpty() ? 0 : 30) + (doorMessage.isEmpty() ? 0 : 30);
            int doorHintMessageX = screenWidth - g2d.getFontMetrics().stringWidth(doorHintMessage) - rightMargin;
            g2d.drawString(doorHintMessage, doorHintMessageX, doorHintMessageY);
        }
        // --- End of Message Display Section ---

        g2d.dispose();
    }
}