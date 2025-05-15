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
 * The {@code PlayerUI} class handles the graphical user interface
 * related to the player's inventory, equipped armor, and weapon.
 * <p>
 * It displays items visually and provides utility methods
 * for detecting item clicks and categorizing armor slots.
 * </p>
 */
public class PlayerUI {

    /** Reference to the main game panel. */
    private final gamePanel gp;

    /** Background image for the inventory grid. */
    private BufferedImage playerInventory;

    /** Background image for the equipped armor display. */
    private BufferedImage sideArmor;

    /** Background image for the weapon slot. */
    private BufferedImage weaponInventory;

    /** Screen bounding boxes for inventory items (used for click detection). */
    private final Rectangle[] inventoryItemBounds;

    /**
     * Constructs a new {@code PlayerUI} instance tied to a {@link gamePanel}.
     *
     * @param gp the game panel reference
     */
    public PlayerUI(gamePanel gp) {
        this.gp = gp;
        inventoryItemBounds = new Rectangle[8];
        loadImages();
    }

    // === Loads UI background images ===
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
     * Draws the player's inventory, equipped armor, and weapon to the screen.
     *
     * @param g2d the graphics context used for rendering
     */
    public void draw(Graphics2D g2d) {
        // === Check if UI components are loaded ===
        if (playerInventory == null || sideArmor == null || weaponInventory == null) {
            GameLogger.info("PlayerUI: Some images are not loaded");
            return;
        }

        // === Calculate positions and sizes ===
        float scaleFactor = 3.0f;
        int playerInvWidth = (int)(playerInventory.getWidth() * scaleFactor);
        int playerInvHeight = (int)(playerInventory.getHeight() * scaleFactor);
        int sideArmorWidth = (int)(sideArmor.getWidth() * scaleFactor);
        int sideArmorHeight = (int)(sideArmor.getHeight() * scaleFactor);
        int weaponInvWidth = (int)(weaponInventory.getWidth() * scaleFactor);
        int weaponInvHeight = (int)(weaponInventory.getHeight() * scaleFactor);

        int playerInvX = gp.screenWidth / 2 - playerInvWidth / 2;
        int playerInvY = gp.screenHeight - playerInvHeight - 10;

        int sideX = 10;
        int startY = gp.defensBar.getY() + gp.defensBar.getBarHeight() + 15;

        // === Draw inventory background and side panels ===
        g2d.drawImage(playerInventory, playerInvX, playerInvY, playerInvWidth, playerInvHeight, null);
        g2d.drawImage(sideArmor, sideX, startY, sideArmorWidth, sideArmorHeight, null);
        g2d.drawImage(weaponInventory, sideX, startY + sideArmorHeight + 15, weaponInvWidth, weaponInvHeight, null);

        // === Draw player inventory items ===
        int gridCols = 8;
        int gridRows = 1;
        int cellWidth = playerInvWidth / gridCols;
        int cellHeight = playerInvHeight / gridRows;
        int itemSize = Math.min(cellWidth, cellHeight);
        int offsetX = playerInvX + 30;
        int offsetY = playerInvY + 5;

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
                int y = offsetY + (cellHeight - itemSize) / 2;

                int drawSize = itemSize;

                // === Resize key fragments to be smaller ===
                if (item.getName().equals("Key1") || item.getName().equals("Key2") || item.getName().equals("Key3")) {
                    drawSize = (int)(itemSize * 0.6667f);
                    x += (itemSize - drawSize) / 2;
                    y += (itemSize - drawSize) / 2;
                }

                g2d.drawImage(itemImage, x, y, drawSize, drawSize, null);
                inventoryItemBounds[i] = new Rectangle(x, y, drawSize, drawSize);
            }
        }

        // === Draw equipped armor ===
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

        // === Draw equipped weapon ===
        int weaponItemSize = Math.min(weaponInvWidth, weaponInvHeight);
        int weaponX = sideX + 5;
        int weaponY = startY + sideArmorHeight + 20;

        GameObject weapon = gp.player.getEquippedWeapon();
        if (weapon != null && weapon.image != null) {
            g2d.drawImage(weapon.image, weaponX, weaponY, weaponItemSize, weaponItemSize, null);
        }
    }

    /**
     * Returns the index of the inventory item that was clicked.
     *
     * @param p screen point of the mouse click
     * @return item index, or -1 if none was clicked
     */
    public int getClickedInventoryIndex(Point p) {
        for (int i = 0; i < inventoryItemBounds.length; i++) {
            if (inventoryItemBounds[i] != null && inventoryItemBounds[i].contains(p)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determines which armor slot corresponds to a given item.
     *
     * @param item item to evaluate
     * @return index of armor slot (0–3), or -1 if not armor
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
}
