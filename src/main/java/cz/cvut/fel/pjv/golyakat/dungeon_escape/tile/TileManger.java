package cz.cvut.fel.pjv.golyakat.dungeon_escape.tile;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Třída TileManager odpovídá za načítání, správu a vykreslování mapy (tile mapy)
public class TileManger {
    gamePanel gp; // Odkaz na hlavní herní panel

    public Tile[] tiles; // Pole všech typů dlaždic
    public int[][] mapTileNum; // 2D pole reprezentující číselnou mapu dlaždic

    // Seznam průchozích oblastí (každá oblast je seznam souřadnic Point)
    public List<List<Point>> walkableRegions;

    // Oblast, ve které se nachází hráč na začátku hry
    public List<Point> playerRegion;

    // Konstruktor TileManageru
    public TileManger(gamePanel gp) {
        this.gp = gp;

        tiles = new Tile[10]; // Předem definovaných 10 typů dlaždic
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow]; // Inicializace mapy
        walkableRegions = new ArrayList<>();
        playerRegion = new ArrayList<>();

        getTileImage(); // Načteme obrázky dlaždic
        loadMap("/cz/cvut/fel/pjv/golyakat/dungeon_escape/maps/level1.txt"); // Načteme mapu z textového souboru
        findWalkableRegions(); // Vyhledáme všechny průchozí oblasti
    }

    // Načítá obrázky dlaždic a nastavuje kolizní vlastnosti
    public void getTileImage() {
        try {
            // Pro každý typ dlaždice načteme obrázek a případně nastavíme kolizi
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/floor_not_in_dungeon.png")));

            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/back_wall.png")));
            tiles[1].collision = true;

            tiles[2] = new Tile();
            tiles[2].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/front_wall.png")));
            tiles[2].collision = true;

            tiles[3] = new Tile();
            tiles[3].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/left_side_wall.png")));
            tiles[3].collision = true;

            tiles[4] = new Tile();
            tiles[4].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/right_side_wall.png")));
            tiles[4].collision = true;

            tiles[5] = new Tile();
            tiles[5].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/floor.png")));

            tiles[6] = new Tile();
            tiles[6].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/left_down_corner.png")));
            tiles[6].collision = true;

            tiles[7] = new Tile();
            tiles[7].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/right_down_corner.png")));
            tiles[7].collision = true;

            tiles[8] = new Tile();
            tiles[8].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/left_up_corner.png")));
            tiles[8].collision = true;

            tiles[9] = new Tile();
            tiles[9].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/right_up_corner.png")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Najde všechny průchozí oblasti na mapě
    public void findWalkableRegions() {
        boolean[][] visited = new boolean[gp.maxWorldRow][gp.maxWorldCol];
        walkableRegions.clear();

        // Výchozí pozice hráče
        int playerStartCol = 15;
        int playerStartRow = 22;

        // Procházíme všechny dlaždice a hledáme oblasti s typem dlaždice 5 (floor)
        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                if (!visited[row][col] && mapTileNum[row][col] == 5) {
                    List<Point> region = new ArrayList<>();
                    // Použijeme DFS pro průchod sousedními průchozími dlaždicemi
                    dfs(row, col, visited, region);
                    walkableRegions.add(region);

                    // Zjistíme, zda se hráč nachází v této oblasti
                    boolean playerInRegion = region.stream().anyMatch(p -> p.x == playerStartRow && p.y == playerStartCol);
                    if (playerInRegion) {
                        playerRegion = region;
                    }
                }
            }
        }

        // Debug výstup: kolik oblastí jsme našli
        System.out.println("Found " + walkableRegions.size() + " walkable regions:");
        for (int i = 0; i < walkableRegions.size(); i++) {
            System.out.println("Region " + i + ": " + walkableRegions.get(i).size() + " tiles");
        }
    }

    // DFS algoritmus pro nalezení všech sousedních průchozích dlaždic
    private void dfs(int row, int col, boolean[][] visited, List<Point> region) {
        // Kontrola hran mapy a zda již bylo navštíveno nebo je neprochozí
        if (row < 0 || row >= gp.maxWorldRow || col < 0 || col >= gp.maxWorldCol || visited[row][col] || mapTileNum[row][col] != 5) {
            return;
        }

        visited[row][col] = true;
        region.add(new Point(row, col));

        // Rekurzivně procházíme sousední dlaždice
        dfs(row - 1, col, visited, region); // Nahoru
        dfs(row + 1, col, visited, region); // Dolů
        dfs(row, col - 1, visited, region); // Vlevo
        dfs(row, col + 1, visited, region); // Vpravo
    }

    // Načtení mapy z textového souboru
    public void loadMap(String s) {
        try {
            InputStream is = getClass().getResourceAsStream(s);
            if (is == null) {
                throw new IOException("Could not find map file: " + s);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int col;
            int row = 0;

            // Čteme soubor řádek po řádku a načítáme čísla dlaždic
            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break;

                String[] numbers = line.split(" ");

                for (col = 0; col < gp.maxWorldCol && col < numbers.length; col++) {
                    try {
                        int num = Integer.parseInt(numbers[col]);
                        mapTileNum[row][col] = num;
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format at row " + row + ", col " + col);
                        mapTileNum[row][col] = 0;
                    }
                }

                // Zaplníme zbytek řádku nulami, pokud jsou sloupce kratší než očekáváme
                while (col < gp.maxWorldCol) {
                    mapTileNum[row][col] = 0;
                    col++;
                }

                row++;
            }

            // Vyplníme zbytek mapy nulami, pokud je řádků méně než očekáváme
            while (row < gp.maxWorldRow) {
                for (col = 0; col < gp.maxWorldCol; col++) {
                    mapTileNum[row][col] = 0;
                }
                row++;
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            // Pokud dojde k chybě, vyplníme mapu nulami
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    mapTileNum[row][col] = 0;
                }
            }
        }
    }

    // Vykreslení celé mapy na obrazovku
    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        // Procházíme celou mapu
        while (worldRow < gp.maxWorldRow && worldCol < gp.maxWorldCol) {
            int tileNum = mapTileNum[worldRow][worldCol];

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;

            // Přepočítáme světové souřadnice na souřadnice na obrazovce
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            // Vykreslíme dlaždici
            g2.drawImage(tiles[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
