package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The {@code Object_CraftingTable} class represents a crafting table object,
 * which is used to create new items from materials in the inventory.
 * <p>
 * Contains the table's image and logic for rendering on the map.
 * </p>
 */
public class Object_CraftingTable extends GameObject {

    /**
     * Creates a new instance of the crafting table object.
     * <p>
     * Attempts to load the table's image from a file.
     * If unsuccessful, creates a fallback image (blue square).
     * </p>
     */
    public Object_CraftingTable() {
        name = "CraftingTable";
        Collision = true;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/crafting_tabel.png"));

            if (image == null) {
                GameLogger.error("Nepodařilo se načíst crafting_tabel.png pro Object_CraftingTable");
                image = createFallbackImage();
            }

        } catch (Exception e) {
            GameLogger.error("Chyba při načítání obrázku CraftingTable: " + e.getMessage());
            e.printStackTrace();
            image = createFallbackImage();
        }
    }

    /**
     * Helper method to create a fallback image if the original fails to load.
     *
     * @return blue square as a replacement image
     */
    private BufferedImage createFallbackImage() {
        BufferedImage fallback = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = fallback.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 48, 48);
        g2d.dispose();
        return fallback;
    }

    /**
     * Renders the crafting table on the screen if it's within the visible range relative to the player.
     *
     * @param g2d graphics context
     * @param gp  main game panel from which player and display information is taken
     */
    @Override
    public void draw(Graphics2D g2d, gamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Render only if visible on screen
        if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {

            if (image != null) {
                g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            } else {
                // Emergency rendering if image is missing
                g2d.setColor(Color.RED);
                g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString("CraftingTable", screenX + 5, screenY + gp.tileSize / 2);
                GameLogger.error("Nelze vykreslit CraftingTable: obrázek je null");
            }
        }
    }
}
