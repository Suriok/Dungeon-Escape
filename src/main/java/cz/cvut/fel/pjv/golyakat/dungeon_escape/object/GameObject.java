package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import java.awt.*;
import java.awt.image.BufferedImage;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

// Třída reprezentující herní objekt (např. truhla, klíč, překážka atd.)
public class GameObject {
    // Obrázky pro objekt, může jich být více pro různé animace nebo stavy objektu
    public BufferedImage image, image2, image3, image4, image5;

    public String name; // Název objektu (např. "Truhla", "Klíč")
    public boolean Collision = false; // Určuje, zda má objekt kolizní vlastnosti (hráč do něj nemůže projít)
    public int worldX, worldY; // Souřadnice objektu ve světě
    
    // Collision area
    public Rectangle solidArea;
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    public GameObject() {
        // Default collision area
        solidArea = new Rectangle(0, 0, 48, 48); // Default size of 48x48 pixels
    }

    // Metoda pro vykreslení objektu na obrazovku
    public void draw(Graphics g2, gamePanel gp) {
        // Vypočítáme pozici na obrazovce podle pozice hráče
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        // Vykreslení hlavního obrázku objektu na vypočítanou pozici s velikostí jednoho dlaždice
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
