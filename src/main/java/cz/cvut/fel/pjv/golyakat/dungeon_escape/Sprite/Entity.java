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
    public float fadeAlpha = 1.0f;
    public int fadeCounter = 0;
    public static final int FADE_DURATION = 60;

    // Fields for monster attack
    protected int attackDamage = 1; // Базовый урон монстра
    protected int attackRange = 48; // 1 тайл
    protected int attackCooldown = 60; // 1 секунда при 60 FPS
    protected int attackCounter = 0;

    public Entity(gamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle();
    }

    public void update() {
        if (!isDead) {
            attackCounter++;
            // Проверяем, может ли монстр атаковать игрока
            if (attackCounter >= attackCooldown) {
                double distance = Math.sqrt(Math.pow(gp.player.worldX - worldX, 2) + Math.pow(gp.player.worldY - worldY, 2));
                if (distance <= attackRange) {
                    attack();
                    attackCounter = 0;
                }
            }

            // Здесь может быть логика движения монстра, если она есть
        }

        // Handle fade-out when dead
        if (isDead && fadeAlpha > 0) {
            fadeCounter++;
            fadeAlpha = 1.0f - ((float) fadeCounter / FADE_DURATION);
            if (fadeAlpha < 0) {
                fadeAlpha = 0;
            }
        }
    }

    public void attack() {
        // Монстр атакует игрока через receiveDamage
        System.out.println("DEBUG: " + name + " is attacking player with " + attackDamage + " damage.");
        gp.player.receiveDamage(attackDamage);
    }

    public void draw(Graphics2D g2d) {
        super.draw(g2d, gp);
    }
}