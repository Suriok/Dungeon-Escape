package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject {
    public BufferedImage image, image2, image3, image4, image5;
    public String name;
    public boolean Collision = false;
    public int worldX, worldY;
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;

    public void draw(Graphics2D g2d, gamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Debug logging
        System.out.println("Drawing " + name + " at worldX: " + worldX + ", worldY: " + worldY +
                ", screenX: " + screenX + ", screenY: " + screenY + ", image: " + (image != null ? "loaded" : "null"));

        if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
            if (image != null) {
                g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            } else {
                System.err.println("Cannot draw " + name + ": image is null");
            }
        } else {
            System.out.println(name + " is out of screen bounds");
        }
    }

    public void interact() {
        // To be implemented by subclasses
    }
}