package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Apple;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Blubbery;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_HealthePotion;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Key; // Add import for Item_Key
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_pants;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_helmet;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_boots;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.iron_pants;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.iron_helmet;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.Item_SilverKey;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.Item_partKey1;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.Item_partKey2;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.Item_partKey3;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.Iron_sword;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChestInventoryManager {
    private Map<Integer, ChestData> chestDataMap;

    public ChestInventoryManager() {
        chestDataMap = new HashMap<>();
        loadChestData();
    }

    public static class ChestData implements Serializable {
        private boolean isOpen;
        private List<ItemData> items;

        public ChestData(boolean isOpen, List<ItemData> items) {
            this.isOpen = isOpen;
            this.items = items;
        }

        public boolean isOpen() {
            return isOpen;
        }

        public List<ItemData> getItems() {
            return items;
        }
    }

    public static class ItemData implements Serializable {
        private String name;
        private int quantity;
        private transient GameObject item;

        public ItemData(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
            this.item = createItemFromName(name);
            System.out.println("Created ItemData: name=" + name + ", quantity=" + quantity + ", item=" + (this.item != null ? this.item.name : "null"));
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public GameObject getItem() {
            if (item == null) {
                item = createItemFromName(name);
            }
            return item;
        }

        public void setItem(GameObject item) {
            this.item = item;
        }
    }

    private static GameObject createItemFromName(String name) {
        System.out.println("Attempting to create item: " + name);
        switch (name) {
            case "Apple":
                return new Item_Apple();
            case "blubbery":
                return new Item_Blubbery();
            case "potion":
                return new Item_HealthePotion();
            case "leather_pants":
                return new leather_pants();
            case "leather_helmet":
                return new leather_helmet();
            case "leather_boots":
                return new leather_boots();
            case "iron_pants":
                return new iron_pants();
            case "iron_helmet":
                return new iron_helmet();
            case "iron_sword":
                return new Iron_sword(2, 2); // Указываем attack=2
            case "Key":
                return new Item_Key();
            case "Key1":
                return new Item_partKey1();
            case "Key2":
                return new Item_partKey2();
            case "Key3":
                return new Item_partKey3();
            case "SilverKey":
                return new Item_SilverKey();
            default:
                System.err.println("Unknown item name: " + name);
                return null;
        }
    }

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

    public void updateChestData(int id, ChestData chestData) {
        chestDataMap.put(id, chestData);
        saveChestData();
    }

    public void saveChestData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("chest_data.dat"))) {
            oos.writeObject(chestDataMap);
            System.out.println("Chest data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving chest data: " + e.getMessage());
        }
    }

    public void resetChestData() {
        try {
            File file = new File("chest_inventory.xml");
            try (FileWriter writer = new FileWriter(file, false)) {
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<chests>\n</chests>");
                System.out.println("Chest inventory XML reset successfully.");
            }
        } catch (IOException e) {
            System.err.println("Error resetting chest inventory XML: " + e.getMessage());
        }
    }

    private void loadChestData() {
        System.out.println("Starting fresh, no previous chest data loaded.");
    }
}