package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerUI {
    private gamePanel gp;
    private BufferedImage playerInventory;
    private BufferedImage sideArmor;
    private BufferedImage weaponInventory;
    private final float scaleFactor = 3.0f;
    private Rectangle playerInventoryBounds;
    private Rectangle[] armorSlotBounds;
    private Rectangle weaponSlotBounds;

    public PlayerUI(gamePanel gp) {
        this.gp = gp;
        armorSlotBounds = new Rectangle[4];
        weaponSlotBounds = null;
        loadImages();
    }

    private void loadImages() {
        try {
            playerInventory = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/player_inven.png")));
            sideArmor = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/side_armor.png")));
            weaponInventory = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/weapon_inv.png")));
        } catch (Exception e) {
            System.out.println("Error loading UI images: " + e.getMessage());
        }
    }

    public void draw(Graphics2D g2d) {
        if (playerInventory == null || sideArmor == null || weaponInventory == null) {
            System.out.println("PlayerUI: Some images are not loaded");
            return;
        }

        int playerInvWidth = (int)(playerInventory.getWidth() * scaleFactor);
        int playerInvHeight = (int)(playerInventory.getHeight() * scaleFactor);
        int sideArmorWidth = (int)(sideArmor.getWidth() * scaleFactor);
        int sideArmorHeight = (int)(sideArmor.getHeight() * scaleFactor);
        int weaponInvWidth = (int)(weaponInventory.getWidth() * scaleFactor);
        int weaponInvHeight = (int)(weaponInventory.getHeight() * scaleFactor);

        int playerInvX = gp.screenWidth/2 - playerInvWidth/2;
        int playerInvY = gp.screenHeight - playerInvHeight - 10;

        int sideX = 10;
        int startY = gp.defensBar.getY() + gp.defensBar.getBarHeight() + 15;

        g2d.drawImage(playerInventory, playerInvX, playerInvY, playerInvWidth, playerInvHeight, null);
        g2d.drawImage(sideArmor, sideX, startY, sideArmorWidth, sideArmorHeight, null);
        g2d.drawImage(weaponInventory, sideX, startY + sideArmorHeight + 15, weaponInvWidth, weaponInvHeight, null);

        playerInventoryBounds = new Rectangle(playerInvX, playerInvY, playerInvWidth, playerInvHeight);

        // Отрисовка предметов в основном инвентаре
        int gridCols = 8;
        int gridRows = 1;
        int cellWidth = playerInvWidth / gridCols;
        int cellHeight = playerInvHeight / gridRows;
        int itemSize = Math.min(cellWidth, cellHeight);
        int offsetX = playerInvX + 30;
        int offsetY = playerInvY - 3;

        List<ChestInventoryManager.ItemData> expandedItems = new ArrayList<>();
        for (ChestInventoryManager.ItemData item : gp.player.getInventory()) {
            int quantity = item.getQuantity();
            for (int i = 0; i < quantity; i++) {
                expandedItems.add(new ChestInventoryManager.ItemData(item.getName(), 1));
            }
        }

        for (int i = 0; i < expandedItems.size(); i++) {
            int row = i / gridCols;
            int col = i % gridCols;
            if (row >= gridRows) break;

            ChestInventoryManager.ItemData item = expandedItems.get(i);
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
            }
        }

        // Отрисовка брони в side_armor
        int armorGridCols = 1;
        int armorGridRows = 4;
        int armorCellWidth = sideArmorWidth / armorGridCols;
        int armorCellHeight = sideArmorHeight / armorGridRows;
        int armorItemSize = Math.min(armorCellWidth, armorCellHeight);
        int armorOffsetX = sideX + 5;
        int armorOffsetY = startY + 5;

        GameObject[] equippedArmor = gp.player.getEquippedArmor();
        for (int i = 0; i < equippedArmor.length; i++) {
            int row = i / armorGridCols;
            int col = i % armorGridCols;

            int slotX = armorOffsetX + col * armorCellWidth + (armorCellWidth - armorItemSize) / 2;
            int slotY = armorOffsetY + row * armorCellHeight + (armorCellHeight - armorItemSize) / 2;
            armorSlotBounds[i] = new Rectangle(slotX, slotY, armorItemSize, armorItemSize);

            GameObject armor = equippedArmor[i];
            if (armor != null && armor.image != null) {
                int x = slotX;
                int y = slotY;
                g2d.drawImage(armor.image, x, y, armorItemSize, armorItemSize, null);

                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.setColor(Color.WHITE);
                String quantityText = "x1";
                int textX = x + armorItemSize - g2d.getFontMetrics().stringWidth(quantityText) - 2;
                int textY = y + armorItemSize - 2;
                g2d.drawString(quantityText, textX, textY);
            }
        }

        // Отрисовка оружия в weapon_inv
        int weaponGridCols = 1;
        int weaponGridRows = 1;
        int weaponCellWidth = weaponInvWidth / weaponGridCols;
        int weaponCellHeight = weaponInvHeight / weaponGridRows;
        int weaponItemSize = Math.min(weaponCellWidth, weaponCellHeight);
        int weaponOffsetX = sideX + 5;
        int weaponOffsetY = startY + sideArmorHeight + 15 + 5;

        int weaponSlotX = weaponOffsetX;
        int weaponSlotY = weaponOffsetY;
        weaponSlotBounds = new Rectangle(weaponSlotX, weaponSlotY, weaponItemSize, weaponItemSize);

        GameObject weapon = gp.player.getEquippedWeapon();
        if (weapon != null && weapon.image != null) {
            int x = weaponSlotX;
            int y = weaponSlotY;
            g2d.drawImage(weapon.image, x, y, weaponItemSize, weaponItemSize, null);

            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.setColor(Color.WHITE);
            String quantityText = "x1";
            int textX = x + weaponItemSize - g2d.getFontMetrics().stringWidth(quantityText) - 2;
            int textY = y + weaponItemSize - 2;
            g2d.drawString(quantityText, textX, textY);
        }
    }

    public Rectangle getPlayerInventoryBounds() {
        return playerInventoryBounds;
    }

    public Rectangle[] getArmorSlotBounds() {
        return armorSlotBounds;
    }

    public Rectangle getWeaponSlotBounds() {
        return weaponSlotBounds;
    }

    public boolean isArmor(ChestInventoryManager.ItemData item) {
        String itemName = item.getName();
        return (itemName.endsWith("_helmet") || itemName.endsWith("_bib") ||
                itemName.endsWith("_pants") || itemName.endsWith("_boots")) &&
                !itemName.equals("Key1") && !itemName.equals("Key2") && !itemName.equals("Key3");
    }

    public int getArmorSlotIndex(ChestInventoryManager.ItemData item) {
        String itemName = item.getName();
        if (itemName.endsWith("_helmet")) {
            return 0;
        } else if (itemName.endsWith("_bib")) {
            return 1;
        } else if (itemName.endsWith("_pants")) {
            return 2;
        } else if (itemName.endsWith("_boots")) {
            return 3;
        }
        return -1;
    }

    public boolean isWeapon(ChestInventoryManager.ItemData item) {
        String itemName = item.getName();
        return itemName.endsWith("_sword") &&
                !itemName.equals("Key1") && !itemName.equals("Key2") && !itemName.equals("Key3");
    }
}