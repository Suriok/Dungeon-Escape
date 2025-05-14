package cz.cvut.fel.pjv.golyakat.dungeon_escape.saveData;

import java.util.ArrayList;
import java.util.List;

public class SaveData {
    public PlayerData player = new PlayerData();
    public int currentMap;
    public boolean[] levelSpawned;
    public List<MonsterData> monsters = new ArrayList<>();

    public static class PlayerData {
        public int worldX;
        public int worldY;
        public int life;
        public List<ItemData> backpack = new ArrayList<>();
        public List<ItemData> armor = new ArrayList<>();
        public ItemData weapon;
        public ItemData grade;
    }

    public static class MonsterData {
        public String type;
        public int worldX;
        public int worldY;
        public int life;
        public boolean dead;
    }

    public static class ItemData {
        public String name;
        public int qty;

        public ItemData() {}
        public ItemData(String name, int qty) {
            this.name = name;
            this.qty = qty;
        }
    }
}
