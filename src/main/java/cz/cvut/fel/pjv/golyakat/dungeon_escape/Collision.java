package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;

// Třída Collision se stará o detekci kolizí entit s dlaždicemi mapy
public class Collision {
    gamePanel gp; // Odkaz na herní panel (kvůli mapě, dlaždicím a parametrům světa)

    // Konstruktor
    public Collision(gamePanel gp) {
        this.gp = gp;
    }

    // Metoda pro kontrolu kolize entity s dlaždicemi mapy
    public void checkTiles(Entity entity) {
        // 1. Vypočítáme hranice hitboxu entity v souřadnicích světa
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // 2. Zjistíme, ve kterých buňkách mapy se entity nachází
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        // 3. Ověření směru entity — pokud je null, dáme "down" jako výchozí
        String direction = (entity.direction != null) ? entity.direction : "down";

        // 4. Na začátku nastavíme, že není kolize
        entity.collisionOn = false;

        // 5. Podle směru entity zkontrolujeme, zda narazí do kolizní dlaždice
        switch (direction) {

            case "up":
                int nextTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                if (nextTopRow < 0) {
                    entity.collisionOn = true; // Kolize s okrajem mapy
                } else {
                    int tileNumLeft = gp.tileH.mapTileNum[nextTopRow][entityLeftCol];
                    int tileNumRight = gp.tileH.mapTileNum[nextTopRow][entityRightCol];
                    if (gp.tileH.tiles[tileNumLeft].collision || gp.tileH.tiles[tileNumRight].collision) {
                        entity.collisionOn = true;
                    }
                }
                break;

            case "down":
                int nextBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                if (nextBottomRow >= gp.maxWorldRow) {
                    entity.collisionOn = true; // Kolize s okrajem mapy
                } else {
                    int tileNumLeft = gp.tileH.mapTileNum[nextBottomRow][entityLeftCol];
                    int tileNumRight = gp.tileH.mapTileNum[nextBottomRow][entityRightCol];
                    if (gp.tileH.tiles[tileNumLeft].collision || gp.tileH.tiles[tileNumRight].collision) {
                        entity.collisionOn = true;
                    }
                }
                break;

            case "left":
                int nextLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                if (nextLeftCol < 0) {
                    entity.collisionOn = true; // Kolize s levým okrajem mapy
                } else {
                    int tileNumTop = gp.tileH.mapTileNum[entityTopRow][nextLeftCol];
                    int tileNumBottom = gp.tileH.mapTileNum[entityBottomRow][nextLeftCol];
                    if (gp.tileH.tiles[tileNumTop].collision || gp.tileH.tiles[tileNumBottom].collision) {
                        entity.collisionOn = true;
                    }
                }
                break;

            case "right":
                int nextRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                if (nextRightCol >= gp.maxWorldCol) {
                    entity.collisionOn = true; // Kolize s pravým okrajem mapy
                } else {
                    int tileNumTop = gp.tileH.mapTileNum[entityTopRow][nextRightCol];
                    int tileNumBottom = gp.tileH.mapTileNum[entityBottomRow][nextRightCol];
                    if (gp.tileH.tiles[tileNumTop].collision || gp.tileH.tiles[tileNumBottom].collision) {
                        entity.collisionOn = true;
                    }
                }
                break;
        }
    }
}
