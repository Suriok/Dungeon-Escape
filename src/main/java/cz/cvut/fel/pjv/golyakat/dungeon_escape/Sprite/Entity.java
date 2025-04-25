package cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import java.awt.*;
import java.awt.image.BufferedImage;

// Abstraktní třída Entity — základní třída pro všechny pohyblivé entity ve hře (např. hráč, příšery)
public class Entity extends GameObject {
    gamePanel gp; // Odkaz na herní panel

    public int speed; // Rychlost pohybu entity

    // Obrázky pro různé směry a fáze animace (pohybu)
    public BufferedImage up1, up2, down1, down2, right1, right2, left1, left2;

    public String direction = "down"; // Výchozí směr entity

    // Počítadla pro animaci sprite
    public int spriteCounter = 0;
    public int spriteNum = 1; // Určuje, který obrázek animace se má použít

    public Rectangle solidArea; // Hitbox entity pro detekci kolizí
    public int solidAreaDefaultX, solidAreaDefaultY; // Výchozí pozice hitboxu
    public boolean collisionOn = false; // Flag, zda entita narazila do překážky

    // Stav entity (společné pro hráče i monstra)
    public int maxLife; // Maximální život entity
    public int life; // Aktuální život entity

    // Konstruktor entity
    public Entity(gamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle(); // Inicializace kolizní oblasti (hitboxu)
    }

    // Metoda pro aktualizaci entity (bude přepsána v potomcích — hráč, příšery atd.)
    public void update() {
        // Základní metoda bez obsahu — potomci si ji implementují podle potřeby
    }

    // Metoda pro vykreslení entity na obrazovku
    public void draw(Graphics2D g2d) {
        // Voláme metodu draw ze základní třídy GameObject
        super.draw(g2d, gp);
    }
}