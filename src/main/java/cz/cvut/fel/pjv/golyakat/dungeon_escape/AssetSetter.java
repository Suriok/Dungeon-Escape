package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Slime;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_key;

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

        // Dveře (Side)
        gp.obj[2] = new Object_DoorSide();
        gp.obj[2].worldX = 20 * gp.tileSize;
        gp.obj[2].worldY = 22 * gp.tileSize;

        // Dveře (Front) — umisťujeme několik dveří na různé pozice
        gp.obj[3] = new Object_DoorFront();
        gp.obj[3].worldX = 32 * gp.tileSize;
        gp.obj[3].worldY = 24 * gp.tileSize;

        gp.obj[4] = new Object_DoorFront();
        gp.obj[4].worldX = 28 * gp.tileSize;
        gp.obj[4].worldY = 20 * gp.tileSize;

        gp.obj[5] = new Object_DoorFront();
        gp.obj[5].worldX = 38 * gp.tileSize;
        gp.obj[5].worldY = 20 * gp.tileSize;
    }

    // Umístění monster na mapu
    public void setMonster() {
        // Získáme všechny procházející oblasti mapy
        List<List<Point>> availableRegions = new ArrayList<>(gp.tileH.walkableRegions);

        if (availableRegions.isEmpty()) {
            System.out.println("No regions available for slime spawning.");
            return;
        }

        // Výchozí pozice hráče
        int playerCol = 15; // worldX = gp.tileSize * 15
        int playerRow = 22; // worldY = gp.tileSize * 22

        // Kolik slime chceme spawnout
        int slimesToSpawn = 6;
        List<Point> spawnedPositions = new ArrayList<>(); // Uchovává pozice již spawnutých slime

        for (int i = 0; i < slimesToSpawn; i++) {
            // Vybereme region (pokud máme méně regionů než slime, začneme od začátku seznamu)
            List<Point> region = availableRegions.get(i % availableRegions.size());

            // Pokud je region prázdný, přeskočíme
            if (region.isEmpty()) {
                System.out.println("Region " + (i % availableRegions.size()) + " is empty, skipping slime spawn.");
                continue;
            }

            int attempts = 0;
            int maxAttempts = 50;
            Point spawnPoint = null;

            // Pokusíme se najít vhodné místo pro spawn
            while (attempts < maxAttempts) {
                spawnPoint = region.get(random.nextInt(region.size()));
                int col = spawnPoint.y;
                int row = spawnPoint.x;

                // Zkontrolujeme vzdálenost od hráče
                int distToPlayerX = Math.abs(col - playerCol);
                int distToPlayerY = Math.abs(row - playerRow);
                if (distToPlayerX < 10 && distToPlayerY < 10) {
                    attempts++;
                    spawnPoint = null;
                    continue;
                }

                // Zkontrolujeme vzdálenost od ostatních slime
                boolean tooClose = false;
                for (Point existing : spawnedPositions) {
                    int distX = Math.abs(existing.y - col);
                    int distY = Math.abs(existing.x - row);
                    if (distX < 5 && distY < 5) { // Minimální vzdálenost mezi slime je 5 tile
                        tooClose = true;
                        break;
                    }
                }

                if (!tooClose) {
                    break; // Našli jsme vhodné místo
                }

                attempts++;
                spawnPoint = null;
            }

            // Pokud jsme našli vhodné místo, spawnujeme slima
            if (spawnPoint != null) {
                int col = spawnPoint.y;
                int row = spawnPoint.x;
                gp.monster[i] = new Monster_Slime(gp);
                gp.monster[i].worldX = col * gp.tileSize;
                gp.monster[i].worldY = row * gp.tileSize;
                spawnedPositions.add(new Point(row, col));

                System.out.println("Spawned slime " + i + " at col: " + col + ", row: " + row);
            } else {
                System.out.println("Failed to spawn slime " + i + " after " + maxAttempts + " attempts (too close to player or other slimes).");
            }
        }
    }
}
