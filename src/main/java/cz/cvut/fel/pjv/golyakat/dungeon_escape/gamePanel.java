package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Player;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.ChestUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.PlayerUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.MonsterUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.bars.DefensBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.bars.HealthBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.tile.TileManger;

import javax.swing.*;
import java.awt.*;
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
    public MonsterUI monsterUi; // Added MonsterUi instance

    public int gameState;
    public final int playerState = 1;

    public String doorMessage = "";
    public int doorMessageCounter = 0;
    public String chestMessage = "";
    public int chestMessageCounter = 0;

    public ChestUI chestUI;
    public PlayerUI playerUI;
    public ChestInventoryManager chestInventoryManager;

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
        monsterUi = new MonsterUI(this); // Initialize MonsterUi
        gameState = playerState;

        // Добавляем слушатель закрытия окна
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    chestInventoryManager.resetChestData(); // Очищаем chest_inventory.xml
                    System.out.println("Reset chest inventory XML on window close.");
                }
            });
        }
    }

    public void setUpObjects() {
        // Сундук с id = 0: кожаные штаны и кожаный шлем
        Map<String, Integer> chest0Armor = new HashMap<>();
        chest0Armor.put("leather_pants", 1);
        chest0Armor.put("leather_helmet", 1);
        obj[0] = new Object_Small_Chest(this, 0, chest0Armor);
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

        if (gameState == playerState) {
            for (int i = 0; i < monster.length; i++) {
                if (monster[i] != null) {
                    monster[i].update();
                    // Remove fully faded monsters
                    if (monster[i].isDead && monster[i].fadeAlpha <= 0) {
                        monster[i] = null;
                    }
                }
            }
        }

        if (doorMessageCounter > 0) {
            doorMessageCounter--;
            if (doorMessageCounter <= 0) {
                doorMessage = "";
            }
        }
        if (chestMessageCounter > 0) {
            chestMessageCounter--;
            if (chestMessageCounter <= 0) {
                chestMessage = "";
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        tileH.draw(g2d);

        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                obj[i].draw(g2d, this);
            }
        }

        // Draw monsters and their UI
        for (int i = 0; i < monster.length; i++) {
            if (monster[i] != null) {
                monsterUi.draw(g2d, monster[i]); // Draw health bar and handle fade
                if (!monster[i].isDead || monster[i].fadeAlpha > 0) {
                    monster[i].draw(g2d); // Draw monster sprite
                }
            }
        }

        player.draw(g2d);

        healthBar.draw(g2d);
        defensBar.draw(g2d);

        chestUI.draw(g2d);

        playerUI.draw(g2d);

        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(Color.WHITE);

        int baseMessageY = screenHeight - 70;

        if (!chestMessage.isEmpty()) {
            int chestMessageY = baseMessageY;
            int chestMessageX = screenWidth - g2d.getFontMetrics().stringWidth(chestMessage) - tileSize + 30;
            g2d.drawString(chestMessage, chestMessageX, chestMessageY);
        }

        if (!doorMessage.isEmpty()) {
            int doorMessageY = baseMessageY - (chestMessage.isEmpty() ? 0 : 30);
            int doorMessageX = screenWidth - g2d.getFontMetrics().stringWidth(doorMessage) - tileSize;
            g2d.drawString(doorMessage, doorMessageX, doorMessageY);
        }

        g2d.dispose();
    }
}