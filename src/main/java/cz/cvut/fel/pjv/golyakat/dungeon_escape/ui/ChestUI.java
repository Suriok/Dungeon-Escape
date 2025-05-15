package cz.cvut.fel.pjv.golyakat.dungeon_escape.ui;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * The {@code ChestUI} class is responsible for displaying and managing
 * the chest inventory interface during gameplay.
 * <p>
 * Handles opening, rendering, item interaction, and click detection
 * within the chest inventory screen.
 * </p>
 */
public class ChestUI {

    /** Reference to the main game panel. */
    private final gamePanel gp;

    /** The currently opened chest, or {@code null} if none is open. */
    private Object_Small_Chest activeChest;

    /** Stores on-screen bounds of each item for click detection. */
    private Rectangle[] itemBounds;

    /**
     * Constructs a new ChestUI instance tied to the game panel.
     *
     * @param gp the game panel context
     */
    public ChestUI(gamePanel gp) {
        this.gp = gp;
        this.activeChest = null;
    }

    /**
     * Opens a chest and displays its inventory UI.
     * If the same chest is already open, toggles it closed.
     *
     * @param chest the chest to open
     */
    public void openChest(Object_Small_Chest chest) {
        if (activeChest == chest && isShowingInventory()) {
            closeInventory();
        } else {
            if (activeChest != null) {
                closeInventory();
            }
            activeChest = chest;
            activeChest.open();
            gp.repaint();
        }
    }

    /**
     * Checks if a chest inventory is currently being displayed.
     *
     * @return true if inventory is visible, false otherwise
     */
    public boolean isShowingInventory() {
        return activeChest != null && activeChest.isShowingInventory();
    }

    /**
     * Closes the currently opened chest inventory (if any).
     */
    public void closeInventory() {
        if (activeChest != null) {
            activeChest.close();
            activeChest = null;
            gp.repaint();
        }
    }

    /**
     * Draws the chest inventory UI on the screen.
     *
     * @param g2d the graphics context to draw on
     */
    public void draw(Graphics2D g2d) {
        // === Ensure an inventory is actually open ===
        if (!isShowingInventory() || !activeChest.isShowingInventory()) return;

        // === Get chest background image ===
        BufferedImage inventoryImage = activeChest.getInventoryImage();
        if (inventoryImage == null) {
            GameLogger.info("ChestUI: No image found");
            return;
        }

        // === Calculate scaling and position ===
        float scaleFactor = 4.0f;
        int imageWidth = (int) (inventoryImage.getWidth() * scaleFactor);
        int imageHeight = (int) (inventoryImage.getHeight() * scaleFactor);
        int windowX = gp.screenWidth / 2 - imageWidth / 2;
        int windowY = gp.screenHeight / 2 - imageHeight / 2;
        int windowWidth = imageWidth + 20;

        // === Draw inventory background ===
        g2d.drawImage(inventoryImage, windowX, windowY, imageWidth, imageHeight, null);

        // === Draw inventory title ===
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String title = "Chest Inventory";
        int titleX = windowX + windowWidth / 2 - g2d.getFontMetrics().stringWidth(title) / 2;
        int titleY = windowY - 25;
        g2d.drawString(title, titleX, titleY);

        // === Get list of items from chest ===
        List<ChestInventoryManager.ItemData> items = activeChest.getItems();
        itemBounds = new Rectangle[items.size()];

        // === Define grid layout (4x4) ===
        int cellWidth = 70;
        int cellHeight = 70;
        int offsetX = windowX + 20;
        int offsetY = windowY + 10;

        // === Render each item in the grid ===
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int index = row * 4 + col;

                if (index < items.size()) {
                    ChestInventoryManager.ItemData item = items.get(index);
                    BufferedImage itemImage = item.getItem().image;

                    if (itemImage != null) {
                        int x = offsetX + col * cellWidth;
                        int y = offsetY + row * cellHeight;

                        g2d.drawImage(itemImage, x, y, cellWidth, cellHeight, null);
                        itemBounds[index] = new Rectangle(x, y, cellWidth, cellHeight);
                    }
                }
            }
        }
    }

    /**
     * Returns the index of an item that was clicked on in the inventory grid.
     *
     * @param p the point of the mouse click
     * @return index of the clicked item, or -1 if none
     */
    public int getClickedItemIndex(Point p) {
        if (itemBounds == null) return -1;
        for (int i = 0; i < itemBounds.length; i++) {
            if (itemBounds[i] != null && itemBounds[i].contains(p)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes one unit of the specified item from the chest and returns a copy of it.
     *
     * @param idx index of the item to take
     * @return a new instance of the taken item, or null if invalid
     */
    public ChestInventoryManager.ItemData takeItem(int idx) {
        if (activeChest == null || idx < 0 || idx >= activeChest.getItems().size()) {
            return null;
        }

        ChestInventoryManager.ItemData item = activeChest.getItems().get(idx);

        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            activeChest.getItems().remove(idx);
        }

        gp.repaint();
        return new ChestInventoryManager.ItemData(item.getName(), 1);
    }
}
