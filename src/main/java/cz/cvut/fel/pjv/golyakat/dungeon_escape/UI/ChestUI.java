package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

public class ChestUI {
    private gamePanel gp;
    private Object_Small_Chest activeChest;
    private ChestInventoryManager.ItemData draggedItem;
    private int draggedX, draggedY;
    private Rectangle[] itemBounds;

    public ChestUI(gamePanel gp) {
        this.gp = gp;
        this.activeChest = null;
        this.draggedItem = null;

        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isShowingInventory() || activeChest == null) return;

                for (int i = 0; i < itemBounds.length; i++) {
                    if (itemBounds[i] != null && itemBounds[i].contains(e.getPoint())) {
                        draggedItem = activeChest.getItems().get(i);
                        draggedX = e.getX();
                        draggedY = e.getY();
                        System.out.println("DEBUG: Picked up item: " + draggedItem.getName());
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggedItem != null) {
                    Rectangle playerInvBounds = gp.playerUI.getPlayerInventoryBounds();
                    Rectangle[] armorSlotBounds = gp.playerUI.getArmorSlotBounds();
                    Rectangle weaponSlotBounds = gp.playerUI.getWeaponSlotBounds();

                    boolean isArmor = gp.playerUI.isArmor(draggedItem);
                    int armorSlotIndex = gp.playerUI.getArmorSlotIndex(draggedItem);
                    boolean isWeapon = gp.playerUI.isWeapon(draggedItem);

                    System.out.println("DEBUG: Dropped item: " + draggedItem.getName() + ", isArmor=" + isArmor + ", armorSlotIndex=" + armorSlotIndex + ", isWeapon=" + isWeapon);

                    if (isArmor && armorSlotIndex >= 0) {
                        Rectangle targetSlotBounds = armorSlotBounds[armorSlotIndex];
                        if (targetSlotBounds != null && targetSlotBounds.contains(e.getPoint())) {
                            GameObject[] equippedArmor = gp.player.getEquippedArmor();
                            if (equippedArmor[armorSlotIndex] != null) {
                                activeChest.getItems().add(new ChestInventoryManager.ItemData(
                                        equippedArmor[armorSlotIndex].name, 1));
                                System.out.println("DEBUG: Swapped armor, added " + equippedArmor[armorSlotIndex].name + " back to chest");
                            }
                            gp.player.equipArmor(draggedItem.getItem(), armorSlotIndex);
                            activeChest.removeItem(draggedItem);
                            System.out.println("DEBUG: Equipped " + draggedItem.getName() + " to slot " + armorSlotIndex);
                        }
                    } else if (isWeapon && weaponSlotBounds != null && weaponSlotBounds.contains(e.getPoint())) {
                        GameObject equippedWeapon = gp.player.getEquippedWeapon();
                        if (equippedWeapon != null) {
                            activeChest.getItems().add(new ChestInventoryManager.ItemData(
                                    equippedWeapon.name, 1));
                            System.out.println("DEBUG: Swapped weapon, added " + equippedWeapon.name + " back to chest");
                        }
                        gp.player.equipWeapon(draggedItem.getItem());
                        activeChest.removeItem(draggedItem);
                        System.out.println("DEBUG: Equipped weapon: " + draggedItem.getName());
                    } else if (playerInvBounds != null && playerInvBounds.contains(e.getPoint())) {
                        gp.player.addItem(draggedItem);
                        activeChest.removeItem(draggedItem);
                        System.out.println("DEBUG: Moved " + draggedItem.getName() + " to player inventory");
                    }

                    draggedItem = null;
                    gp.repaint();
                }
            }
        });

        gp.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedItem != null) {
                    draggedX = e.getX();
                    draggedY = e.getY();
                    gp.repaint();
                }
            }
        });
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
            System.out.println("ChestUI: Сундук открыт, isShowingInventory: " + isShowingInventory());
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
            System.out.println("ChestUI: Сундук закрыт, isShowingInventory: " + isShowingInventory());
            activeChest = null;
            gp.repaint();
        }
    }

    public void draw(Graphics2D g2d) {
        if (!isShowingInventory() || activeChest == null) {
            System.out.println("ChestUI: Не отображаем UI, isShowingInventory: " + isShowingInventory());
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
            System.out.println("Drawing item: " + item.getName() + ", image=" + (itemImage != null ? "present" : "null"));
            if (itemImage != null) {
                int x = offsetX + col * cellWidth + (cellWidth - itemSize) / 2;
                int y = offsetY + row * cellHeight + (cellHeight - itemSize) / 2;
                g2d.drawImage(itemImage, x, y, itemSize, itemSize, null);

                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.setColor(Color.WHITE);
                String quantityText = "x" + item.getQuantity();
                int textX = x + itemSize - g2d.getFontMetrics().stringWidth(quantityText) - 2;
                int textY = y + itemSize - 2;
                g2d.drawString(quantityText, textX, textY);

                itemBounds[i] = new Rectangle(x, y, itemSize, itemSize);
            }
        }

        if (draggedItem != null) {
            BufferedImage draggedImage = draggedItem.getItem().image;
            if (draggedImage != null) {
                g2d.drawImage(draggedImage, draggedX - itemSize / 2, draggedY - itemSize / 2, itemSize, itemSize, null);
            }
        }
    }
}