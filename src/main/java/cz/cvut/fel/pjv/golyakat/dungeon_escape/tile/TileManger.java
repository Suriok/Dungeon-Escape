package cz.cvut.fel.pjv.golyakat.dungeon_escape.tile;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Třída {@code TileManger} odpovídá za načítání, správu a vykreslování herní mapy.
 * <p>
 * Obsahuje logiku pro načítání dlaždic (tile) z obrázků a mapy z textových souborů,
 * včetně identifikace průchozích oblastí a výpočtu viditelných dlaždic.
 * </p>
 */
public class TileManger {

    /** Odkaz na hlavní herní panel */
    gamePanel gp;

    /** Pole definujících vlastnosti jednotlivých typů dlaždic */
    public Tile[] tiles;

    /**
     * Třírozměrné pole obsahující čísla dlaždic na jednotlivých mapách.
     * Struktura: [mapa][řádek][sloupec]
     */
    public int[][][] mapTileNum;

    /**
     * Seznam všech průchozích oblastí (regionů), které se skládají z několika sousedních dlaždic.
     * Každá oblast je seznam objektů {@link Point}.
     */
    public List<List<Point>> walkableRegions;

    /**
     * Seznam souřadnic aktuální oblasti, ve které se nachází hráč na začátku hry.
     */
    public List<Point> playerRegion;

    /**
     * Vytváří správce dlaždic a inicializuje mapu, včetně načtení obrázků a výpočtu průchodných oblastí.
     *
     * @param gp hlavní herní panel
     */
    public TileManger(gamePanel gp) {
        this.gp = gp;

        tiles = new Tile[15];
        mapTileNum = new int[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
        walkableRegions = new ArrayList<>();
        playerRegion = new ArrayList<>();

        getTileImage();
        loadMap("/cz/cvut/fel/pjv/golyakat/dungeon_escape/maps/level1.txt", 0);
        loadMap("/cz/cvut/fel/pjv/golyakat/dungeon_escape/maps/level2.txt", 1);
        findWalkableRegions();
    }

    /**
     * Načítá obrázky všech typů dlaždic a nastavuje jejich kolizní vlastnosti, pokud je potřeba.
     */
    public void getTileImage() {
        try {
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/floor_not_in_dungeon.png")));

            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/back_wall.png")));
            tiles[1].collision = true;

            tiles[2] = new Tile();
            tiles[2].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/front_wall.png")));
            tiles[2].collision = true;

            tiles[3] = new Tile();
            tiles[3].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/left_side_wall.png")));
            tiles[3].collision = true;

            tiles[4] = new Tile();
            tiles[4].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/right_side_wall.png")));
            tiles[4].collision = true;

            tiles[5] = new Tile();
            tiles[5].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/floor.png")));

            tiles[6] = new Tile();
            tiles[6].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/left_down_corner.png")));
            tiles[6].collision = true;

            tiles[7] = new Tile();
            tiles[7].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/right_down_corner.png")));
            tiles[7].collision = true;

            tiles[8] = new Tile();
            tiles[8].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/left_up_corner.png")));
            tiles[8].collision = true;

            tiles[9] = new Tile();
            tiles[9].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/right_up_corner.png")));

            tiles[10] = new Tile();
            tiles[10].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/ledde.png")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Najde a uloží všechny průchozí oblasti (podlahy) ve světě pomocí DFS algoritmu.
     * <p>
     * Hráčova počáteční oblast je také detekována a uložena zvlášť.
     * </p>
     */
    public void findWalkableRegions() {
        boolean[][] visited = new boolean[gp.maxWorldRow][gp.maxWorldCol];
        walkableRegions.clear();

        int playerStartCol = 15;
        int playerStartRow = 22;

        for (int row = 0; row < gp.maxWorldRow; row++) {
            for (int col = 0; col < gp.maxWorldCol; col++) {
                if (!visited[row][col] && mapTileNum[gp.currentMap][row][col] == 5) {
                    List<Point> region = new ArrayList<>();
                    dfs(row, col, visited, region);
                    walkableRegions.add(region);

                    boolean playerInRegion = region.stream().anyMatch(p -> p.x == playerStartRow && p.y == playerStartCol);
                    if (playerInRegion) {
                        playerRegion = region;
                    }
                }
            }
        }

        System.out.println("Found " + walkableRegions.size() + " walkable regions:");
        for (int i = 0; i < walkableRegions.size(); i++) {
            System.out.println("Region " + i + ": " + walkableRegions.get(i).size() + " tiles");
        }
    }

    /**
     * Pomocná rekurzivní metoda pro DFS algoritmus pro vyhledávání sousedních průchozích dlaždic.
     *
     * @param row    aktuální řádek
     * @param col    aktuální sloupec
     * @param visited matice již navštívených pozic
     * @param region seznam bodů tvořících jednu oblast
     */
    private void dfs(int row, int col, boolean[][] visited, List<Point> region) {
        if (row < 0 || row >= gp.maxWorldRow || col < 0 || col >= gp.maxWorldCol ||
                visited[row][col] || mapTileNum[gp.currentMap][row][col] != 5) {
            return;
        }

        visited[row][col] = true;
        region.add(new Point(row, col));

        dfs(row - 1, col, visited, region); // Nahoru
        dfs(row + 1, col, visited, region); // Dolů
        dfs(row, col - 1, visited, region); // Vlevo
        dfs(row, col + 1, visited, region); // Vpravo
    }

    /**
     * Načte mapu z textového souboru a uloží ji do matice mapy.
     *
     * @param filePath cesta k textovému souboru mapy
     * @param map      index mapy (např. 0 = první úroveň)
     */
    public void loadMap(String filePath, int map) {
        try {
            InputStream is = getClass().getResourceAsStream(filePath);
            if (is == null) {
                throw new IOException("Could not find map file: " + filePath);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) break;

                String[] numbers = line.split(" ");
                for (int col = 0; col < gp.maxWorldCol && col < numbers.length; col++) {
                    try {
                        mapTileNum[map][row][col] = Integer.parseInt(numbers[col]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number format at row " + row + ", col " + col);
                        mapTileNum[map][row][col] = 0;
                    }
                }
                row++;
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            for (int row = 0; row < gp.maxWorldRow; row++) {
                for (int col = 0; col < gp.maxWorldCol; col++) {
                    mapTileNum[map][row][col] = 0;
                }
            }
        }
    }

    /**
     * Vykreslí celou mapu na základě pozice hráče a dlaždic v okolí.
     *
     * @param g2 grafický kontext
     */
    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldRow < gp.maxWorldRow && worldCol < gp.maxWorldCol) {
            int tileNum = mapTileNum[gp.currentMap][worldRow][worldCol];

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;

            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            g2.drawImage(tiles[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);

            worldCol++;
            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}
