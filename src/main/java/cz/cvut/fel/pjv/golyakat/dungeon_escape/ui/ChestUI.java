package cz.cvut.fel.pjv.golyakat.dungeon_escape.ui;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * The {@code ChestUI} class is responsible for displaying and interacting with the contents of chests in the game.
 */
public class ChestUI {

    private final gamePanel gp;
    private Object_Small_Chest activeChest;
    private Rectangle[] itemBounds;

    public ChestUI(gamePanel gp) {
        this.gp = gp;
        this.activeChest = null;
    }

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

    public boolean isShowingInventory() {
        return activeChest != null && activeChest.isShowingInventory();
    }

    public void closeInventory() {
        if (activeChest != null) {
            activeChest.close();
            activeChest = null;
            gp.repaint();
        }
    }

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


        g2d.drawImage(inventoryImage, windowX, windowY, imageWidth, imageHeight, null);

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String title = "Chest Inventory";
        int titleX = windowX + windowWidth / 2 - g2d.getFontMetrics().stringWidth(title) / 2;
        int titleY = windowY - 25;
        g2d.drawString(title, titleX, titleY);

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
                itemBounds[i] = new Rectangle(x, y, drawSize, drawSize);
            }
        }
    }

    public int getClickedItemIndex(Point p) {
        if (itemBounds == null) return -1;
        for (int i = 0; i < itemBounds.length; i++) {
            if (itemBounds[i] != null && itemBounds[i].contains(p)) {
                return i;
            }
        }
        return -1;
    }

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