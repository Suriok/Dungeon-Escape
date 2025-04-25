package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;

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

    public int checkObject(Entity entity, boolean player) {
        int index = 999; // Check if player hit any object if yes return 999, basically a flag

        for(int i = 0; i < gp.obj.length; i++){ // Scan the GameObject class
            if(gp.obj[i] != null){
                // Get entity's solid area position
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;

                // Get the object's position
                gp.obj[i].solidArea.x = gp.obj[i].worldX + gp.obj[i].solidArea.x;
                gp.obj[i].solidArea.y = gp.obj[i].worldY + gp.obj[i].solidArea.y;

                switch(entity.direction){
                    case "up":
                        entity.solidArea.y -= entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){//Check if player and object are touching
                            if(gp.obj[i].Collision){
                                entity.collisionOn = true;
                            }
                            if(player){
                                index = i;
                            }
                        }
                        break;
                    case "down":
                        entity.solidArea.y += entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){//Check if player and object are touching
                            if(gp.obj[i].Collision){
                                entity.collisionOn = true;
                            }
                            if(player){
                                index = i;
                            }
                        }
                        break;
                    case "left":
                        entity.solidArea.x -= entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].Collision){
                                entity.collisionOn = true;
                            }
                            if(player){
                                index = i;
                            }
                        }
                        break;
                    case "right":
                        entity.solidArea.x += entity.speed;
                        if(entity.solidArea.intersects(gp.obj[i].solidArea)){
                            if(gp.obj[i].Collision){
                                entity.collisionOn = true;
                            }
                            if(player){
                                index = i;
                            }
                        }
                        break;
                }

                // Reset solid area positions
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                gp.obj[i].solidArea.x = gp.obj[i].solidAreaDefaultX;
                gp.obj[i].solidArea.y = gp.obj[i].solidAreaDefaultY;
            }
        }
        return index;
    }

    // New method to handle door interaction
    public void handleDoorInteraction(Entity entity, int objectIndex) {
        if (objectIndex != 999 && gp.obj[objectIndex] != null) {
            String objName = gp.obj[objectIndex].name;
            if (objName.equals("DoorFront") || objName.equals("DoorSide")) {
                if (gp.obj[objectIndex] instanceof Object_DoorFront) {
                    ((Object_DoorFront) gp.obj[objectIndex]).interact();
                    System.out.println("Interacting with front door");
                } else if (gp.obj[objectIndex] instanceof Object_DoorSide) {
                    ((Object_DoorSide) gp.obj[objectIndex]).interact();
                    System.out.println("Interacting with side door");
                }
            }
        }
    }
}