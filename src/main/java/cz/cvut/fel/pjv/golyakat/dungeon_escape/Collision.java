package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
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
                if (nextTopRow < 0) {
                    entity.collisionOn = true;
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

    public int checkObject(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] != null) {
                // Получаем текущую область сущности
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Получаем область объекта
                gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;

                // Проверяем коллизию для движения
                switch (entity.direction) {
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if (gp.obj[i].Collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if (gp.obj[i].Collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if (gp.obj[i].Collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                            if (gp.obj[i].Collision) {
                                entity.collisionOn = true;
                            }
                            if (player) {
                                index = i;
                            }
                        }
                        break;
                }

                // Сбрасываем области
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }
        return index;
    }

    public int checkObjectForInteraction(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.obj.length; i++) {
            if (gp.obj[i] != null) {
                // Получаем текущую область сущности
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Расширяем область объекта для взаимодействия
                gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x - gp.tileSize;
                gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y - gp.tileSize;
                gp.obj[i].solidArea.width += 2 * gp.tileSize;
                gp.obj[i].solidArea.height += 2 * gp.tileSize;

                // Проверяем пересечение
                if (entity.solidArea.intersects(gp.obj[i].solidArea)) {
                    if (player) {
                        index = i;
                    }
                }

                // Сбрасываем области
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
                gp.obj[i].solidArea.width = gp.tileSize; // Возвращаем исходный размер
                gp.obj[i].solidArea.height = gp.tileSize;
            }
        }
        return index;
    }

    public void handleObjectInteraction(Entity entity, int objectIndex) {
        if (objectIndex != 999 && gp.obj[objectIndex] != null) {
            String objName = gp.obj[objectIndex].name;
            if (objName.equals("DoorFront") && !((Object_DoorFront) gp.obj[objectIndex]).isOpen()) {
                ((Object_DoorFront) gp.obj[objectIndex]).interact();
                System.out.println("Interacting with front door");
            } else if (objName.equals("DoorSide") && !((Object_DoorSide) gp.obj[objectIndex]).isOpen()) {
                ((Object_DoorSide) gp.obj[objectIndex]).interact();
                System.out.println("Interacting with side door");
            } else if (objName.equals("small_chest")) {
                Object_Small_Chest chest = (Object_Small_Chest) gp.obj[objectIndex];
                gp.chestUI.openChest(chest);
                System.out.println("Interacting with chest");
            }
        }
    }
}