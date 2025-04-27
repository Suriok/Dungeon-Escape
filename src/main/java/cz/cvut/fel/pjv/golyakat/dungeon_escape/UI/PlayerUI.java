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
    private final float scaleFactor = 3.0f; // Масштаб инвентаря (можно настроить)
    private Rectangle playerInventoryBounds;
    private Rectangle[] armorSlotBounds; // Границы слотов для брони

    public PlayerUI(gamePanel gp) {
        this.gp = gp;
        armorSlotBounds = new Rectangle[4]; // 4 слота: шлем, нагрудник, штаны, ботинки
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

        // Размеры инвентаря игрока
        int playerInvWidth = (int)(playerInventory.getWidth() * scaleFactor);
        int playerInvHeight = (int)(playerInventory.getHeight() * scaleFactor);
        int sideArmorWidth = (int)(sideArmor.getWidth() * scaleFactor);
        int sideArmorHeight = (int)(sideArmor.getHeight() * scaleFactor);
        int weaponInvWidth = (int)(weaponInventory.getWidth() * scaleFactor);
        int weaponInvHeight = (int)(weaponInventory.getHeight() * scaleFactor);

        // Позиция инвентаря игрока (можно настроить)
        int playerInvX = gp.screenWidth/2 - playerInvWidth/2; // Настройка: позиция по X
        int playerInvY = gp.screenHeight - playerInvHeight - 10; // Настройка: позиция по Y

        // Позиция боковых панелей (выше DefensBar)
        int sideX = 10; // Настройка: позиция боковых панелей по X
        int startY = gp.defensBar.getY() + gp.defensBar.getBarHeight() + 15; // Настройка: начинаем ниже DefensBar

        // Отрисовка инвентаря и боковых панелей
        g2d.drawImage(playerInventory, playerInvX, playerInvY, playerInvWidth, playerInvHeight, null);
        g2d.drawImage(sideArmor, sideX, startY, sideArmorWidth, sideArmorHeight, null);
        g2d.drawImage(weaponInventory, sideX, startY + sideArmorHeight + 15, weaponInvWidth, weaponInvHeight, null);

        playerInventoryBounds = new Rectangle(playerInvX, playerInvY, playerInvWidth, playerInvHeight);

        // Отрисовка предметов в основном инвентаре
        int gridCols = 8; // Настройка: количество столбцов
        int gridRows = 1; // Настройка: количество строк
        int cellWidth = playerInvWidth / gridCols; // Настройка: ширина ячейки
        int cellHeight = playerInvHeight / gridRows; // Настройка: высота ячейки
        int itemSize = Math.min(cellWidth, cellHeight); // Настройка: размер предмета
        int offsetX = playerInvX + 30; // Настройка: смещение сетки по X
        int offsetY = playerInvY - 3; // Настройка: смещение сетки по Y

        // Разворачиваем предметы, чтобы каждый занимал отдельную ячейку
        List<ChestInventoryManager.ItemData> expandedItems = new ArrayList<>();
        for (ChestInventoryManager.ItemData item : gp.player.getInventory()) {
            int quantity = item.getQuantity();
            for (int i = 0; i < quantity; i++) {
                // Создаем копию ItemData с quantity = 1 для каждого предмета
                ChestInventoryManager.ItemData singleItem = new ChestInventoryManager.ItemData(item.getName(), 1);
                expandedItems.add(singleItem);
            }
        }

        for (int i = 0; i < expandedItems.size(); i++) {
            int row = i / gridCols;
            int col = i % gridCols;
            if (row >= gridRows) break;

            ChestInventoryManager.ItemData item = expandedItems.get(i);
            BufferedImage itemImage = item.getItem().image;
            if (itemImage != null) {
                int x = offsetX + col * cellWidth + (cellWidth - itemSize) / 2; // Настройка: позиция предмета по X
                int y = offsetY + row * cellHeight + (cellHeight - itemSize) / 2; // Настройка: позиция предмета по Y
                g2d.drawImage(itemImage, x, y, itemSize, itemSize, null);

                g2d.setFont(new Font("Arial", Font.PLAIN, 12)); // Настройка: размер шрифта для количества
                g2d.setColor(Color.WHITE); // Настройка: цвет текста
                String quantityText = "x" + item.getQuantity();
                int textX = x + itemSize - g2d.getFontMetrics().stringWidth(quantityText) - 2; // Настройка: позиция текста по X
                int textY = y + itemSize - 2; // Настройка: позиция текста по Y
                g2d.drawString(quantityText, textX, textY);
            }
        }

        // Отрисовка брони в side_armor (1 столбец, 4 строки)
        int armorGridCols = 1; // Настройка: количество столбцов
        int armorGridRows = 4; // Настройка: количество строк
        int armorCellWidth = sideArmorWidth / armorGridCols; // Настройка: ширина ячейки
        int armorCellHeight = sideArmorHeight / armorGridRows; // Настройка: высота ячейки
        int armorItemSize = Math.min(armorCellWidth, armorCellHeight); // Настройка: размер предмета
        int armorOffsetX = sideX + 5; // Настройка: смещение сетки по X
        int armorOffsetY = startY + 5; // Настройка: смещение сетки по Y

        GameObject[] equippedArmor = gp.player.getEquippedArmor();
        for (int i = 0; i < equippedArmor.length; i++) {
            int row = i / armorGridCols;
            int col = i % armorGridCols;

            // Определяем границы слота
            int slotX = armorOffsetX + col * armorCellWidth + (armorCellWidth - armorItemSize) / 2;
            int slotY = armorOffsetY + row * armorCellHeight + (armorCellHeight - armorItemSize) / 2;
            armorSlotBounds[i] = new Rectangle(slotX, slotY, armorItemSize, armorItemSize);

            GameObject armor = equippedArmor[i];
            if (armor != null && armor.image != null) {
                int x = slotX; // Настройка: позиция предмета по X
                int y = slotY; // Настройка: позиция предмета по Y
                g2d.drawImage(armor.image, x, y, armorItemSize, armorItemSize, null);

                // Отображаем количество (всегда 1, так как это экипированная броня)
                g2d.setFont(new Font("Arial", Font.PLAIN, 12)); // Настройка: размер шрифта для количества
                g2d.setColor(Color.WHITE); // Настройка: цвет текста
                String quantityText = "x1";
                int textX = x + armorItemSize - g2d.getFontMetrics().stringWidth(quantityText) - 2; // Настройка: позиция текста по X
                int textY = y + armorItemSize - 2; // Настройка: позиция текста по Y
                g2d.drawString(quantityText, textX, textY);
            }
        }
    }

    public Rectangle getPlayerInventoryBounds() {
        return playerInventoryBounds;
    }

    public Rectangle[] getArmorSlotBounds() {
        return armorSlotBounds;
    }

    // Проверяет, является ли предмет бронёй
    public boolean isArmor(ChestInventoryManager.ItemData item) {
        String itemName = item.getName();
        return itemName.endsWith("_helmet") || itemName.endsWith("_bib") ||
                itemName.endsWith("_pants") || itemName.endsWith("_boots");
    }

    // Возвращает индекс слота, соответствующего типу брони
    public int getArmorSlotIndex(ChestInventoryManager.ItemData item) {
        String itemName = item.getName();
        if (itemName.endsWith("_helmet")) {
            return 0; // Шлем
        } else if (itemName.endsWith("_bib")) {
            return 1; // Нагрудник
        } else if (itemName.endsWith("_pants")) {
            return 2; // Штаны
        } else if (itemName.endsWith("_boots")) {
            return 3; // Ботинки
        }
        return -1; // Не броня
    }
}