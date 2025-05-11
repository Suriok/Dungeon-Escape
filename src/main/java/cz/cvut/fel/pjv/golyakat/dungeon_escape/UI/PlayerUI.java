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

/**
 * Třída {@code PlayerUI} zajišťuje vykreslení inventáře hráče,
 * včetně zbraní, brnění a předmětů v grafickém uživatelském rozhraní.
 */
public class PlayerUI {

    /** Odkaz na hlavní herní panel, ze kterého čerpáme data o hráči. */
    private gamePanel gp;

    /** Obrázek hlavního inventáře hráče. */
    private BufferedImage playerInventory;

    /** Obrázek bočního panelu pro vybavení brnění. */
    private BufferedImage sideArmor;

    /** Obrázek panelu pro vybavenou zbraň. */
    private BufferedImage weaponInventory;

    /** Měřítko pro zvětšení UI prvků při vykreslení. */
    private final float scaleFactor = 3.0f;

    /** Hraniční obdélník inventáře pro výběr nebo kolize. */
    private Rectangle playerInventoryBounds;

    /** Hraniční oblasti jednotlivých slotů na brnění (4 sloty: helma, nátepník, kalhoty, boty). */
    private Rectangle[] armorSlotBounds;

    /** Hraniční oblast pro jeden slot vybavené zbraně. */
    private Rectangle weaponSlotBounds;

    /**
     * Inicializuje UI hráče včetně načtení grafiky.
     *
     * @param gp hlavní instance herního panelu
     */
    public PlayerUI(gamePanel gp) {
        this.gp = gp;
        armorSlotBounds = new Rectangle[4];
        weaponSlotBounds = null;
        loadImages();
    }

    /**
     * Načte všechny obrázky použité pro vykreslení inventáře.
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
            System.out.println("Error loading UI images: " + e.getMessage());
        }
    }

    /**
     * Vykreslí kompletní UI hráče včetně panelu předmětů, zbroje a zbraně.
     *
     * @param g2d grafický kontext pro vykreslování
     */
    public void draw(Graphics2D g2d) {
        if (playerInventory == null || sideArmor == null || weaponInventory == null) {
            System.out.println("PlayerUI: Some images are not loaded");
            return;
        }

        // Výpočet rozměrů a pozic všech panelů
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

        // Vykreslení tří hlavních panelů
        g2d.drawImage(playerInventory, playerInvX, playerInvY, playerInvWidth, playerInvHeight, null);
        g2d.drawImage(sideArmor, sideX, startY, sideArmorWidth, sideArmorHeight, null);
        g2d.drawImage(weaponInventory, sideX, startY + sideArmorHeight + 15, weaponInvWidth, weaponInvHeight, null);

        playerInventoryBounds = new Rectangle(playerInvX, playerInvY, playerInvWidth, playerInvHeight);

        // --- Inventář hráče ---
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

        // --- Brnění ---
        int armorGridCols = 1;
        int armorGridRows = 4;
        int armorCellWidth = sideArmorWidth / armorGridCols;
        int armorCellHeight = sideArmorHeight / armorGridRows;
        int armorItemSize = Math.min(armorCellWidth, armorCellHeight);
        int armorOffsetX = sideX + 5;
        int armorOffsetY = startY + 5;

        GameObject[] equippedArmor = gp.player.getEquippedArmor();
        for (int i = 0; i < equippedArmor.length; i++) {
            int slotX = armorOffsetX;
            int slotY = armorOffsetY + i * armorCellHeight + (armorCellHeight - armorItemSize) / 2;
            armorSlotBounds[i] = new Rectangle(slotX, slotY, armorItemSize, armorItemSize);

            GameObject armor = equippedArmor[i];
            if (armor != null && armor.image != null) {
                g2d.drawImage(armor.image, slotX, slotY, armorItemSize, armorItemSize, null);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.setColor(Color.WHITE);
                g2d.drawString("x1", slotX + armorItemSize - g2d.getFontMetrics().stringWidth("x1") - 2, slotY + armorItemSize - 2);
            }
        }

        // --- Zbraň ---
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

    /** @return hranice hlavního inventáře */
    public Rectangle getPlayerInventoryBounds() {
        return playerInventoryBounds;
    }

    /** @return pole obdélníků reprezentujících sloty brnění */
    public Rectangle[] getArmorSlotBounds() {
        return armorSlotBounds;
    }

    /** @return obdélník pro slot zbraně */
    public Rectangle getWeaponSlotBounds() {
        return weaponSlotBounds;
    }

    /**
     * Určí, zda daný předmět je součástí brnění.
     *
     * @param item položka k posouzení
     * @return {@code true}, pokud jde o brnění
     */
    public boolean isArmor(ChestInventoryManager.ItemData item) {
        String itemName = item.getName();
        return (itemName.endsWith("_helmet") || itemName.endsWith("_bib") ||
                itemName.endsWith("_pants") || itemName.endsWith("_boots")) &&
                !itemName.equals("Key1") && !itemName.equals("Key2") && !itemName.equals("Key3");
    }

    /**
     * Určí index slotu, do kterého brnění patří.
     *
     * @param item položka brnění
     * @return index slotu (0–3) nebo -1, pokud neznámé
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
     * Určí, zda daný předmět je zbraň (meč).
     *
     * @param item položka k posouzení
     * @return {@code true}, pokud je zbraň
     */
    public boolean isWeapon(ChestInventoryManager.ItemData item) {
        String itemName = item.getName();
        return itemName.endsWith("_sword") &&
                !itemName.equals("Key1") && !itemName.equals("Key2") && !itemName.equals("Key3");
    }
}
