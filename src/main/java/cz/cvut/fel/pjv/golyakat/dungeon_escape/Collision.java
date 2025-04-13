package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;

public class Collision {
    gamePanel gp;

    public Collision(gamePanel gp) {
        this.gp = gp;
    }

    public void checkTiles(Entity entity) {
        // Calculate the bounding box of the entity in world coords
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Determine which tile cells the entity occupies
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        // Default direction to "down" if null
        String direction = (entity.direction != null) ? entity.direction : "down";

        // Reset collision flag
        entity.collisionOn = false;

        switch (direction) {
            case "up":
                // Next row if moving 'speed' pixels up
                int nextTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                // If out of map bounds, collision
                if (nextTopRow < 0) {
                    entity.collisionOn = true;
                } else {
                    // Check the two top corners (leftCol, rightCol) at nextTopRow
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
                    entity.collisionOn = true;
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
                    entity.collisionOn = true;
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
                    entity.collisionOn = true;
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
