package cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity extends GameObject {
    gamePanel gp;

    public int speed;
    public BufferedImage up1, up2, down1, down2, right1, right2, left1, left2;
    public String direction = "down";
    public int spriteCounter = 0;
    public int spriteNum = 1;
    public Rectangle solidArea;
    public int solidAreaDefaultX, solidAreaDefaultY;
    public boolean collisionOn = false;
    public int maxLife;
    public int life;

    // Fields for fade effect and death state
    public boolean isDead = false;
    public float fadeAlpha = 1.0f; // Opacity for fade effect (1.0 = fully visible, 0.0 = fully transparent)
    public int fadeCounter = 0;
    public static final int FADE_DURATION = 60; // Frames to fade out (e.g., 1 second at 60 FPS)

    public Entity(gamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle();
    }

    public void update() {
        // Handle fade-out when dead
        if (isDead && fadeAlpha > 0) {
            fadeCounter++;
            fadeAlpha = 1.0f - ((float) fadeCounter / FADE_DURATION);
            if (fadeAlpha < 0) {
                fadeAlpha = 0;
            }
        }
    }

    public void draw(Graphics2D g2d) {
        super.draw(g2d, gp);
    }
}