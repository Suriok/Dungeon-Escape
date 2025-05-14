
        package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.*;

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
        return switch (name) {
            case "Apple" -> new Item_Apple();
            case "blubbery" -> new Item_Blubbery();
            case "potion" -> new Item_HealthePotion();
            case "leather_pants" -> new leather_pants();
            case "leather_bib" -> new leather_bib();
            case "leather_helmet" -> new leather_helmet();
            case "leather_boots" -> new leather_boots();
            case "iron_pants" -> new iron_pants();
            case "iron_helmet" -> new iron_helmet();
            case "iron_boots" -> new iron_boots();
            case "iron_bib" -> new iron_bib();
            case "iron_sword" -> new Iron_sword(2);
            case "emerald_sword" -> new Emerald_sword(3);
            case "Key" -> new Item_Key();
            case "Key1" -> new Item_partKey1();
            case "Key2" -> new Item_partKey2();
            case "Key3" -> new Item_partKey3();
            case "SilverKey" -> new Item_SilverKey();
            default -> null;
        };
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
