package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Player;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.HealthBar;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.tile.TileManger;

import javax.swing.*;
import java.awt.*;

// Hlavní panel hry, kde se odehrává veškeré vykreslování a logika herní smyčky
public class gamePanel extends JPanel implements Runnable {

    // === Nastavení obrazovky ===
    final int originalTileSize = 16; // Originální velikost dlaždice (např. 16x16 pixelů)
    final int scale = 3; // Násobení velikosti pro lepší viditelnost

    public final int tileSize = originalTileSize * scale; // Výsledná velikost tile
    public final int maxScreenCol = 20; // Počet dlaždic na šířku obrazovky
    public final int maxScreenRow = 12; // Počet dlaždic na výšku obrazovky
    public final int screenWidth = tileSize * maxScreenCol; // Celková šířka obrazovky
    public final int screenHeight = tileSize * maxScreenRow; // Celková výška obrazovky

    // === Nastavení světa ===
    public final int maxWorldCol = 60;  // Počet sloupců ve světě (mapa)
    public final int maxWorldRow = 60;  // Počet řádků ve světě (mapa)

    // === FPS nastavení ===
    int FPS = 60; // Počet snímků za sekundu

    // === Herní komponenty ===
    TileManger tileH = new TileManger(this); // Správa dlaždic
    KeyHandler keyH = new KeyHandler(); // Ovládání klávesnice
    Thread gameThread; // Herní vlákno
    public Collision collisionChecker = new Collision(this); // Kontrola kolizí
    public AssetSetter assetSetter = new AssetSetter(this); // Nastavení herních objektů

    // Entity a objekty
    public Player player = new Player(this, keyH); // Hráč
    public GameObject obj[] = new GameObject[10]; // Herní objekty (truhly, dveře atd.)
    public HealthBar healthBar; // Ukazatel zdraví hráče
    public Entity monster[] = new Entity[20]; // Pole monster

    // Stav hry
    public int gameState;
    public final int playerState = 1;

    // UI message for door interaction
    public String doorMessage = "";
    public int doorMessageCounter = 0;

    // === Konstruktor gamePanel ===
    public gamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); // Nastavení velikosti okna
        this.setBackground(Color.BLACK); // Barva pozadí
        this.setDoubleBuffered(true); // Zlepšení vykreslování
        this.addKeyListener(keyH); // Přidání posluchače klávesnice
        this.setFocusable(true); // Panel může přijímat klávesnici

        healthBar = new HealthBar(this); // Inicializace health baru
        gameState = playerState; // Nastavíme výchozí stav hry
    }

    // Nastaví všechny objekty ve hře (truhly, dveře, monstra atd.)
    public void setUpObjects() {
        assetSetter.setObg();
        assetSetter.setMonster();
    }

    // Spuštění herního vlákna
    public void startGameThread() {
        gameThread = new Thread(this); // Vytvoříme nové vlákno s gamePanelem
        gameThread.start(); // Spustíme vlákno
    }

    // Herní smyčka
    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS; // Interval mezi snímky v nanosekundách
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update(); // Aktualizace stavu hry
            repaint(); // Vykreslení hry

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000; // Převedeme na milisekundy

                if (remainingTime < 0) {
                    remainingTime = 0;
                }

                Thread.sleep((long) remainingTime); // Pauza mezi snímky
                nextDrawTime += drawInterval; // Nastavíme čas pro další snímek

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Aktualizace všech herních objektů
    public void update() {
        player.update(); // Aktualizace hráče
        healthBar.update(player.life); // Aktualizace health baru

        if (gameState == playerState) {
            for (int i = 0; i < monster.length; i++) {
                if (monster[i] != null) {
                    monster[i].update(); // Aktualizace každého monstra
                }
            }
        }

        // Update door message visibility
        if (doorMessageCounter > 0) {
            doorMessageCounter--;
            if (doorMessageCounter <= 0) {
                doorMessage = "";
            }
        }
    }

    // Vykreslování všech prvků na obrazovku
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 1. Vykreslení mapy
        tileH.draw(g2d);

        // 2. Vykreslení herních objektů (např. truhla, dveře)
        for (int i = 0; i < obj.length; i++) {
            if (obj[i] != null) {
                obj[i].draw(g2d, this);
            }
        }

        // 3. Vykreslení monster
        for (int i = 0; i < monster.length; i++) {
            if (monster[i] != null) {
                monster[i].draw(g2d);
            }
        }

        // 4. Vykreslení hráče
        player.draw(g2d);

        // 5. Vykreslení health baru (na konec, aby byl vždy navrchu)
        healthBar.draw(g2d);

        // 6. Vykreslení UI zprávy pro dveře
        if (!doorMessage.isEmpty()) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 20));
            g2d.setColor(Color.WHITE);
            int messageX = tileSize;
            int messageY = tileSize * 11;
            g2d.drawString(doorMessage, messageX, messageY);
        }

        g2d.dispose(); // Uvolnění prostředků
    }
}