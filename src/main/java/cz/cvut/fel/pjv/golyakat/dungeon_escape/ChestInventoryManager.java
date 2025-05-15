package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import java.util.*;

/**
 * Manages chest contents in the game.
 */
public class ChestInventoryManager {
    private final Map<Integer, List<ItemData>> chestDataMap = new HashMap<>();

    public static class ItemData {
        private final String name;
        private int quantity;
        private transient GameObject item;

        public ItemData(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
            this.item = createItemFromName(name);
        }

        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public GameObject getItem() {
            if (item == null) {
                item = createItemFromName(name);
            }
            return item;
        }

        public ItemType getType() {
            return switch (name) {
                case "Apple", "blubbery", "potion" -> ItemType.HEALING;
                case "Key", "SilverKey" -> ItemType.KEY;
                case "Key1", "Key2", "Key3" -> ItemType.KEY_PART;
                case "leather_pants", "leather_bib", "leather_helmet", "leather_boots",
                     "iron_pants", "iron_helmet", "iron_boots", "iron_bib" -> ItemType.ARMOR;
                case "iron_sword", "emerald_sword" -> ItemType.WEAPON;
                default -> throw new IllegalArgumentException("Unknown item type: " + name);
            };
        }
    }

    private static GameObject createItemFromName(String name) {
        return ItemFactory.makeItem(name);
    }

    public List<ItemData> getChestData(int id, Map<String, Integer> defaultItems) {
        if (!chestDataMap.containsKey(id)) {
            List<ItemData> items = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : defaultItems.entrySet()) {
                ItemData itemData = new ItemData(entry.getKey(), entry.getValue());
                if (itemData.getItem() != null) {
                    items.add(itemData);
                }
            }
            chestDataMap.put(id, items);
        }
        return chestDataMap.get(id);
    }

    public void resetChestData() {
        chestDataMap.clear();
    }
}
