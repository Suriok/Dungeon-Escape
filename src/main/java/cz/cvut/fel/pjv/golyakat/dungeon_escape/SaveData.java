package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Data class representing the structure of a saved game.
 * <p>
 * Enables serialization/deserialization using the Jackson library in XML format.
 * Contains information about the player, monsters, chests, and the current map.
 * </p>
 */
@JacksonXmlRootElement(localName = "save")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveData {

    /**
     * Index of the currently active map.
     * Used for switching between different levels.
     */
    @JacksonXmlProperty(isAttribute = true)
    public int currentMap;

    /**
     * Array of flags indicating whether individual levels have already been "spawned"
     * (e.g., generating monsters only on the first entry).
     */
    @JacksonXmlElementWrapper(localName = "levelsSpawned")
    @JacksonXmlProperty(localName = "level")
    public boolean[] levelSpawned;

    /**
     * Inner data class representing an item in the inventory or chest.
     */
    public static class ItemData {
        /**
         * Name of the item.
         */
        @JacksonXmlProperty(isAttribute = true)
        public String name;

        /**
         * Quantity of the item.
         */
        @JacksonXmlProperty(isAttribute = true)
        public int qty;

        /**
         * Default constructor (required for deserialization).
         */
        public ItemData() {}

        /**
         * Constructor setting the name  name and quantity.
         *
         * @param name name of the item
         * @param qty number of items
         */
        public ItemData(String name, int qty) {
            this.name = name;
            this.qty = qty;
        }
    }

    /**
     * Data class storing information about the player.
     */
    public static class PlayerData {
        /**
         * X position of the player in world coordinates.
         */
        public int worldX;

        /**
         * Y position of the player in world coordinates.
         */
        public int worldY;

        /**
         * Current number of player lives.
         */
        public int life;

        /**
         * List of items in the player's backpack.
         */
        @JacksonXmlElementWrapper(localName = "backpack")
        @JacksonXmlProperty(localName = "backpackItem")
        public List<ItemData> backpack;

        /**
         * List of armor pieces equipped by the player.
         */
        @JacksonXmlElementWrapper(localName = "armor")
        @JacksonXmlProperty(localName = "armorItem")
        public List<ItemData> armor;

        /**
         * Currently selected weapon of the player.
         */
        public ItemData weapon;

        /**
         * Player level or upgrade.
         */
        public ItemData grade;

        /**
         * Default constructor – initializes an empty backpack and armor.
         */
        public PlayerData() {
            backpack = new ArrayList<>();
            armor = new ArrayList<>();
        }
    }

    /**
     * Data class representing a single monster in the game.
     */
    public static class MonsterData {
        /**
         * Type of the monster (e.g., "zombie", "skeleton").
         */
        @JacksonXmlProperty(isAttribute = true)
        public String type;

        /**
         * X position of the monster.
         */
        public int worldX;

        /**
         * Y position of the monster.
         */
        public int worldY;

        /**
         * Current number of monster lives.
         */
        public int life;

        /**
         * Flag indicating whether the monster is dead.
         */
        public boolean dead;
    }

    /**
     * Data class representing a single chest on the map.
     */
    public static class ChestData {
        /**
         * Unique ID of the chest, used for its identification.
         */
        @JacksonXmlProperty(isAttribute = true)
        public int id;

        /**
         * List of items stored in this chest.
         */
        @JacksonXmlElementWrapper(localName = "items")
        @JacksonXmlProperty(localName = "item")
        public List<ItemData> items;

        /**
         * Default constructor – initializes an empty list of items.
         */
        public ChestData() {
            items = new ArrayList<>();
        }
    }

    /**
     * Object storing the complete state of the player.
     */
    public PlayerData player;

    /**
     * List of all monsters currently existing on the map.
     */
    @JacksonXmlElementWrapper(localName = "monsters")
    @JacksonXmlProperty(localName = "monster")
    public List<MonsterData> monsters;

    /**
     * List of all chests on the current map and their contents.
     */
    @JacksonXmlElementWrapper(localName = "chests")
    @JacksonXmlProperty(localName = "chest")
    public List<ChestData> chests;

    /**
     * Default constructor – initializes an empty player and empty lists of monsters and chests.
     */
    public SaveData() {
        player = new PlayerData();
        monsters = new ArrayList<>();
        chests = new ArrayList<>();
    }
}