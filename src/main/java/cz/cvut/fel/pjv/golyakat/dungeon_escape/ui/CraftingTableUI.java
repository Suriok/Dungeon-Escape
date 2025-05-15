package cz.cvut.fel.pjv.golyakat.dungeon_escape.ui;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import java.awt.*;
import java.util.Arrays;

/**
 * The {@code CraftingTableUI} class represents the graphical interface
 * for crafting items from components (e.g., crafting a SilverKey from Key1–3).
 * <p>
 * It handles drawing the interface, processing the crafting recipe,
 * and managing interaction with crafting slots.
 * </p>
 */
public class CraftingTableUI {

    /** Reference to the main game panel. */
    private final gamePanel gp;

    /** Whether the crafting UI is currently shown. */
    private boolean isShowing;

    /** Slots for crafting ingredients (e.g., 3 keys). */
    private final ChestInventoryManager.ItemData[] craftingSlots;

    /** Screen bounds of each crafting slot (used for mouse interaction). */
    private final Rectangle[] slotBounds;

    /** Bounding rectangle of the "Craft" button. */
    private Rectangle craftButtonBounds;

    /**
     * Constructs a new crafting table UI instance.
     *
     * @param gp reference to the main game panel
     */
    public CraftingTableUI(gamePanel gp) {
        this.gp = gp;
        this.isShowing = false;
        this.craftingSlots = new ChestInventoryManager.ItemData[3];
        this.slotBounds = new Rectangle[3];
        this.craftButtonBounds = null;
    }

    /**
     * Opens the crafting interface and logs the event.
     */
    public void open() {
        isShowing = true;
        GameLogger.info("CraftingTableUI: Opened");
    }

    /**
     * Closes the crafting interface and logs the event.
     */
    public void close() {
        isShowing = false;
        GameLogger.info("CraftingTableUI: Closed");
    }

    /**
     * Checks if the crafting UI is currently displayed.
     *
     * @return {@code true} if crafting UI is visible, {@code false} otherwise
     */
    public boolean isShowing() {
        return isShowing;
    }

    /**
     * Returns the bounds of the "Craft" button.
     *
     * @return button bounding rectangle
     */
    public Rectangle getCraftButtonBounds() {
        return craftButtonBounds;
    }

    /**
     * Attempts to craft a SilverKey from Key1, Key2, and Key3 if all are present.
     * <p>
     * Removes the keys from both the crafting UI and player inventory.
     * </p>
     */
    public void craftSilverKey() {
        boolean hasKey1 = false, hasKey2 = false, hasKey3 = false;

        // === Check for required components ===
        for (ChestInventoryManager.ItemData slot : craftingSlots) {
            if (slot == null) continue;
            switch (slot.getName()) {
                case "Key1" -> hasKey1 = true;
                case "Key2" -> hasKey2 = true;
                case "Key3" -> hasKey3 = true;
            }
        }

        if (!(hasKey1 && hasKey2 && hasKey3)) {
            gp.repaint();
            return;
        }

        // === Add SilverKey to inventory ===
        gp.player.addItem(new ChestInventoryManager.ItemData("SilverKey", 1));
        GameLogger.info("CraftingTableUI: Crafted SilverKey and added to player inventory");

        // === Clear crafting table ===
        Arrays.fill(craftingSlots, null);

        // === Remove Key1–Key3 from player inventory ===
        String[] parts = {"Key1", "Key2", "Key3"};
        for (String part : parts) {
            for (int i = 0; i < gp.player.getInventory().size(); i++) {
                ChestInventoryManager.ItemData it = gp.player.getInventory().get(i);
                if (it.getName().equals(part)) {
                    if (it.getQuantity() > 1) {
                        it.setQuantity(it.getQuantity() - 1);
                    } else {
                        gp.player.getInventory().remove(i);
                    }
                    break;
                }
            }
        }

        gp.repaint();
    }

    /**
     * Draws the crafting UI, including slots and the "Craft" button.
     *
     * @param g2d the graphics context to draw on
     */
    public void draw(Graphics2D g2d) {
        if (!isShowing) return;

        // === Define UI window ===
        int windowX = gp.screenWidth / 2 - 200;
        int windowY = gp.screenHeight / 2 - 100;
        int windowWidth = 400;
        int windowHeight = 200;

        int slotSize = 48;
        int offsetX = windowX + 50;
        int offsetY = windowY + 50;

        // === Draw crafting slots ===
        for (int i = 0; i < craftingSlots.length; i++) {
            int x = offsetX + i * (slotSize + 10);
            slotBounds[i] = new Rectangle(x, offsetY, slotSize, slotSize);

            g2d.setColor(Color.GRAY);
            g2d.fillRect(x, offsetY, slotSize, slotSize);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(x, offsetY, slotSize, slotSize);

            if (craftingSlots[i] != null) {
                GameObject item = craftingSlots[i].getItem();
                if (item.image != null) {
                    g2d.drawImage(item.image, x, offsetY, slotSize, slotSize, null);
                }
            }
        }

        // === Draw craft button ===
        craftButtonBounds = new Rectangle(windowX + windowWidth - 100, windowY + windowHeight - 50, 80, 30);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(craftButtonBounds.x, craftButtonBounds.y, craftButtonBounds.width, craftButtonBounds.height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(craftButtonBounds.x, craftButtonBounds.y, craftButtonBounds.width, craftButtonBounds.height);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Craft", craftButtonBounds.x + 20, craftButtonBounds.y + 20);
    }

    /**
     * Returns the index of a crafting slot under the given mouse point.
     *
     * @param p screen point
     * @return slot index (0–2) or -1 if none
     */
    public int getSlotAt(Point p) {
        for (int i = 0; i < slotBounds.length; i++) {
            if (slotBounds[i].contains(p)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes the item from the specified crafting slot.
     *
     * @param slot the slot index (0–2)
     * @return the removed item, or {@code null} if invalid
     */
    public ChestInventoryManager.ItemData removeFromSlot(int slot) {
        if (slot < 0 || slot >= craftingSlots.length) return null;
        ChestInventoryManager.ItemData item = craftingSlots[slot];
        craftingSlots[slot] = null;
        return item;
    }

    /**
     * Places an item into the first empty crafting slot.
     *
     * @param item item to insert into the crafting grid
     */
    public void putToFirstEmpty(ChestInventoryManager.ItemData item) {
        for (int i = 0; i < craftingSlots.length; i++) {
            if (craftingSlots[i] == null) {
                craftingSlots[i] = item;
                break;
            }
        }
    }
}
