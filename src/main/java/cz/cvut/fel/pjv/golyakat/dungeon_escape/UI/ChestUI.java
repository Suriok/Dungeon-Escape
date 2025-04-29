package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ChestUI {
    private gamePanel gp;
    private Object_Small_Chest activeChest;
    private Rectangle[] itemBounds;
    private Rectangle chestBounds;

    public ChestUI(gamePanel gp) {
        this.gp = gp;
        this.activeChest = null;
        this.chestBounds = null;
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

    public void setActiveChest(Object_Small_Chest chest) {
        this.activeChest = chest;
    }

    public boolean isShowingInventory() {
        return activeChest != null && activeChest.isShowingInventory();
    }

    public void closeInventory() {
        if (activeChest != null) {
            activeChest.close();
            activeChest = null;
            chestBounds = null;
            gp.repaint();
        }
    }

    public void draw(Graphics2D g2d) {
        if (!isShowingInventory() || activeChest == null) {
            return;
        }

        BufferedImage inventoryImage = activeChest.getInventoryImage();
        if (inventoryImage == null) {
            System.out.println("ChestUI: Картинка недоступна");
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

        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(windowX - 10, windowY - 10, windowWidth, windowHeight, 25, 25);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(windowX - 10, windowY - 10, windowWidth, windowHeight, 25, 25);

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
                // Уменьшаем размер для фрагментов ключа
                int drawSize = itemSize;
                boolean isKeyPart = item.getName().equals("Key1") ||
                        item.getName().equals("Key2") ||
                        item.getName().equals("Key3");
                if (isKeyPart) {
                    drawSize = (int)(itemSize * 0.6667f); // Уменьшение на 2f (1/3)
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

    public Rectangle[] getItemBounds() {
        return itemBounds != null ? itemBounds : new Rectangle[0];
    }

    public Object_Small_Chest getActiveChest() {
        return activeChest;
    }

    public Rectangle getChestBounds() {
        return chestBounds;
    }
}