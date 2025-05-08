package cz.cvut.fel.pjv.golyakat.dungeon_escape;

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

public class AssetSetter {
    public ChestInventoryManager chestInventoryManager;
    gamePanel gp;
    Random random;

    public AssetSetter(gamePanel gp) {
        this.gp = gp;
        this.random = new Random();
        this.chestInventoryManager = gp.chestInventoryManager;
    }

    public void setObg() {
        // Боковая дверь
        gp.obj[1] = new Object_DoorSide();
        gp.obj[1].worldX = 20 * gp.tileSize;
        gp.obj[1].worldY = 22 * gp.tileSize;
        System.out.println("Placed side door at: " + gp.obj[1].worldX/gp.tileSize + ", " + gp.obj[1].worldY/gp.tileSize);

        // Передние двери
        gp.obj[2] = new Object_DoorFront();
        gp.obj[2].worldX = 32 * gp.tileSize;
        gp.obj[2].worldY = 24 * gp.tileSize;
        System.out.println("Placed front door 1 at: " + gp.obj[2].worldX/gp.tileSize + ", " + gp.obj[2].worldY/gp.tileSize);

        gp.obj[3] = new Object_DoorFront();
        gp.obj[3].worldX = 28 * gp.tileSize;
        gp.obj[3].worldY = 20 * gp.tileSize;
        System.out.println("Placed front door 2 at: " + gp.obj[3].worldX/gp.tileSize + ", " + gp.obj[3].worldY/gp.tileSize);

        gp.obj[4] = new Object_DoorFront();
        gp.obj[4].worldX = 38 * gp.tileSize;
        gp.obj[4].worldY = 20 * gp.tileSize;
        System.out.println("Placed front door 3 at: " + gp.obj[4].worldX/gp.tileSize + ", " + gp.obj[4].worldY/gp.tileSize);

        gp.obj[5] = new Object_DoorFront();
        gp.obj[5].worldX = 41 * gp.tileSize;
        gp.obj[5].worldY = 10 * gp.tileSize;
        ((Object_DoorFront) gp.obj[5]).requiresKey = true; // This door requires a Silver Key
        System.out.println("Placed front door (requires Silver Key) at: " + gp.obj[5].worldX/gp.tileSize + ", " + gp.obj[5].worldY/gp.tileSize);

        gp.obj[6] = new Object_DoorSide();
        gp.obj[6].worldX = 31 * gp.tileSize;
        gp.obj[6].worldY = 7 * gp.tileSize;
        ((Object_DoorSide) gp.obj[6]).requiresKey = true; // This door requires a key
        System.out.println("Placed side door (requires key) at: " + gp.obj[6].worldX/gp.tileSize + ", " + gp.obj[6].worldY/gp.tileSize);

        Map<String, Integer> chest0Items = new HashMap<>();
        chest0Items.put("leather_pants", 1);
        chest0Items.put("leather_helmet", 1);
        chest0Items.put("iron_sword", 1);
        gp.obj[0] = new Object_Small_Chest(this, 0, chest0Items);
        gp.obj[0].worldX = 15 * gp.tileSize;
        gp.obj[0].worldY = 21 * gp.tileSize;

        Map<String, Integer> chest7Items = new HashMap<>();
        chest7Items.put("Key1", 1);
        gp.obj[7] = new Object_Small_Chest(this, 7, chest7Items);
        gp.obj[7].worldX = 40 * gp.tileSize;
        gp.obj[7].worldY = 30 * gp.tileSize;

        Map<String, Integer> chest8Items = new HashMap<>();
        chest8Items.put("Key2", 1);
        gp.obj[8] = new Object_Small_Chest(this, 8, chest8Items);
        gp.obj[8].worldX = 25 * gp.tileSize;
        gp.obj[8].worldY = 13 * gp.tileSize;

        Map<String, Integer> chest9Items = new HashMap<>();
        chest9Items.put("Key3", 1);
        gp.obj[9] = new Object_Small_Chest(this, 9, chest9Items);
        gp.obj[9].worldX = 50 * gp.tileSize;
        gp.obj[9].worldY = 21 * gp.tileSize;

        gp.obj[10] = new Object_CraftingTable();
        gp.obj[10].worldX = 38 * gp.tileSize;
        gp.obj[10].worldY = 14 * gp.tileSize;

        gp.player.worldX = 15 * gp.tileSize;
        gp.player.worldY = 22 * gp.tileSize;

        if (!gp.objectsLogged) {
            System.out.println("Objects initialized:");
            System.out.println("Chest 0 at (" + (gp.obj[0].worldX / gp.tileSize) + ", " + (gp.obj[0].worldY / gp.tileSize) + "): " + chest0Items);
            System.out.println("Chest 7 at (" + (gp.obj[7].worldX / gp.tileSize) + ", " + (gp.obj[7].worldY / gp.tileSize) + "): " + chest7Items);
            System.out.println("Chest 8 at (" + (gp.obj[8].worldX / gp.tileSize) + ", " + (gp.obj[8].worldY / gp.tileSize) + "): " + chest8Items);
            System.out.println("Chest 9 at (" + (gp.obj[9].worldX / gp.tileSize) + ", " + (gp.obj[9].worldY / gp.tileSize) + "): " + chest9Items);
            System.out.println("Crafting Table at (" + (gp.obj[10].worldX / gp.tileSize) + ", " + (gp.obj[10].worldY / gp.tileSize) + ")");
            gp.objectsLogged = true;
        }

    }

    public void setMonster() {
        List<List<Point>> availableRegions = new ArrayList<>(gp.tileH.walkableRegions);

        if (availableRegions.isEmpty()) {
            System.out.println("No regions available for monster spawning.");
            return;
        }

        int playerCol = 15;
        int playerRow = 22;
        int monstersToSpawn = 14; // Reduced by 1 to make room for the boss
        List<Point> spawnedPositions = new ArrayList<>();

        // Spawn the Boss_Goblin at fixed position (x:35, y:8)
        gp.monster[0] = new Boss_Goblin(gp);
        gp.monster[0].worldX = 35 * gp.tileSize;
        gp.monster[0].worldY = 8 * gp.tileSize;
        spawnedPositions.add(new Point(8, 35));
        System.out.println("Spawned Boss Goblin at col: 35, row: 8");

        // Spawn other monsters
        for (int i = 1; i < monstersToSpawn; i++) {
            List<Point> region = availableRegions.get(i % availableRegions.size());

            if (region.isEmpty()) {
                System.out.println("Region " + (i % availableRegions.size()) + " is empty, skipping monster spawn.");
                continue;
            }

            int attempts = 0;
            int maxAttempts = 50;
            Point spawnPoint = null;

            while (attempts < maxAttempts) {
                spawnPoint = region.get(random.nextInt(region.size()));
                int col = spawnPoint.y;
                int row = spawnPoint.x;

                int distToPlayerX = Math.abs(col - playerCol);
                int distToPlayerY = Math.abs(row - playerRow);
                if (distToPlayerX < 10 && distToPlayerY < 10) {
                    attempts++;
                    spawnPoint = null;
                    continue;
                }

                boolean tooClose = false;
                for (Point existing : spawnedPositions) {
                    int distX = Math.abs(existing.y - col);
                    int distY = Math.abs(existing.x - row);
                    if (distX < 5 && distY < 5) {
                        tooClose = true;
                        break;
                    }
                }

                if (!tooClose) {
                    break;
                }

                attempts++;
                spawnPoint = null;
            }

            if (spawnPoint != null) {
                int col = spawnPoint.y;
                int row = spawnPoint.x;

                int monsterType = random.nextInt(3);
                switch (monsterType) {
                    case 0:
                        gp.monster[i] = new Monster_Slime(gp);
                        break;
                    case 1:
                        gp.monster[i] = new Monster_Zombie(gp);
                        break;
                    case 2:
                        gp.monster[i] = new Monster_Skeleton(gp);
                        break;
                }

                gp.monster[i].worldX = col * gp.tileSize;
                gp.monster[i].worldY = row * gp.tileSize;
                spawnedPositions.add(new Point(row, col));

                System.out.println("Spawned " + gp.monster[i].name + " " + i + " at col: " + col + ", row: " + row);
            } else {
                System.out.println("Failed to spawn monster " + i + " after " + maxAttempts + " attempts.");
            }
        }
    }
}