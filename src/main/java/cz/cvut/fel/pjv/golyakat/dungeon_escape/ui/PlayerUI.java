package cz.cvut.fel.pjv.golyakat.dungeon_escape.ui;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The {@code PlayerUI} class handles the rendering of the player's inventory,
 * including weapons, armor, and items in the graphical user interface.
 */
public class PlayerUI {

    private final gamePanel gp;
    private BufferedImage playerInventory;
    private BufferedImage sideArmor;
    private BufferedImage weaponInventory;
    final private Rectangle[] inventoryItemBounds;

    public PlayerUI(gamePanel gp) {
        this.gp = gp;
        inventoryItemBounds = new Rectangle[8];
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
            GameLogger.info("Error loading UI images: " + e.getMessage());
        }
    }

    public void draw(Graphics2D g2d) {
        if (playerInventory == null || sideArmor == null || weaponInventory == null) {
            GameLogger.info("PlayerUI: Some images are not loaded");
            return;
        }

        float scaleFactor = 3.0f;
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


        // Player Inventory
        int gridCols = 8;
        int gridRows = 1;
        int cellWidth = playerInvWidth / gridCols;
        int cellHeight = playerInvHeight / gridRows;
        int itemSize = Math.min(cellWidth, cellHeight);
        int offsetX = playerInvX + 30;
        int offsetY = playerInvY - 3;

        List<ChestInventoryManager.ItemData> expandedItems = new ArrayList<>();
        for (ChestInventoryManager.ItemData item : gp.player.getInventory()) {
            for (int i = 0; i < item.getQuantity(); i++) {
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
                int y = offsetY + cellHeight + (cellHeight - itemSize) / 2;

                int drawSize = itemSize;
                if (item.getName().equals("Key1") || item.getName().equals("Key2") || item.getName().equals("Key3")) {
                    drawSize = (int)(itemSize * 0.6667f);
                    x += (itemSize - drawSize) / 2;
                    y += (itemSize - drawSize) / 2;
                }

                g2d.drawImage(itemImage, x, y, drawSize, drawSize, null);
                inventoryItemBounds[i] = new Rectangle(x, y, drawSize, drawSize);
            }
        }

        // Armor
        int armorGridCols = 1;
        int armorGridRows = 4;
        int armorCellWidth = sideArmorWidth / armorGridCols;
        int armorCellHeight = sideArmorHeight / armorGridRows;
        int armorItemSize = Math.min(armorCellWidth, armorCellHeight);
        int armorOffsetX = sideX + 5;
        int armorOffsetY = startY + 5;

        GameObject[] equippedArmor = gp.player.getEquippedArmor();
        for (int i = 0; i < equippedArmor.length; i++) {
            int slotY = armorOffsetY + i * armorCellHeight + (armorCellHeight - armorItemSize) / 2;

            GameObject armor = equippedArmor[i];
            if (armor != null && armor.image != null) {
                g2d.drawImage(armor.image, armorOffsetX, slotY, armorItemSize, armorItemSize, null);
            }
        }

        // Weapon
        int weaponItemSize = Math.min(weaponInvWidth, weaponInvHeight);
        int weaponX = sideX + 5;
        int weaponY = startY + sideArmorHeight + 20;

        GameObject weapon = gp.player.getEquippedWeapon();
        if (weapon != null && weapon.image != null) {
            g2d.drawImage(weapon.image, weaponX, weaponY, weaponItemSize, weaponItemSize, null);
        }
    }

    public int getClickedInventoryIndex(Point p) {
        for (int i = 0; i < inventoryItemBounds.length; i++) {
            if (inventoryItemBounds[i] != null && inventoryItemBounds[i].contains(p)) {
                return i;
            }
        }
        return -1;
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
}