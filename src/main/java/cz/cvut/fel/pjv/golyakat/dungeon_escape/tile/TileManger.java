package cz.cvut.fel.pjv.golyakat.dungeon_escape.tile;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class TileManger {
    gamePanel gp;
    Tile[] tiles;
    int mapTileNum[][];

    public TileManger(gamePanel gp) {
        this.gp = gp;

        tiles = new Tile[10];
        mapTileNum = new int[gp.maxScreenRow][gp.maxScreenCol];

        getTileImage();
        loadMap();
    }

    public void getTileImage() {
        try {
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/floor_not_in_dungeon.png")));

            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/back_wall.png")));

            tiles[2] = new Tile();
            tiles[2].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/front_wall.png")));

            tiles[3] = new Tile();
            tiles[3].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/left_side_wall.png")));

            tiles[4] = new Tile();
            tiles[4].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/right_side_wall.png")));

            tiles[5] = new Tile();
            tiles[5].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/floor.png")));

            tiles[6] = new Tile();
            tiles[6].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/left_down_corner.png")));

            tiles[7] = new Tile();
            tiles[7].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/right_down_corner.png")));

            tiles[8] = new Tile();
            tiles[8].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/left_up_corner.png")));

            tiles[9] = new Tile();
            tiles[9].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/tileset/right_up_corner.png")));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // Class that load the map(Scan the map.txt)
    public void loadMap(){
        try {
            InputStream is = getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/maps/level1.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while(row < gp.maxScreenRow) {
                String line = br.readLine();
                if(line == null) break;  // Stop if we reach end of file

                String numbers[] = line.split(" "); // Split the line into numbers
                
                for(col = 0; col < gp.maxScreenCol && col < numbers.length; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[row][col] = num;
                }
                
                row++;
            }
            br.close(); //Close the buffer reader
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while(row < gp.maxScreenRow && col < gp.maxScreenCol) {
            int tileNum = mapTileNum[row][col];
            g2.drawImage(tiles[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
            col++;
            x += gp.tileSize;

            if(col == gp.maxScreenCol) {
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }
    }
}
