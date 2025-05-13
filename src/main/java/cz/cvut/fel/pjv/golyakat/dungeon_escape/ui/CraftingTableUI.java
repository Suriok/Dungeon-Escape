package cz.cvut.fel.pjv.golyakat.dungeon_escape.ui;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import java.awt.*;

/**
 * The {@code CraftingTableUI} class represents the graphical interface for crafting items.
 * <p>
 * In the current implementation, it is used to create the {@code SilverKey} item from three key parts
 * – Key1, Key2, and Key3.
 * </p>
 */
public class CraftingTableUI {

    /** Reference to the main game panel for accessing player data, dimensions, and repainting. */
    final private gamePanel gp;

    /** Indicates whether the crafting window is currently displayed. */
    private boolean isShowing;

    /** Array of slots used for crafting. Expects exactly 3 key parts. */
    final private ChestInventoryManager.ItemData[] craftingSlots;

    /** Rectangles representing interactive areas for the slots. */
    final private Rectangle[] slotBounds;

    /** Rectangle for the "Craft" button. */
    private Rectangle craftButtonBounds;

    /**
     * Initializes the crafting UI, sets empty slots, and hides the window.
     *
     * @param gp the main game panel
     */
    public CraftingTableUI(gamePanel gp) {
        this.gp = gp;
        this.isShowing = false;
        this.craftingSlots = new ChestInventoryManager.ItemData[3];
        this.slotBounds = new Rectangle[3];
        this.craftButtonBounds = null;
    }

    /**
     * Adds an item to the list. If it already exists, increments its quantity.
     *
     * @param list the list to add to
     * @param item the item to add or increment
     */
    public static void addOrIncrement(java.util.List<ChestInventoryManager.ItemData> list,
                                      ChestInventoryManager.ItemData item) {
        for (ChestInventoryManager.ItemData d : list) {
            if (d.getName().equals(item.getName())) {
                d.setQuantity(d.getQuantity() + item.getQuantity());
                return;
            }
        }
        list.add(item);
    }

    /** Displays the crafting window. */
    public void open() {
        isShowing = true;
        GameLogger.info("CraftingTableUI: Opened");
    }

    /** Hides the crafting window. */
    public void close() {
        isShowing = false;
        GameLogger.info("CraftingTableUI: Closed");
    }

    /** @return whether the crafting window is currently displayed */
    public boolean isShowing() {
        return isShowing;
    }

    /** @return array of rectangles representing the item slots */
    public Rectangle[] getSlotBounds() {
        return slotBounds;
    }

    /** @return rectangle representing the "Craft" button */
    public Rectangle getCraftButtonBounds() {
        return craftButtonBounds;
    }

    /**
     * Returns the item in the slot at the given index.
     *
     * @param index the slot number (0–2)
     * @return the item instance or {@code null}
     */
    public ChestInventoryManager.ItemData getCraftingSlot(int index) {
        if (index >= 0 && index < craftingSlots.length) {
            return craftingSlots[index];
        }
        return null;
    }

    /**
     * Sets an item in a specific crafting slot.
     *
     * @param index the slot index (0–2)
     * @param item the item to place
     */
    public void setCraftingSlot(int index, ChestInventoryManager.ItemData item) {
        if (index >= 0 && index < craftingSlots.length) {
            craftingSlots[index] = item;
        }
    }

    /**
     * Determines whether the given name belongs to a key part.
     *
     * @param name the item name
     * @return {@code true} if it is "Key1", "Key2", or "Key3"
     */
    public boolean isKeyPart(String name) {
        return "Key1".equals(name) || "Key2".equals(name) || "Key3".equals(name);
    }

    /**
     * Determines whether the given key part is already in a crafting slot.
     *
     * @param name the name of the key part
     * @return {@code true} if the slot already contains this part
     */
    public boolean containsPart(String name) {
        for (ChestInventoryManager.ItemData d : craftingSlots)
            if (d != null && d.getName().equals(name)) return true;
        return false;
    }

    /**
     * Attempts to craft the "SilverKey" item from all three key parts.
     * <p>
     * If Key1, Key2, and Key3 are present, it creates a new item, adds it to the player's inventory,
     * and removes the parts from both the crafting slots and the player's inventory.
     * </p>
     */
    public void craftSilverKey() {
        boolean hasKey1 = false, hasKey2 = false, hasKey3 = false;

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

        gp.player.addItem(new ChestInventoryManager.ItemData("SilverKey", 1));
        GameLogger.info("CraftingTableUI: Crafted SilverKey and added to player inventory");

        for (int i = 0; i < craftingSlots.length; i++) {
            craftingSlots[i] = null;
        }

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
     * Renders the UI interface for the crafting table.
     *
     * @param g2d the graphics context
     */
    public void draw(Graphics2D g2d) {
        if (!isShowing) return;

        int windowX = gp.screenWidth / 2 - 200;
        int windowY = gp.screenHeight / 2 - 100;
        int windowWidth = 400;
        int windowHeight = 200;

        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(windowX, windowY, windowWidth, windowHeight, 25, 25);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(windowX, windowY, windowWidth, windowHeight, 25, 25);

        int slotSize = 48;
        int offsetX = windowX + 50;
        int offsetY = windowY + 50;

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

        craftButtonBounds = new Rectangle(windowX + windowWidth - 100, windowY + windowHeight - 50, 80, 30);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(craftButtonBounds.x, craftButtonBounds.y, craftButtonBounds.width, craftButtonBounds.height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(craftButtonBounds.x, craftButtonBounds.y, craftButtonBounds.width, craftButtonBounds.height);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Craft", craftButtonBounds.x + 20, craftButtonBounds.y + 20);
    }
}