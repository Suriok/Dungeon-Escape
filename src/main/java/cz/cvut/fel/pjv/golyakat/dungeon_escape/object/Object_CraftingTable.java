package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Object_CraftingTable extends GameObject {

    public Object_CraftingTable() {
        name = "CraftingTable";
        Collision = true;
        try {
            // Attempt to load the crafting table image
            image = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/crafting_tabel.png"));
            if (image == null) {
                System.err.println("Failed to load crafting_tabel.png for Object_CraftingTable");
                // Create a fallback image (blue square)
                image = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = image.createGraphics();
                g2d.setColor(Color.BLUE);
                g2d.fillRect(0, 0, 48, 48);
                g2d.dispose();
            }
        } catch (Exception e) {
            System.err.println("Error loading CraftingTable image: " + e.getMessage());
            e.printStackTrace();
            // Create a fallback image (blue square)
            image = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.BLUE);
            g2d.fillRect(0, 0, 48, 48);
            g2d.dispose();
        }
    }

    @Override
    public void draw(Graphics2D g2d, gamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Only draw if within screen bounds
        if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
            if (image != null) {
                g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            } else {
                // Fallback rendering if image is null
                g2d.setColor(Color.RED);
                g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString("CraftingTable", screenX + 5, screenY + gp.tileSize / 2);
                System.err.println("Cannot draw CraftingTable: image is null");
            }
        }
    }
}