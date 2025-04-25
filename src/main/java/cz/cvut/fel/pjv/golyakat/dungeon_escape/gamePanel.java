package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Player;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.ChestUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.UI.PlayerUI;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.HealthBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.tile.TileManger;

import javax.swing.*;
import java.awt.*;

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
    public Entity monster[] = new Entity[20];

    public int gameState;
    public final int playerState = 1;

    public String doorMessage = "";
    public int doorMessageCounter = 0;
    public String chestMessage = "";
    public int chestMessageCounter = 0;

    public ChestUI chestUI;
    public PlayerUI playerUI;

    public gamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        healthBar = new HealthBar(this);
        chestUI = new ChestUI(this);
        playerUI = new PlayerUI(this);
        gameState = playerState;
    }

    public void setUpObjects() {
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

        if (gameState == playerState) {
            for (int i = 0; i < monster.length; i++) {
                if (monster[i] != null) {
                    monster[i].update();
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

        for (int i = 0; i < monster.length; i++) {
            if (monster[i] != null) {
                monster[i].draw(g2d);
            }
        }

        player.draw(g2d);

        healthBar.draw(g2d);

        chestUI.draw(g2d);

        playerUI.draw(g2d);

        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        g2d.setColor(Color.WHITE);


        // Базовая Y-координата для сообщений
        int baseMessageY = screenHeight - 70;

        // Отрисовка сообщения для сундука
        if (!chestMessage.isEmpty()) {
            int chestMessageY = baseMessageY; // Первое сообщение внизу
            int chestMessageX = screenWidth - g2d.getFontMetrics().stringWidth(chestMessage) - tileSize + 30; // Справа с отступом
            g2d.drawString(chestMessage, chestMessageX, chestMessageY);
        }

        // Отрисовка сообщения для двери (выше сообщения для сундука, если оно есть)
        if (!doorMessage.isEmpty()) {
            int doorMessageY = baseMessageY - (chestMessage.isEmpty() ? 0 : 30); // Сдвигаем вверх на 30 пикселей, если есть chestMessage
            int doorMessageX = screenWidth - g2d.getFontMetrics().stringWidth(doorMessage) - tileSize; // Справа с отступом
            g2d.drawString(doorMessage, doorMessageX, doorMessageY);
        }

        g2d.dispose();
    }
}