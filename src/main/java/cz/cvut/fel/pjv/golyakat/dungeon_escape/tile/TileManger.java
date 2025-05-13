package cz.cvut.fel.pjv.golyakat.dungeon_escape.tile;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The {@code TileManger} class is responsible for loading, managing,
 * and rendering the game map.
 * <p>
 * It includes logic for loading tile images and reading maps from text files,
 * as well as identifying walkable regions and calculating visible tiles.
 * </p>
 */
public class TileManger {

    /** Reference to the main game panel */
    gamePanel gp;

    /** Array defining properties of individual tile types */
    public Tile[] tiles;

    /**
     * A three-dimensional array containing tile numbers for each map.
     * Structure: [map][row][column]
     */
    public int[][][] mapTileNum;

    /**
     * A list of all walkable regions consisting of multiple adjacent tiles.
     * Each region is a list of {@link Point} objects.
     */
    public List<List<Point>> walkableRegions;

    /**
     * A list of coordinates of the region the player is in at the beginning of the game.
     */
    public List<Point> playerRegion;

    /**
     * Creates the tile manager and initializes the map,
     * including image loading and walkable region detection.
     *
     * @param gp the main game panel
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
     * Loads images for all tile types and sets their collision property if necessary.
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
     * Finds and stores all walkable regions (floors) in the world using the DFS algorithm.
     * <p>
     * The player's starting region is also detected and stored separately.
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

        GameLogger.info("Found " + walkableRegions.size() + " walkable regions:");
        for (int i = 0; i < walkableRegions.size(); i++) {
            GameLogger.info("Region " + i + ": " + walkableRegions.get(i).size() + " tiles");
        }
    }

    /**
     * Helper recursive method for the DFS algorithm to find neighboring walkable tiles.
     *
     * @param row    current row
     * @param col    current column
     * @param visited matrix of already visited positions
     * @param region list of points forming one region
     */
    private void dfs(int row, int col, boolean[][] visited, List<Point> region) {
        if (row < 0 || row >= gp.maxWorldRow || col < 0 || col >= gp.maxWorldCol ||
                visited[row][col] || mapTileNum[gp.currentMap][row][col] != 5) {
            return;
        }

        visited[row][col] = true;
        region.add(new Point(row, col));

        dfs(row - 1, col, visited, region); // Up
        dfs(row + 1, col, visited, region); // Down
        dfs(row, col - 1, visited, region); // Left
        dfs(row, col + 1, visited, region); // Right
    }

    /**
     * Loads a map from a text file and stores it in the map matrix.
     *
     * @param filePath path to the text file
     * @param map      index of the map (e.g., 0 = first level)
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
                        GameLogger.error("Invalid number format at row " + row + ", col " + col);
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
     * Draws the entire map based on the player’s position and surrounding tiles.
     *
     * @param g2 the graphics context
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
