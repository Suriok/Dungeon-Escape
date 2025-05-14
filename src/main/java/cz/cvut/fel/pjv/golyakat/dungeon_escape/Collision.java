package cz.cvut.fel.pjv.golyakat.dungeon_escape;


import cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

/**
 * The {@code Collision} class handles collision detection between entities, tiles, and objects in the game.
 * <p>
 * It contains logic for movement collisions, interactions, and sets collision flags
 * for entities of type {@link Entity}.
 * </p>
 */
public class Collision {

    /**
     * Reference to the main game panel {@link gamePanel}, from which we obtain the map, objects, and tiles.
     */
    final gamePanel gp;

    /**
     * Creates a new instance of the collision manager for the given game panel.
     *
     * @param gp the main game instance (gamePanel)
     */
    public Collision(gamePanel gp) {
        this.gp = gp;
    }

    /**
     * Checks if an entity collides with collision-enabled tiles depending on the movement direction.
     * <p>
     * If a collision exists, sets {@code entity.collisionOn = true}.
     * </p>
     *
     * @param entity the entity for which we are detecting collisions with tiles
     */
    public void checkTiles(Entity entity) {
        // Calculate the boundary points of the entity's solid area
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Convert to map columns and rows
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        String direction = (entity.direction != null) ? entity.direction : "down";
        entity.collisionOn = false;

        // Collision logic based on movement direction
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

        // For non-player entities, also detect collisions with objects
        if (!entity.getClass().getSimpleName().equals("Player")) {
            checkObject(entity);
        }
    }

    /**
     * Detects collisions between an entity and an object and optionally returns the index of the colliding object.
     * <p>
     * If the {@code player} parameter is {@code true}, the method returns the index of the colliding object.
     * Otherwise, it only sets the collision flag.
     * </p>
     *
     * @param entity the entity for which we are detecting collisions
     */
    public void checkObject(Entity entity) {
        int index = 999;

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] != null) {
                GameObject obj = gp.obj[gp.currentMap][i];

                // Set current positions
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;
                obj.solidArea.x = obj.worldX + obj.solidArea.x;
                obj.solidArea.y = obj.worldY + obj.solidArea.y;

                // Simulate movement
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
                }

                // Reset positions
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;
            }
        }
    }

    /**
     * Detects whether an entity is within the interaction range of an object.
     * <p>
     * Unlike {@link #checkObject}, this method expands the detection area.
     * It is used, for example, to open chests or doors when a key is pressed.
     * </p>
     *
     * @param entity the interacting entity
     * @param player whether it is the player
     * @return the index of the object that can be interacted with, or 999
     */
    public int checkObjectForInteraction(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] != null) {
                GameObject obj = gp.obj[gp.currentMap][i];

                // Expand the object's collision area
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

                // Reset collision area
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

    /**
     * Handles the player's interaction with an object.
     * <p>
     * For example, opening doors, opening chests, etc.
     * </p>
     *
     * @param entity the entity (player) that is interacting
     * @param objectIndex the index of the object in the {@code gp.obj} array with which interaction occurs
     */
    public void handleObjectInteraction(Entity entity, int objectIndex) {
        if (objectIndex != 999 && gp.obj[gp.currentMap][objectIndex] != null) {
            GameObject obj = gp.obj[gp.currentMap][objectIndex];
            String objName = obj.name;

            if (objName.equals("DoorFront") && !((Object_DoorFront) obj).isOpen()) {
                ((Object_DoorFront) obj).interact();
                GameLogger.info("Interacting with front door");
            } else if (objName.equals("DoorSide") && !((Object_DoorSide) obj).isOpen()) {
                ((Object_DoorSide) obj).interact();
                GameLogger.info("Interacting with side door");
            } else if (objName.equals("small_chest")) {
                Object_Small_Chest chest = (Object_Small_Chest) obj;
                gp.chestUI.openChest(chest);
                GameLogger.info("Interacting with chest");
            }
        }
    }
}