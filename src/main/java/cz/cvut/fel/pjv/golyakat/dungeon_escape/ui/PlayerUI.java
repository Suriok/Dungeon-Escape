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

    /** Reference to the main game panel, from which player data is retrieved. */
    final private gamePanel gp;

    /** Image of the player's main inventory. */
    private BufferedImage playerInventory;

    /** Image of the side panel for equipped armor. */
    private BufferedImage sideArmor;

    /** Image of the panel for the equipped weapon. */
    private BufferedImage weaponInventory;

    /** Bounding rectangle of the inventory for selection or collision detection. */
    private Rectangle playerInventoryBounds;

    /** Bounding areas of individual armor slots (4 slots: helmet, bib, pants, boots). */
    final private Rectangle[] armorSlotBounds;

    /** Bounding area for the single equipped weapon slot. */
    private Rectangle weaponSlotBounds;

    /**
     * Initializes the player's UI, including loading graphics.
     *
     * @param gp the main instance of the game panel
     */
    public PlayerUI(gamePanel gp) {
        this.gp = gp;
        armorSlotBounds = new Rectangle[4];
        weaponSlotBounds = null;
        loadImages();
    }

    /**
     * Loads all images used for rendering the inventory.
     */
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

    /**
     * Renders the complete player UI, including the item panel, armor, and weapon.
     *
     * @param g2d the graphics context for rendering
     */
    public void draw(Graphics2D g2d) {
        if (playerInventory == null || sideArmor == null || weaponInventory == null) {
            GameLogger.info("PlayerUI: Some images are not loaded");
            return;
        }

        // Calculate dimensions and positions of all panels
        /** Scale factor for enlarging UI elements during rendering. */
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

        // Render the three main panels
        g2d.drawImage(playerInventory, playerInvX, playerInvY, playerInvWidth, playerInvHeight, null);
        g2d.drawImage(sideArmor, sideX, startY, sideArmorWidth, sideArmorHeight, null);
        g2d.drawImage(weaponInventory, sideX, startY + sideArmorHeight + 15, weaponInvWidth, weaponInvHeight, null);

        playerInventoryBounds = new Rectangle(playerInvX, playerInvY, playerInvWidth, playerInvHeight);

        // --- Player Inventory ---
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
                int y = offsetY + row * cellHeight + (cellHeight - itemSize) / 2;

                int drawSize = itemSize;
                if (item.getName().equals("Key1") || item.getName().equals("Key2") || item.getName().equals("Key3")) {
                    drawSize = (int)(itemSize * 0.6667f);
                    x += (itemSize - drawSize) / 2;
                    y += (itemSize - drawSize) / 2;
                }

                g2d.drawImage(itemImage, x, y, drawSize, drawSize, null);

                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.setColor(Color.WHITE);
                String quantityText = "x" + item.getQuantity();
                g2d.drawString(quantityText, x + drawSize - g2d.getFontMetrics().stringWidth(quantityText) - 2, y + drawSize - 2);
            }
        }

        // --- Armor ---
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
            armorSlotBounds[i] = new Rectangle(armorOffsetX, slotY, armorItemSize, armorItemSize);

            GameObject armor = equippedArmor[i];
            if (armor != null && armor.image != null) {
                g2d.drawImage(armor.image, armorOffsetX, slotY, armorItemSize, armorItemSize, null);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.setColor(Color.WHITE);
                g2d.drawString("x1", armorOffsetX + armorItemSize - g2d.getFontMetrics().stringWidth("x1") - 2, slotY + armorItemSize - 2);
            }
        }

        // --- Weapon ---
        int weaponItemSize = Math.min(weaponInvWidth, weaponInvHeight);
        int weaponX = sideX + 5;
        int weaponY = startY + sideArmorHeight + 20;
        weaponSlotBounds = new Rectangle(weaponX, weaponY, weaponItemSize, weaponItemSize);

        GameObject weapon = gp.player.getEquippedWeapon();
        if (weapon != null && weapon.image != null) {
            g2d.drawImage(weapon.image, weaponX, weaponY, weaponItemSize, weaponItemSize, null);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.setColor(Color.WHITE);
            g2d.drawString("x1", weaponX + weaponItemSize - g2d.getFontMetrics().stringWidth("x1") - 2, weaponY + weaponItemSize - 2);
        }
    }

    /** @return the bounds of the main inventory */
    public Rectangle getPlayerInventoryBounds() {
        return playerInventoryBounds;
    }

    /** @return an array of rectangles representing the armor slots */
    public Rectangle[] getArmorSlotBounds() {
        return armorSlotBounds;
    }

    /** @return the rectangle for the weapon slot */
    public Rectangle getWeaponSlotBounds() {
        return weaponSlotBounds;
    }

    /**
     * Determines whether the given item is part of armor.
     *
     * @param item the item to evaluate
     * @return {@code true} if it is armor
     */
    public boolean isArmor(ChestInventoryManager.ItemData item) {
        String itemName = item.getName();
        return (itemName.endsWith("_helmet") || itemName.endsWith("_bib") ||
                itemName.endsWith("_pants") || itemName.endsWith("_boots")) &&
                !itemName.equals("Key1") && !itemName.equals("Key2") && !itemName.equals("Key3");
    }

    /**
     * Determines the slot index for the given armor item.
     *
     * @param item the armor item
     * @return the slot index (0–3) or -1 if unknown
     */
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

    /**
     * Determines whether the given item is a weapon (sword).
     *
     * @param item the item to evaluate
     * @return {@code true} if it is a weapon
     */
    public boolean isWeapon(ChestInventoryManager.ItemData item) {
        String itemName = item.getName();
        return itemName.endsWith("_sword") &&
                !itemName.equals("Key1") && !itemName.equals("Key2") && !itemName.equals("Key3");
    }
}