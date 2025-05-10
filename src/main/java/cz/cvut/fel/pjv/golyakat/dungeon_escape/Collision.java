package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

import java.awt.*;

public class Collision {
    gamePanel gp;

    public Collision(gamePanel gp) {
        this.gp = gp;
    }

    public void checkTiles(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        String direction = (entity.direction != null) ? entity.direction : "down";
        entity.collisionOn = false;

        switch (direction) {
            case "up":
                int nextTopRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                if (nextTopRow >= 0) {
                    int tileNumLeft = gp.tileH.mapTileNum[gp.currentMap][nextTopRow][entityLeftCol];
                    int tileNumRight = gp.tileH.mapTileNum[gp.currentMap][nextTopRow][entityRightCol];
                    if (gp.tileH.tiles[tileNumLeft].collision || gp.tileH.tiles[tileNumRight].collision) {
                        entity.collisionOn = true;
                    }
                }
                break;
            case "down":
                int nextBottomRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                if (nextBottomRow < gp.maxWorldRow) {
                    int tileNumLeft = gp.tileH.mapTileNum[gp.currentMap][nextBottomRow][entityLeftCol];
                    int tileNumRight = gp.tileH.mapTileNum[gp.currentMap][nextBottomRow][entityRightCol];
                    if (gp.tileH.tiles[tileNumLeft].collision || gp.tileH.tiles[tileNumRight].collision) {
                        entity.collisionOn = true;
                    }
                }
                break;
            case "left":
                int nextLeftCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                if (nextLeftCol >= 0) {
                    int tileNumTop = gp.tileH.mapTileNum[gp.currentMap][entityTopRow][nextLeftCol];
                    int tileNumBottom = gp.tileH.mapTileNum[gp.currentMap][entityBottomRow][nextLeftCol];
                    if (gp.tileH.tiles[tileNumTop].collision || gp.tileH.tiles[tileNumBottom].collision) {
                        entity.collisionOn = true;
                    }
                }
                break;
            case "right":
                int nextRightCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                if (nextRightCol < gp.maxWorldCol) {
                    int tileNumTop = gp.tileH.mapTileNum[gp.currentMap][entityTopRow][nextRightCol];
                    int tileNumBottom = gp.tileH.mapTileNum[gp.currentMap][entityBottomRow][nextRightCol];
                    if (gp.tileH.tiles[tileNumTop].collision || gp.tileH.tiles[tileNumBottom].collision) {
                        entity.collisionOn = true;
                    }
                }
                break;
        }

        // Check collision with objects (for non-player entities too)
        if (!entity.getClass().getSimpleName().equals("Player")) {
            checkObject(entity, false);
        }
    }

    public int checkObject(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] != null) {
                GameObject obj = gp.obj[gp.currentMap][i];

                // Set actual positions
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;
                obj.solidArea.x = obj.worldX + obj.solidArea.x;
                obj.solidArea.y = obj.worldY + obj.solidArea.y;

                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        break;
                }

                if (entity.solidArea.intersects(obj.solidArea)) {
                    if (obj.Collision) {
                        entity.collisionOn = true;
                    }
                    if (player) {
                        index = i;
                    }
                }

                // Reset
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;
            }
        }
        return index;
    }

    public int checkObjectForInteraction(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] != null) {
                GameObject obj = gp.obj[gp.currentMap][i];

                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                obj.solidArea.x = obj.worldX + obj.solidArea.x - gp.tileSize;
                obj.solidArea.y = obj.worldY + obj.solidArea.y - gp.tileSize;
                obj.solidArea.width += 2 * gp.tileSize;
                obj.solidArea.height += 2 * gp.tileSize;

                if (entity.solidArea.intersects(obj.solidArea)) {
                    if (player) {
                        index = i;
                    }
                }

                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;
                obj.solidArea.width = gp.tileSize;
                obj.solidArea.height = gp.tileSize;
            }
        }

        return index;
    }

    public void handleObjectInteraction(Entity entity, int objectIndex) {
        if (objectIndex != 999 && gp.obj[gp.currentMap][objectIndex] != null) {
            GameObject obj = gp.obj[gp.currentMap][objectIndex];
            String objName = obj.name;

            if (objName.equals("DoorFront") && !((Object_DoorFront) obj).isOpen()) {
                ((Object_DoorFront) obj).interact();
                System.out.println("Interacting with front door");
            } else if (objName.equals("DoorSide") && !((Object_DoorSide) obj).isOpen()) {
                ((Object_DoorSide) obj).interact();
                System.out.println("Interacting with side door");
            } else if (objName.equals("small_chest")) {
                Object_Small_Chest chest = (Object_Small_Chest) obj;
                gp.chestUI.openChest(chest);
                System.out.println("Interacting with chest");
            }
        }
    }
}
