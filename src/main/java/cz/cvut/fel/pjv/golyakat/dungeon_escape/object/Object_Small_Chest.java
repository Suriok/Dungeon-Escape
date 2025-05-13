package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.AssetSetter;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * The {@code Object_Small_Chest} class represents an interactive chest
 * that contains randomly or fixedly defined items and can be opened.
 * <p>
 * The chest has a graphical representation and its own inventory that can be displayed to the player.
 * </p>
 */
public class Object_Small_Chest extends GameObject {

    /**
     * Determines whether the chest is already open.
     */
    private boolean isOpen;

    /**
     * Image of the chest's graphical interface (inventory).
     */
    private BufferedImage inventoryImage;

    /**
     * Flag indicating whether the chest's inventory should be displayed.
     */
    private boolean showInventory;

    /**
     * Unique identifier for the chest for state saving.
     */
    private int id;

    /**
     * List of items currently contained in the chest.
     */
    private List<ChestInventoryManager.ItemData> items;

    /**
     * Reference to the chest inventory manager that handles saving and loading state.
     */
    private ChestInventoryManager chestInventoryManager;

    /**
     * Map with fixed (unchanging) items for this chest.
     */
    private Map<String, Integer> fixedArmor;

    /**
     * Creates a new instance of a small chest with predefined contents.
     *
     * @param gp         reference to {@link AssetSetter}, through which we get the inventory manager
     * @param id         unique identifier for the chest
     * @param fixedArmor map with fixed items (e.g., armor)
     */
    public Object_Small_Chest(AssetSetter gp, int id, Map<String, Integer> fixedArmor) {
        this.id = id;
        this.fixedArmor = (fixedArmor != null) ? fixedArmor : new HashMap<>();
        this.chestInventoryManager = gp.chestInventoryManager;

        name = "small_chest";
        Collision = true;
        isOpen = false;
        showInventory = false;

        Map<String, Integer> randomItems = generateRandomItems();
        Map<String, Integer> allItems = new HashMap<>(randomItems);
        allItems.putAll(this.fixedArmor);

        ChestInventoryManager.ChestData chestData = chestInventoryManager.getChestData(id, allItems);
        this.items = chestData.getItems();
        this.isOpen = chestData.isOpen();

        try {
            var chestStream = getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/small_chest.png");
            if (chestStream != null) image = ImageIO.read(chestStream);

            var inventoryStream = getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/case_inventory.png");
            if (inventoryStream != null) inventoryImage = ImageIO.read(inventoryStream);
        } catch (Exception e) {
            GameLogger.error("Error loading chest images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generates random contents for the chest (consumable items).
     *
     * @return map of item names and their quantities
     */
    private Map<String, Integer> generateRandomItems() {
        Map<String, Integer> randomItems = new HashMap<>();
        Random random = new Random();
        String[] possibleItems = {"Apple", "blubbery", "potion"};

        int numItems = random.nextInt(3) + 1;
        for (int i = 0; i < numItems; i++) {
            String itemName = possibleItems[random.nextInt(possibleItems.length)];
            int quantity = random.nextInt(3) + 1;
            randomItems.put(itemName, randomItems.getOrDefault(itemName, 0) + quantity);
        }
        return randomItems;
    }

    /**
     * Opens the chest and displays its contents to the player.
     * <p>The state is saved to the chest manager.</p>
     */
    public void open() {
        showInventory = true;
        if (!isOpen) isOpen = true;
        chestInventoryManager.updateChestData(id, new ChestInventoryManager.ChestData(isOpen, items));
    }

    /**
     * Closes the chest and updates its state.
     */
    public void close() {
        showInventory = false;
        chestInventoryManager.updateChestData(id, new ChestInventoryManager.ChestData(isOpen, items));
    }

    /** @return {@code true} if the chest is open */
    public boolean isOpen() {
        return isOpen;
    }

    /** @return {@code true} if the chest's inventory should be displayed */
    public boolean isShowingInventory() {
        return showInventory;
    }

    /** @return the chest's inventory image */
    public BufferedImage getInventoryImage() {
        return inventoryImage;
    }

    /** @return list of items in the chest */
    public List<ChestInventoryManager.ItemData> getItems() {
        return items;
    }

    /**
     * Removes 1 piece from the given item in the chest and possibly removes it completely.
     *
     * @param item the item to be removed
     */
    public void removeItem(ChestInventoryManager.ItemData item) {
        item.setQuantity(item.getQuantity() - 1);
        if (item.getQuantity() <= 0) items.remove(item);
        chestInventoryManager.updateChestData(id, new ChestInventoryManager.ChestData(isOpen, items));
    }

    /** @return the chest's identifier */
    public int getId() {
        return id;
    }

    /**
     * Removes an item from the list by its index.
     *
     * @param index position of the item in the list
     */
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    /**
     * Renders the chest on the map according to its position relative to the player.
     *
     * @param g2d graphics context
     * @param gp  game panel instance
     */
    @Override
    public void draw(java.awt.Graphics2D g2d, gamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        if (image != null) {
            g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
