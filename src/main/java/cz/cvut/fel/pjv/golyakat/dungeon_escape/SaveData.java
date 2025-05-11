package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "save")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveData {

    @JacksonXmlProperty(isAttribute = true)
    public int currentMap; // Добавлено для сохранения текущей карты

    @JacksonXmlElementWrapper(localName = "levelsSpawned")
    @JacksonXmlProperty(localName = "level")
    public boolean[] levelSpawned; // Добавлено для сохранения состояния спавна уровней

    public static class ItemData {
        @JacksonXmlProperty(isAttribute = true)
        public String name;
        @JacksonXmlProperty(isAttribute = true)
        public int qty;

        public ItemData() {}

        public ItemData(String name, int qty) {
            this.name = name;
            this.qty = qty;
        }
    }

    public static class PlayerData {
        public int worldX;
        public int worldY;
        public int life;

        @JacksonXmlElementWrapper(localName = "backpack")
        @JacksonXmlProperty(localName = "backpackItem")
        public List<ItemData> backpack;

        @JacksonXmlElementWrapper(localName = "armor")
        @JacksonXmlProperty(localName = "armorItem")
        public List<ItemData> armor;

        public ItemData weapon;
        public ItemData grade;

        public PlayerData() {
            backpack = new ArrayList<>();
            armor = new ArrayList<>();
        }
    }

    public static class MonsterData {
        @JacksonXmlProperty(isAttribute = true)
        public String type;
        public int worldX;
        public int worldY;
        public int life;
        public boolean dead;
    }

    public static class ChestData {
        @JacksonXmlProperty(isAttribute = true)
        public int id;

        @JacksonXmlElementWrapper(localName = "items")
        @JacksonXmlProperty(localName = "item")
        public List<ItemData> items;

        public ChestData() {
            items = new ArrayList<>();
        }
    }

    public PlayerData player;

    @JacksonXmlElementWrapper(localName = "monsters")
    @JacksonXmlProperty(localName = "monster")
    public List<MonsterData> monsters;

    @JacksonXmlElementWrapper(localName = "chests")
    @JacksonXmlProperty(localName = "chest")
    public List<ChestData> chests;

    public SaveData() {
        player = new PlayerData();
        monsters = new ArrayList<>();
        chests = new ArrayList<>();
    }
}