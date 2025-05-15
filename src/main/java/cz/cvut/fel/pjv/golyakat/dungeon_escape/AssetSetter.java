package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.boss.Boss_Eye;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.boss.Boss_Goblin;
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
 * The {@code AssetSetter} class is responsible for placing objects and monsters
 * in the game world during initialization or level loading.
 */
public class AssetSetter {
    /**
     * Reference to the main game panel, which contains the map, objects, monsters, etc.
     */
    final gamePanel gp;

    /**
     * Random number generator for selecting random monsters or positions.
     */
    final Random random;

    /**
     * Creates a new {@code AssetSetter} for the given game panel.
     *
     * @param gp the main game panel
     */
    public AssetSetter(gamePanel gp) {
        this.gp = gp;
        this.random = new Random();
    }

    /**
     * Places objects (doors, chests, crafting table) on all maps.
     * <p>
     * Divided by map level (0 and 1). Uses manual placement.
     * </p>
     */
    public void setObg() {
        // ————— LEVEL 1 —————

        // Adding doors to map 0
        gp.obj[0][1] = new Object_DoorSide(gp, false);
        gp.obj[0][1].worldX = 20 * gp.tileSize;
        gp.obj[0][1].worldY = 22 * gp.tileSize;

        gp.obj[0][2] = new Object_DoorFront(gp, false);
        gp.obj[0][2].worldX = 32 * gp.tileSize;
        gp.obj[0][2].worldY = 24 * gp.tileSize;

        gp.obj[0][3] = new Object_DoorFront(gp, false);
        gp.obj[0][3].worldX = 28 * gp.tileSize;
        gp.obj[0][3].worldY = 20 * gp.tileSize;

        gp.obj[0][4] = new Object_DoorFront(gp, false);
        gp.obj[0][4].worldX = 38 * gp.tileSize;
        gp.obj[0][4].worldY = 20 * gp.tileSize;

        gp.obj[0][5] = new Object_DoorFront(gp, true);
        gp.obj[0][5].worldX = 41 * gp.tileSize;
        gp.obj[0][5].worldY = 10 * gp.tileSize;

        gp.obj[0][6] = new Object_DoorSide(gp, true);
        gp.obj[0][6].worldX = 31 * gp.tileSize;
        gp.obj[0][6].worldY = 7 * gp.tileSize;

        // Adding chests with contents (manually)
        Map<String, Integer> chest0Items = new HashMap<>();
        chest0Items.put("leather_pants", 1);
        chest0Items.put("leather_helmet", 1);
        chest0Items.put("iron_sword", 1);
        chest0Items.put("Apple", 3);
        chest0Items.put("blubbery", 2);
        gp.obj[0][0] = new Object_Small_Chest(gp, 0, chest0Items);
        gp.obj[0][0].worldX = 15 * gp.tileSize;
        gp.obj[0][0].worldY = 21 * gp.tileSize;

        // Additional chests with key parts
        Map<String, Integer> chest7Items = new HashMap<>();
        chest7Items.put("Key1", 1);
        chest7Items.put("blubbery", 2);
        chest7Items.put("potion", 2);
        gp.obj[0][7] = new Object_Small_Chest(gp, 7, chest7Items);
        gp.obj[0][7].worldX = 40 * gp.tileSize;
        gp.obj[0][7].worldY = 30 * gp.tileSize;

        Map<String, Integer> chest8Items = new HashMap<>();
        chest8Items.put("Key2", 1);
        chest8Items.put("leather_bib", 1);
        chest8Items.put("potion", 4);
        chest8Items.put("Apple", 2);
        gp.obj[0][8] = new Object_Small_Chest(gp, 8, chest8Items);
        gp.obj[0][8].worldX = 25 * gp.tileSize;
        gp.obj[0][8].worldY = 13 * gp.tileSize;

        Map<String, Integer> chest9Items = new HashMap<>();
        chest9Items.put("Key3", 1);
        chest9Items.put("leather_boots", 1);
        chest9Items.put("potion", 2);
        chest9Items.put("blubbery", 5);
        gp.obj[0][9] = new Object_Small_Chest(gp, 9, chest9Items);
        gp.obj[0][9].worldX = 50 * gp.tileSize;
        gp.obj[0][9].worldY = 21 * gp.tileSize;

        // Crafting table
        gp.obj[0][10] = new Object_CraftingTable();
        gp.obj[0][10].worldX = 38 * gp.tileSize;
        gp.obj[0][10].worldY = 14 * gp.tileSize;

        // ————— LEVEL 2 —————

        // Doors
        gp.obj[1][1] = new Object_DoorSide(gp, false);
        gp.obj[1][1].worldX = 15 * gp.tileSize;
        gp.obj[1][1].worldY = 19 * gp.tileSize;

        gp.obj[1][2] = new Object_DoorFront(gp, false);
        gp.obj[1][2].worldX = 27 * gp.tileSize;
        gp.obj[1][2].worldY = 21 * gp.tileSize;

        gp.obj[1][3] = new Object_DoorSide(gp, true);
        gp.obj[1][3].worldX = 39 * gp.tileSize;
        gp.obj[1][3].worldY = 29 * gp.tileSize;

        gp.obj[1][4] = new Object_DoorFront(gp, false);
        gp.obj[1][4].worldX = 44 * gp.tileSize;
        gp.obj[1][4].worldY = 21 * gp.tileSize;

        gp.obj[1][5] = new Object_DoorFront(gp, true);
        gp.obj[1][5].worldX = 34 * gp.tileSize;
        gp.obj[1][5].worldY = 17 * gp.tileSize;

        // Chests
        Map<String, Integer> chest10Items = new HashMap<>();
        chest10Items.put("iron_bib", 1);
        chest10Items.put("iron_pants", 1);
        chest10Items.put("emerald_sword", 1);
        gp.obj[1][0] = new Object_Small_Chest(gp, 0, chest10Items);
        gp.obj[1][0].worldX = 13 * gp.tileSize;
        gp.obj[1][0].worldY = 18 * gp.tileSize;

        Map<String, Integer> chest17Items = new HashMap<>();
        chest17Items.put("Key1", 1);
        gp.obj[1][7] = new Object_Small_Chest(gp, 7, chest17Items);
        gp.obj[1][7].worldX = 30 * gp.tileSize;
        gp.obj[1][7].worldY = 18 * gp.tileSize;

        Map<String, Integer> chest18Items = new HashMap<>();
        chest18Items.put("Key2", 1);
        chest18Items.put("iron_helmet", 1);
        gp.obj[1][8] = new Object_Small_Chest(gp, 8, chest18Items);
        gp.obj[1][8].worldX = 32 * gp.tileSize;
        gp.obj[1][8].worldY = 27 * gp.tileSize;

        Map<String, Integer> chest19Items = new HashMap<>();
        chest19Items.put("Key3", 1);
        chest19Items.put("iron_boots", 1);
        gp.obj[1][9] = new Object_Small_Chest(gp, 9, chest19Items);
        gp.obj[1][9].worldX = 35 * gp.tileSize;
        gp.obj[1][9].worldY = 12 * gp.tileSize;

        gp.obj[1][10] = new Object_CraftingTable();
        gp.obj[1][10].worldX = 38 * gp.tileSize;
        gp.obj[1][10].worldY = 27 * gp.tileSize;
    }

    /**
     * Places boss monsters on the current map.
     * <p>
     * Depending on the map, spawns either {@link Boss_Goblin} (map 0) or {@link Boss_Eye} (map 1).
     * Other monster spawning is commented out.
     * </p>
     */
    public void setMonster() {
        List<List<Point>> availableRegions = new ArrayList<>(gp.tileH.walkableRegions);

        if (availableRegions.isEmpty()) {
            GameLogger.info("No regions available for monster spawning.");
            return;
        }

        int monstersToSpawn = 14; // Reduced by 1 to make room for the boss
        List<Point> spawnedPositions = new ArrayList<>();

        if (gp.currentMap == 0) {
            gp.monster[0][0] = new Boss_Goblin(gp);
            gp.monster[0][0].worldX = 35 * gp.tileSize;
            gp.monster[0][0].worldY = 8 * gp.tileSize;
        } else if (gp.currentMap == 1) {
            gp.monster[1][0] = new Boss_Eye(gp);
            gp.monster[1][0].worldX = 41 * gp.tileSize;
            gp.monster[1][0].worldY = 25 * gp.tileSize;
        }

        // Spawn other monsters
        for (int i = 1; i < monstersToSpawn; i++) {
            List<Point> region = availableRegions.get(i % availableRegions.size());

            if (region.isEmpty()) {
                GameLogger.info("Region " + (i % availableRegions.size()) + " is empty, skipping monster spawn.");
                continue;
            }

            Point spawnPoint = null;
            int maxAttempts = 50;

            for (int attempt = 0; attempt < maxAttempts; attempt++) {
                Point candidate = region.get(random.nextInt(region.size()));
                int col = candidate.y;
                int row = candidate.x;
                int spawnX = col * gp.tileSize;
                int spawnY = row * gp.tileSize;

                // Do not spawn in a 5x5 tile rectangle around the player
                Rectangle monsterRect = new Rectangle(spawnX, spawnY, gp.tileSize, gp.tileSize);
                Rectangle safeZone = new Rectangle(
                        gp.player.worldX - gp.tileSize * 2,
                        gp.player.worldY - gp.tileSize * 2,
                        gp.tileSize * 10,
                        gp.tileSize * 10
                );

                if (monsterRect.intersects(safeZone)) {
                    continue;
                }

                boolean tooClose = false;
                for (Point existing : spawnedPositions) {
                    int dx = Math.abs(existing.y - col);
                    int dy = Math.abs(existing.x - row);
                    if (dx < 5 && dy < 5) {
                        tooClose = true;
                        break;
                    }
                }

                if (!tooClose) {
                    spawnPoint = candidate;
                    break;
                }
            }

            if (spawnPoint == null) {
                System.out.printf("Failed to spawn monster %d after %d attempts.%n", i, maxAttempts);
                continue;
            }

            int col = spawnPoint.y;
            int row = spawnPoint.x;
            int spawnX = col * gp.tileSize;
            int spawnY = row * gp.tileSize;

            Entity monster = switch (random.nextInt(3)) {
                case 0 -> new Monster_Slime(gp);
                case 1 -> new Monster_Zombie(gp);
                case 2 -> new Monster_Skeleton(gp);
                default -> throw new IllegalStateException("Unexpected monster type");
            };

            monster.worldX = spawnX;
            monster.worldY = spawnY;
            gp.monster[gp.currentMap][i] = monster;
            spawnedPositions.add(new Point(row, col));

            System.out.printf("Spawned %s %d at col: %d, row: %d%n", monster.name, i, col, row);
        }
    }
}