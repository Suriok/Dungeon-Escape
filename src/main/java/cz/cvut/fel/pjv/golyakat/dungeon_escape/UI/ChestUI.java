package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * The {@code ChestUI} class is responsible for displaying and interacting with the contents of chests in the game.
 * <p>
 * It handles the graphical rendering of the chest's inventory and manages its opening or closing.
 * </p>
 */
public class ChestUI {

    /** The main game panel, from which player data, window size, etc., are obtained. */
    private gamePanel gp;

    /** The currently open chest. */
    private Object_Small_Chest activeChest;

    /** Array of bounding rectangles for items displayed in the chest. */
    private Rectangle[] itemBounds;

    /** Bounding rectangle for the entire chest inventory. */
    private Rectangle chestBounds;

    /**
     * Initializes the chest UI manager.
     *
     * @param gp instance of the main game panel
     */
    public ChestUI(gamePanel gp) {
        this.gp = gp;
        this.activeChest = null;
        this.chestBounds = null;
    }

    /**
     * Opens or closes the given chest depending on its state.
     *
     * @param chest the chest to open or close
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
     * Sets the active chest without displaying it.
     *
     * @param chest the chest to set
     */
    public void setActiveChest(Object_Small_Chest chest) {
        this.activeChest = chest;
    }

    /**
     * Indicates whether the chest's inventory screen is currently displayed.
     *
     * @return {@code true} if active and open
     */
    public boolean isShowingInventory() {
        return activeChest != null && activeChest.isShowingInventory();
    }

    /**
     * Closes the current chest inventory.
     */
    public void closeInventory() {
        if (activeChest != null) {
            activeChest.close();
            activeChest = null;
            chestBounds = null;
            gp.repaint();
        }
    }

    /**
     * Renders the chest's user interface and its contents.
     *
     * @param g2d the graphics context
     */
    public void draw(Graphics2D g2d) {
        if (!isShowingInventory() || activeChest == null) {
            return;
        }

        BufferedImage inventoryImage = activeChest.getInventoryImage();
        if (inventoryImage == null) {
            GameLogger.info("ChestUI: No image found");
            return;
        }

        float scaleFactor = 4.0f;
        int imageWidth = (int) (inventoryImage.getWidth() * scaleFactor);
        int imageHeight = (int) (inventoryImage.getHeight() * scaleFactor);
        int windowX = gp.screenWidth / 2 - imageWidth / 2;
        int windowY = gp.screenHeight / 2 - imageHeight / 2;
        int windowWidth = imageWidth + 20;
        int windowHeight = imageHeight + 20;

        chestBounds = new Rectangle(windowX - 10, windowY - 10, windowWidth, windowHeight);

        // Background and border
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(windowX - 10, windowY - 10, windowWidth, windowHeight, 25, 25);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(windowX - 10, windowY - 10, windowWidth, windowHeight, 25, 25);

        // Chest image
        g2d.drawImage(inventoryImage, windowX, windowY, imageWidth, imageHeight, null);

        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String title = "Chest Inventory";
        int titleX = windowX + windowWidth / 2 - g2d.getFontMetrics().stringWidth(title) / 2;
        int titleY = windowY - 25;
        g2d.drawString(title, titleX, titleY);

        // Items in the chest
        List<ChestInventoryManager.ItemData> items = activeChest.getItems();
        itemBounds = new Rectangle[items.size()];
        int gridSize = 4;
        int cellWidth = imageWidth / gridSize;
        int cellHeight = imageHeight / gridSize;
        int itemSize = Math.min(cellWidth, cellHeight) - 10;
        int offsetX = windowX + 5;
        int offsetY = windowY + 5;

        for (int i = 0; i < items.size(); i++) {
            int row = i / gridSize;
            int col = i % gridSize;
            ChestInventoryManager.ItemData item = items.get(i);
            BufferedImage itemImage = item.getItem().image;
            if (itemImage != null) {
                int x = offsetX + col * cellWidth + (cellWidth - itemSize) / 2;
                int y = offsetY + row * cellHeight + (cellHeight - itemSize) / 2;

                int drawSize = itemSize;
                boolean isKeyPart = item.getName().equals("Key1") ||
                        item.getName().equals("Key2") ||
                        item.getName().equals("Key3");
                if (isKeyPart) {
                    drawSize = (int)(itemSize * 0.6667f);
                    x += (itemSize - drawSize) / 2;
                    y += (itemSize - drawSize) / 2;
                }

                g2d.drawImage(itemImage, x, y, drawSize, drawSize, null);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.setColor(Color.WHITE);
                String quantityText = "x" + item.getQuantity();
                int textX = x + drawSize - g2d.getFontMetrics().stringWidth(quantityText) - 2;
                int textY = y + drawSize - 2;
                g2d.drawString(quantityText, textX, textY);

                itemBounds[i] = new Rectangle(x, y, drawSize, drawSize);
            }
        }
    }

    /**
     * Returns an array of rectangles with the positions of items in the currently open chest.
     *
     * @return an array of {@link Rectangle} corresponding to individual items
     */
    public Rectangle[] getItemBounds() {
        return itemBounds != null ? itemBounds : new Rectangle[0];
    }

    /**
     * Returns the currently active (open) chest.
     *
     * @return an instance of {@link Object_Small_Chest} or {@code null}
     */
    public Object_Small_Chest getActiveChest() {
        return activeChest;
    }

    /**
     * Returns the rectangle covering the entire chest UI.
     *
     * @return a {@link Rectangle} or {@code null} if the chest is not displayed
     */
    public Rectangle getChestBounds() {
        return chestBounds;
    }
}