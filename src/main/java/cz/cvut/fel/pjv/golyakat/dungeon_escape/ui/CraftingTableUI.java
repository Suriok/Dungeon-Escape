package cz.cvut.fel.pjv.golyakat.dungeon_escape.ui;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import java.awt.*;

/**
 * The {@code CraftingTableUI} class represents the graphical interface for crafting items.
 */
public class CraftingTableUI {

    private final gamePanel gp;
    private boolean isShowing;
    private final ChestInventoryManager.ItemData[] craftingSlots;
    private final Rectangle[] slotBounds;
    private Rectangle craftButtonBounds;

    public CraftingTableUI(gamePanel gp) {
        this.gp = gp;
        this.isShowing = false;
        this.craftingSlots = new ChestInventoryManager.ItemData[3];
        this.slotBounds = new Rectangle[3];
        this.craftButtonBounds = null;
    }

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

    public void open() {
        isShowing = true;
        GameLogger.info("CraftingTableUI: Opened");
    }

    public void close() {
        isShowing = false;
        GameLogger.info("CraftingTableUI: Closed");
    }

    public boolean isShowing() {
        return isShowing;
    }

    public Rectangle[] getSlotBounds() {
        return slotBounds;
    }

    public Rectangle getCraftButtonBounds() {
        return craftButtonBounds;
    }

    public ChestInventoryManager.ItemData getCraftingSlot(int index) {
        if (index >= 0 && index < craftingSlots.length) {
            return craftingSlots[index];
        }
        return null;
    }

    public void setCraftingSlot(int index, ChestInventoryManager.ItemData item) {
        if (index >= 0 && index < craftingSlots.length) {
            craftingSlots[index] = item;
        }
    }

    public boolean isKeyPart(String name) {
        return "Key1".equals(name) || "Key2".equals(name) || "Key3".equals(name);
    }

    public boolean containsPart(String name) {
        for (ChestInventoryManager.ItemData d : craftingSlots)
            if (d != null && d.getName().equals(name)) return true;
        return false;
    }

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

    public int getSlotAt(Point p) {
        for (int i = 0; i < slotBounds.length; i++) {
            if (slotBounds[i].contains(p)) {
                return i;
            }
        }
        return -1;
    }

    public ChestInventoryManager.ItemData removeFromSlot(int slot) {
        if (slot < 0 || slot >= craftingSlots.length) return null;
        ChestInventoryManager.ItemData item = craftingSlots[slot];
        craftingSlots[slot] = null;
        return item;
    }

    public void putToFirstEmpty(ChestInventoryManager.ItemData item) {
        for (int i = 0; i < craftingSlots.length; i++) {
            if (craftingSlots[i] == null) {
                craftingSlots[i] = item;
                break;
            }
        }
    }
}