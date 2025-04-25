package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Slime;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Zombie;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Skeleton;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Třída AssetSetter slouží k nastavení objektů a nepřátel ve hře
public class AssetSetter {
    gamePanel gp; // Odkaz na hlavní herní panel
    Random random; // Generátor náhodných čísel

    // Konstruktor
    public AssetSetter(gamePanel gp) {
        this.gp = gp;
        this.random = new Random();
    }

    // Umístění objektů na mapu
    public void setObg() {
        // Malá truhla
        gp.obj[0] = new Object_Small_Chest();
        gp.obj[0].worldX = 15 * gp.tileSize;
        gp.obj[0].worldY = 21 * gp.tileSize;
        gp.obj[0].Collision = true; // Ensure chest is solid
        System.out.println("Placed chest at: " + gp.obj[0].worldX/gp.tileSize + ", " + gp.obj[0].worldY/gp.tileSize);

        // Side door
        gp.obj[1] = new Object_DoorSide();
        gp.obj[1].worldX = 20 * gp.tileSize;
        gp.obj[1].worldY = 22 * gp.tileSize;
        System.out.println("Placed side door at: " + gp.obj[1].worldX/gp.tileSize + ", " + gp.obj[1].worldY/gp.tileSize);

        // Front doors
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
    }

    // Umístění monster na mapu
    public void setMonster() {
        List<List<Point>> availableRegions = new ArrayList<>(gp.tileH.walkableRegions);

        if (availableRegions.isEmpty()) {
            System.out.println("No regions available for monster spawning.");
            return;
        }

        int playerCol = 15;
        int playerRow = 22;
        int monstersToSpawn = 15;
        List<Point> spawnedPositions = new ArrayList<>();

        for (int i = 0; i < monstersToSpawn; i++) {
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

                // Randomly choose between Slime, Zombie, and Skeleton
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