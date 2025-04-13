package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import java.awt.*;
import java.awt.image.BufferedImage;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

public class GameObject {
    BufferedImage image;
    public String name;
    public boolean Collision = false;
    public int worldX, worldY;

    public void draw(Graphics g2, gamePanel gp) {
        // Calculate screen position based on player's position
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Only draw if the object is within the camera view
        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
            worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
            worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
            worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {
            
            g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
