package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.*;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * The {@code ChestInventoryManager} class manages the contents and state of chests in the game.
 * <p>
 * It enables saving and loading data about chests, including their items.
 * Each chest has its own ID and a list of items.
 * </p>
 */
public class ChestInventoryManager {

    /**
     * Map of all known chests in the game, with their unique ID as the key.
     */
    private final Map<Integer, ChestData> chestDataMap = new HashMap<>();

    /**
     * Constructor for the chest inventory manager. Currently performs no initialization.
     */
    public ChestInventoryManager() {}

    /**
     * The {@code ChestData} class represents the specific state of a chest:
     * whether it has been opened and what items it contains.
     */
    public static class ChestData implements Serializable {

        /**
         * Flag indicating whether the chest is open.
         */
        private boolean isOpen;

        /**
         * List of items stored in the chest.
         */
        private List<ItemData> items;

        /**
         * Creates a new chest object with a state and list of items.
         *
         * @param isOpen whether the chest is open
         * @param items list of items inside
         */
        public ChestData(boolean isOpen, List<ItemData> items) {
            this.isOpen = isOpen;
            this.items = items;
        }

        /**
         * Returns the open state of the chest.
         *
         * @return {@code true} if the chest is open
         */
        public boolean isOpen() {
            return isOpen;
        }

        /**
         * Returns the list of items in the chest.
         *
         * @return list of {@link ItemData}
         */
        public List<ItemData> getItems() {
            return items;
        }
    }

    /**
     * The {@code ItemData} class represents a specific item,
     * its name, quantity, and reference to the actual {@link GameObject}.
     */
    public static class ItemData implements Serializable {

        private String name;
        private int quantity;

        /**
         * The in-game item – an instance of the {@link GameObject} class.
         * Marked as {@code transient} because it is not serialized.
         */
        private transient GameObject item;

        /**
         * Creates a new item record with the given name and quantity.
         *
         * @param name the name of the item
         * @param quantity the number of items
         */
        public ItemData(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
            this.item = createItemFromName(name);
        }

        /** @return the name of the item */
        public String getName() { return name; }

        /** @return the quantity of the item */
        public int getQuantity() { return quantity; }

        /** @param quantity sets the new quantity of the item */
        public void setQuantity(int quantity) { this.quantity = quantity; }

        /**
         * Returns the game object associated with this item.
         * If not initialized, creates it based on the name.
         *
         * @return an instance of {@link GameObject}
         */
        public GameObject getItem() {
            if (item == null) {
                item = createItemFromName(name);
            }
            return item;
        }

        /** @param item manually sets the item object */
        public void setItem(GameObject item) {
            this.item = item;
        }
    }

    /**
     * Creates a specific instance of an item based on its name.
     *
     * @param name the name of the item as a string
     * @return the corresponding instance of {@link GameObject}, or {@code null} if unknown
     */
    private static GameObject createItemFromName(String name) {
        switch (name) {
            case "Apple": return new Item_Apple();
            case "blubbery": return new Item_Blubbery();
            case "potion": return new Item_HealthePotion();
            case "leather_pants": return new leather_pants();
            case "leather_bib": return new leather_bib();
            case "leather_helmet": return new leather_helmet();
            case "leather_boots": return new leather_boots();
            case "iron_pants": return new iron_pants();
            case "iron_helmet": return new iron_helmet();
            case "iron_boots": return new iron_boots();
            case "iron_bib": return new iron_bib();
            case "iron_sword": return new Iron_sword(2);
            case "emerald_sword": return new Emerald_sword(3);
            case "Key": return new Item_Key();
            case "Key1": return new Item_partKey1();
            case "Key2": return new Item_partKey2();
            case "Key3": return new Item_partKey3();
            case "SilverKey": return new Item_SilverKey();
            default:
                GameLogger.error("Unknown item name: " + name);
                return null;
        }
    }

    /**
     * Creates new chest data with the specified default items.
     *
     * @param id the chest's ID
     * @param defaultItems a map of item names and quantities for default items
     * @return the newly created chest data
     */
    public ChestData getChestData(int id, Map<String, Integer> defaultItems) {
        List<ItemData> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : defaultItems.entrySet()) {
            ItemData itemData = new ItemData(entry.getKey(), entry.getValue());
            if (itemData.getItem() != null) {
                items.add(itemData);
            }
        }
        ChestData chestData = new ChestData(false, items);
        chestDataMap.put(id, chestData);
        return chestData;
    }

    /**
     * Updates the state of a chest in the internal map and saves the data to disk.
     *
     * @param id the chest's ID
     * @param chestData the updated chest data
     */
    public void updateChestData(int id, ChestData chestData) {
        chestDataMap.put(id, chestData);
        saveChestData();
    }

    /**
     * Saves all chest data to a file on disk.
     * <p>
     * Currently uses object serialization to the file {@code chest_data.xml}.
     * </p>
     */
    public void saveChestData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("chest_data.xml"))) {
            oos.writeObject(chestDataMap);
            GameLogger.info("Chest data saved successfully.");
        } catch (IOException e) {
            GameLogger.error("Error saving chest data: " + e.getMessage());
        }
    }

    /**
     * Loads chest data from a file – not yet implemented.
     * <p>In the future, it will read from a saved file.</p>
     */
    private void loadChestData() {
        GameLogger.info("Starting fresh, no previous chest data loaded.");
    }

    /**
     * Performs the specified action for each chest in the map.
     *
     * @param action a function that takes the ID and list of items
     */
    public void forEachChest(BiConsumer<Integer, List<ItemData>> action) {
        chestDataMap.forEach((id, chest) -> action.accept(id, chest.items));
    }

    /**
     * Replaces the contents of a specific chest with a new list of items.
     * <p>
     * Used when loading a saved game.
     * </p>
     *
     * @param id the chest's ID
     * @param items the new list of items
     */
    public void overrideChest(int id, List<ItemData> items) {
        chestDataMap.put(id, new ChestData(true, items));
    }

    /**
     * Resets all chest data – used at the start of a new game.
     */
    public void resetChestData() {
        chestDataMap.clear();
    }
}