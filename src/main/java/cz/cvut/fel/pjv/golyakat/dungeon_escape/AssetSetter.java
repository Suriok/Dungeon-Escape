package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss.Boss_Eye;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss.Boss_Goblin;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Slime;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Zombie;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Skeleton;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_CraftingTable;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Třída {@code AssetSetter} je zodpovědná za umísťování objektů a monster
 * do herního světa při inicializaci nebo načítání úrovně.
 */
public class AssetSetter {

    /**
     * Instance správce truhlic {@link ChestInventoryManager}, který se používá
     * pro naplnění obsahu jednotlivých truhlic.
     */
    public ChestInventoryManager chestInventoryManager;

    /**
     * Odkaz na hlavní herní panel, kde se nachází mapa, objekty, potvory atd.
     */
    gamePanel gp;

    /**
     * Generátor náhodných čísel pro výběr náhodných monster nebo pozic.
     */
    Random random;

    /**
     * Vytvoří nový {@code AssetSetter} pro daný herní panel.
     *
     * @param gp hlavní panel hry
     */
    public AssetSetter(gamePanel gp) {
        this.gp = gp;
        this.random = new Random();
        this.chestInventoryManager = gp.chestInventoryManager;
    }

    /**
     * Nastavuje objekty (dveře, truhly, crafting table) na všech mapách.
     * <p>
     * Rozděleno podle úrovně mapy (0 a 1). Používá se ruční rozmístění.
     * </p>
     */
    public void setObg() {
        // ————— 1. ÚROVEŇ —————

        // Přidání dveří na mapu 0
        gp.obj[0][1] = new Object_DoorSide();
        gp.obj[0][1].worldX = 20 * gp.tileSize;
        gp.obj[0][1].worldY = 22 * gp.tileSize;

        gp.obj[0][2] = new Object_DoorFront();
        gp.obj[0][2].worldX = 32 * gp.tileSize;
        gp.obj[0][2].worldY = 24 * gp.tileSize;

        gp.obj[0][3] = new Object_DoorFront();
        gp.obj[0][3].worldX = 28 * gp.tileSize;
        gp.obj[0][3].worldY = 20 * gp.tileSize;

        gp.obj[0][4] = new Object_DoorFront();
        gp.obj[0][4].worldX = 38 * gp.tileSize;
        gp.obj[0][4].worldY = 20 * gp.tileSize;

        gp.obj[0][5] = new Object_DoorFront();
        gp.obj[0][5].worldX = 41 * gp.tileSize;
        gp.obj[0][5].worldY = 10 * gp.tileSize;
        ((Object_DoorFront) gp.obj[0][5]).requiresKey = true;

        gp.obj[0][6] = new Object_DoorSide();
        gp.obj[0][6].worldX = 31 * gp.tileSize;
        gp.obj[0][6].worldY = 7 * gp.tileSize;
        ((Object_DoorSide) gp.obj[0][6]).requiresKey = true;

        // Přidání truhlic s obsahem (ručně)
        Map<String, Integer> chest0Items = new HashMap<>();
        chest0Items.put("leather_pants", 1);
        chest0Items.put("leather_helmet", 1);
        chest0Items.put("iron_sword", 1);
        gp.obj[0][0] = new Object_Small_Chest(this, 0, chest0Items);
        gp.obj[0][0].worldX = 15 * gp.tileSize;
        gp.obj[0][0].worldY = 21 * gp.tileSize;

        // Další truhly s částmi klíče
        Map<String, Integer> chest7Items = new HashMap<>();
        chest7Items.put("Key1", 1);
        gp.obj[0][7] = new Object_Small_Chest(this, 7, chest7Items);
        gp.obj[0][7].worldX = 40 * gp.tileSize;
        gp.obj[0][7].worldY = 30 * gp.tileSize;

        Map<String, Integer> chest8Items = new HashMap<>();
        chest8Items.put("Key2", 1);
        chest8Items.put("leather_bib", 1);
        gp.obj[0][8] = new Object_Small_Chest(this, 8, chest8Items);
        gp.obj[0][8].worldX = 25 * gp.tileSize;
        gp.obj[0][8].worldY = 13 * gp.tileSize;

        Map<String, Integer> chest9Items = new HashMap<>();
        chest9Items.put("Key3", 1);
        chest9Items.put("leather_boots", 1);
        gp.obj[0][9] = new Object_Small_Chest(this, 9, chest9Items);
        gp.obj[0][9].worldX = 50 * gp.tileSize;
        gp.obj[0][9].worldY = 21 * gp.tileSize;

        // Craftovací stůl
        gp.obj[0][10] = new Object_CraftingTable();
        gp.obj[0][10].worldX = 38 * gp.tileSize;
        gp.obj[0][10].worldY = 14 * gp.tileSize;

        // ————— 2. ÚROVEŇ —————

        // Dveře
        gp.obj[1][1] = new Object_DoorSide();
        gp.obj[1][1].worldX = 15 * gp.tileSize;
        gp.obj[1][1].worldY = 19 * gp.tileSize;

        gp.obj[1][2] = new Object_DoorFront();
        gp.obj[1][2].worldX = 27 * gp.tileSize;
        gp.obj[1][2].worldY = 21 * gp.tileSize;

        gp.obj[1][3] = new Object_DoorSide();
        gp.obj[1][3].worldX = 39 * gp.tileSize;
        gp.obj[1][3].worldY = 29 * gp.tileSize;
        ((Object_DoorSide) gp.obj[1][3]).requiresKey = true;

        gp.obj[1][4] = new Object_DoorFront();
        gp.obj[1][4].worldX = 44 * gp.tileSize;
        gp.obj[1][4].worldY = 21 * gp.tileSize;
        ((Object_DoorFront) gp.obj[1][4]).requiresKey = true;

        gp.obj[1][5] = new Object_DoorFront();
        gp.obj[1][5].worldX = 34 * gp.tileSize;
        gp.obj[1][5].worldY = 17 * gp.tileSize;

        // Truhly
        Map<String, Integer> chest10Items = new HashMap<>();
        chest10Items.put("iron_bib", 1);
        chest10Items.put("iron_pants", 1);
        chest10Items.put("emerald_sword", 1);
        gp.obj[1][0] = new Object_Small_Chest(this, 0, chest10Items);
        gp.obj[1][0].worldX = 13 * gp.tileSize;
        gp.obj[1][0].worldY = 18 * gp.tileSize;

        Map<String, Integer> chest17Items = new HashMap<>();
        chest17Items.put("Key1", 1);
        gp.obj[1][7] = new Object_Small_Chest(this, 7, chest17Items);
        gp.obj[1][7].worldX = 30 * gp.tileSize;
        gp.obj[1][7].worldY = 18 * gp.tileSize;

        Map<String, Integer> chest18Items = new HashMap<>();
        chest18Items.put("Key2", 1);
        chest18Items.put("iron_helmet", 1);
        gp.obj[1][8] = new Object_Small_Chest(this, 8, chest18Items);
        gp.obj[1][8].worldX = 32 * gp.tileSize;
        gp.obj[1][8].worldY = 27 * gp.tileSize;

        Map<String, Integer> chest19Items = new HashMap<>();
        chest19Items.put("Key3", 1);
        chest19Items.put("iron_boots", 1);
        gp.obj[1][9] = new Object_Small_Chest(this, 9, chest19Items);
        gp.obj[1][9].worldX = 35 * gp.tileSize;
        gp.obj[1][9].worldY = 12 * gp.tileSize;

        gp.obj[1][10] = new Object_CraftingTable();
        gp.obj[1][10].worldX = 38 * gp.tileSize;
        gp.obj[1][10].worldY = 27 * gp.tileSize;
    }

    /**
     * Umístí boss monstra na aktuální mapu.
     * <p>
     * Podle mapy spawne buď {@link Boss_Goblin} (mapa 0), nebo {@link Boss_Eye} (mapa 1).
     * Ostatní spawnování monster je zakomentováno.
     * </p>
     */
    public void setMonster() {
        List<List<Point>> availableRegions = new ArrayList<>(gp.tileH.walkableRegions);

        if (availableRegions.isEmpty()) {
            System.out.println("No regions available for monster spawning.");
            return;
        }

        int monstersToSpawn = 14; // Místo pro bosse

        if (gp.currentMap == 0) {
            gp.monster[0][0] = new Boss_Goblin(gp);
            gp.monster[0][0].worldX = 35 * gp.tileSize;
            gp.monster[0][0].worldY = 8 * gp.tileSize;
        } else if (gp.currentMap == 1) {
            gp.monster[1][0] = new Boss_Eye(gp);
            gp.monster[1][0].worldX = 41 * gp.tileSize;
            gp.monster[1][0].worldY = 25 * gp.tileSize;
        }

        // Zakomentovaný kód pro spawn běžných monster (Slime, Zombie, Skeleton)
    }
}
