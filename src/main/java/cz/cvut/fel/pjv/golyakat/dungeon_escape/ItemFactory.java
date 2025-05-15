package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.iron_bib;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.iron_boots;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.iron_helmet;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.iron_pants;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_bib;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_boots;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_helmet;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.leather_pants;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Apple;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Blubbery;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_HealthePotion;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Key;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.Item_SilverKey;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.Item_partKey1;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.Item_partKey2;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.Item_partKey3;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.Emerald_sword;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.Iron_sword;

/**
 * The {@code ItemFactory} class is responsible for creating instances of in-game items
 * based on their string identifiers (names).
 * <p>
 * It acts as a central point for instantiating consumables, armor, weapons, and key items
 * used in chests, inventory, and crafting.
 * </p>
 */
public class ItemFactory {
    public static GameObject makeItem(String name) {
        if (name == null) return null;

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
            default -> {
                GameLogger.error("ItemFactory: Unknown item name: " + name);
                yield null;
            }
        };
    }
}
