package cz.cvut.fel.pjv.golyakat.dungeon_escape.ui;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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
        int playerInvWidth = (int) (playerInventory.getWidth() * scaleFactor);
        int playerInvHeight = (int) (playerInventory.getHeight() * scaleFactor);
        int sideArmorWidth = (int) (sideArmor.getWidth() * scaleFactor);
        int sideArmorHeight = (int) (sideArmor.getHeight() * scaleFactor);
        int weaponInvWidth = (int) (weaponInventory.getWidth() * scaleFactor);
        int weaponInvHeight = (int) (weaponInventory.getHeight() * scaleFactor);

        int playerInvX = gp.screenWidth / 2 - playerInvWidth / 2;
        int playerInvY = gp.screenHeight - playerInvHeight - 10;

        int sideX = 10;
        int startYdefBar = gp.defensBar.getY() + gp.defensBar.getBarHeight() + 15;

        // === Draw inventory background and side panels ===
        g2d.drawImage(playerInventory, playerInvX, playerInvY, playerInvWidth, playerInvHeight, null);
        g2d.drawImage(sideArmor, sideX, startYdefBar, sideArmorWidth, sideArmorHeight, null);
        g2d.drawImage(weaponInventory, sideX, startYdefBar + sideArmorHeight + 15, weaponInvWidth, weaponInvHeight, null);

        // === Draw player inventory items ===
        int columns = 8;
        int cellWidth = 55;
        int cellHeight = 55;
        int startX = 300;
        int startY = 470;


        List<ChestInventoryManager.ItemData> items = gp.player.getInventory();

        // === Loop through the first 8 items ===
        for (int i = 0; i < columns && i < items.size(); i++) {
            // === Get one item ===
            ChestInventoryManager.ItemData item = items.get(i);
            BufferedImage itemImage = item.getItem().image;

            // === If the image exists, draw it ===
            if (itemImage != null) {
                // === Рассчитываем позицию слота ===
                int slotX = startX + i * cellWidth - 30;
                int slotY = startY;

                // --- [НАЧАЛО ИЗМЕНЕНИЯ] ---
                String itemName = item.getName();
                // Проверяем, является ли это частью ключа (Key1, Key2 и т.д.), но не "Key"
                boolean isKeyPart = itemName.startsWith("Key") && itemName.length() > 3;

                if (isKeyPart) {
                    // --- Рисуем ЧАСТЬ КЛЮЧА (маленьким, по центру) ---
                    int imageWidth = itemImage.getWidth();  // Будет 48
                    int imageHeight = itemImage.getHeight(); // Будет 48

                    // Центрируем 48x48 внутри ячейки 55x55
                    int paddingX = (cellWidth - imageWidth) / 2; // (55 - 48) / 2 = 3
                    int paddingY = (cellHeight - imageHeight) / 2; // (55 - 48) / 2 = 3
                    int drawX = slotX + paddingX;
                    int drawY = slotY + paddingY;

                    g2d.drawImage(itemImage, drawX, drawY, null); // Рисуем без растягивания

                } else {
                    // --- Рисуем ОБЫЧНЫЙ ПРЕДМЕТ (растягиваем, как и раньше) ---
                    g2d.drawImage(itemImage, slotX, slotY, cellWidth, cellHeight, null);
                }
                inventoryItemBounds[i] = new Rectangle(slotX, slotY, cellWidth, cellHeight);
            }
        }

        // === Draw the player's equipped armor ===
        int armorRows = 4;
        int armorCellHeight = 50;
        int armorItemSize = 48;
        int armorStartX = 26;
        int armorStartY = 120;

        GameObject[] equippedArmor = gp.player.getEquippedArmor();

        // === Loop through all 4 armor slots ===
        for (int i = 0; i < armorRows; i++) {
            // === Calculate the Y position for this armor slot ===
            int y = armorStartY + i * armorCellHeight + (armorCellHeight - armorItemSize) / 2;

            // === Get the armor piece for this slot ===
            GameObject armor = equippedArmor[i];

            // === If there is armor and it has an image, draw it ===
            if (armor != null && armor.image != null) {
                g2d.drawImage(armor.image, armorStartX, y, armorItemSize, armorItemSize, null);
            }
        }

        // === Draw the player's equipped weapon ===
        int weaponX = 30;
        int weaponY = 360;
        int weaponSize = 70;

        // === Get the player's equipped weapon ===
        GameObject weapon = gp.player.getEquippedWeapon();

        // === If there is a weapon and it has an image, draw it ===
        if (weapon != null && weapon.image != null) {
            g2d.drawImage(weapon.image, weaponX, weaponY, weaponSize, weaponSize, null);
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
