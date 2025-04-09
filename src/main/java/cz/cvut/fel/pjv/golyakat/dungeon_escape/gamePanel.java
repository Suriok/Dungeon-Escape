package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import Sprite.Player;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.tile.TileManger;

import javax.swing.*;
import java.awt.*;

public class gamePanel extends JPanel implements Runnable {
    //  screen settings
    final int originalTileSize = 16; //map tiles and players
    final int scale = 2;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 30;
    public final int maxScreenRow = 20;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    //World settings
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;
    public final int worldWidth = tileSize * maxWorldCol;
    public final int worldHeight = tileSize * maxWorldRow;

    //FPS
    int FPS = 60;

    TileManger tileH = new TileManger(this);
    KeyHandler keyH = new KeyHandler();
    Thread gameThread; //Keep program running without stop
    public Player player = new Player(this,keyH);

    // GamePanel Constructor
    public gamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this); //Passing gamePanel to the constructor
        gameThread.start(); // call run method
    }

    // When call gameThread automatically start the run method
    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS; // To reload screen every 0.01666 sec
        double nextDrawTime = System.nanoTime() + drawInterval; // Next time reload will be after 60fps

        while(gameThread != null) { // As long as gameThread live process will repeat
            // Update character position
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime(); //Time left to reload
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) {
                    remainingTime = 0;  //When it takes more than in variable drawInterval than no time left
                }

                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void update(){
        player.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        //Draw tiles
        tileH.draw(g2d);
        // Draw player
        player.draw(g2d);
        
        g2d.dispose();
    }
}