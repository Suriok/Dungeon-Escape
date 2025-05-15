package cz.cvut.fel.pjv.golyakat.dungeon_escape.saveData;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete save state of the game.
 * <p>
 * This class contains information about the current map, level state,
 * player position, inventory, equipment, and monsters.
 * It is serialized/deserialized using Jackson XML.
 * </p>
 */
public class SaveData {

    /** Saved player-related data (position, inventory, equipment). */
    public final PlayerData player = new PlayerData();

    /** Index of the currently active map. */
    public int currentMap;

    /** Boolean array indicating which levels have been spawned. */
    public boolean[] levelSpawned;

    /** List of all monsters currently present in the active map. */
    public final List<MonsterData> monsters = new ArrayList<>();

    /**
     * Stores player's position, health, inventory and equipment.
     */
    public static class PlayerData {

        /** X-coordinate of the player's position in the world. */
        public int worldX;

        /** Y-coordinate of the player's position in the world. */
        public int worldY;

        /** Current life (health) of the player. */
        public int life;

        /** List of items in the player's backpack (inventory). */
        public final List<ItemData> backpack = new ArrayList<>();

        /** List of equipped armor pieces (each slot as separate item). */
        public final List<ItemData> armor = new ArrayList<>();

        /** Currently equipped weapon, if any. */
        public ItemData weapon;

        /** Currently equipped grade item (e.g., rank or special badge). */
        public ItemData grade;
    }

    /**
     * Represents a single monster's saved state.
     */
    public static class MonsterData {

        /** Monster type (e.g., "Monster_Zombie", "Boss_Eye"). */
        public String type;

        /** X-coordinate of the monster's position. */
        public int worldX;

        /** Y-coordinate of the monster's position. */
        public int worldY;

        /** Monster's remaining health points. */
        public int life;

        /** Whether the monster is already dead. */
        public boolean dead;
    }

    /**
     * Represents an item in an inventory or equipped slot.
     */
    public static class ItemData {

        /** Name of the item (used to recreate the object). */
        public String name;

        /** Quantity of this item (used for stackable items like keys or potions). */
        public int qty;

        /** Default constructor required for XML deserialization. */
        public ItemData() {}

        /**
         * Constructs a new item data instance with name and quantity.
         *
         * @param name name of the item
         * @param qty  quantity of the item
         */
        public ItemData(String name, int qty) {
            this.name = name;
            this.qty = qty;
        }
    }
}
