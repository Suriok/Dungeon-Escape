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
        // Collision logic based on movement direction
        int checkRow1 = -1, checkRow2 = -1, checkCol1 = -1, checkCol2 = -1;

        switch (direction) {
            case "up" -> {
                int nextRow = (entityTopWorldY - entity.speed) / gp.tileSize;
                if (nextRow >= 0) {
                    checkRow1 = checkRow2 = nextRow;
                    checkCol1 = entityLeftCol;
                    checkCol2 = entityRightCol;
                }
            }
            case "down" -> {
                int nextRow = (entityBottomWorldY + entity.speed) / gp.tileSize;
                if (nextRow < gp.maxWorldRow) {
                    checkRow1 = checkRow2 = nextRow;
                    checkCol1 = entityLeftCol;
                    checkCol2 = entityRightCol;
                }
            }
            case "left" -> {
                int nextCol = (entityLeftWorldX - entity.speed) / gp.tileSize;
                if (nextCol >= 0) {
                    checkCol1 = checkCol2 = nextCol;
                    checkRow1 = entityTopRow;
                    checkRow2 = entityBottomRow;
                }
            }
            case "right" -> {
                int nextCol = (entityRightWorldX + entity.speed) / gp.tileSize;
                if (nextCol < gp.maxWorldCol) {
                    checkCol1 = checkCol2 = nextCol;
                    checkRow1 = entityTopRow;
                    checkRow2 = entityBottomRow;
                }
            }
        }

        // Check tile collisions if valid positions were set
        if (checkRow1 >= 0 && checkCol1 >= 0) {
            int tileNum1 = gp.tileH.mapTileNum[gp.currentMap][checkRow1][checkCol1];
            int tileNum2 = gp.tileH.mapTileNum[gp.currentMap][checkRow2][checkCol2];
            if (gp.tileH.tiles[tileNum1].collision || gp.tileH.tiles[tileNum2].collision) {
                entity.collisionOn = true;
            }
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
     * @param objectIndex the index of the object in the {@code gp.obj} array with which interaction occurs
     */
    public void handleObjectInteraction(int objectIndex) {
        if (objectIndex != 999 && gp.obj[gp.currentMap][objectIndex] != null) {
            GameObject obj = gp.obj[gp.currentMap][objectIndex];

            if (obj instanceof Object_DoorFront door && !door.isOpen()) {
                door.interact();
                GameLogger.info("Interacting with front door");
            } else if (obj instanceof Object_DoorSide door && !door.isOpen()) {
                door.interact();
                GameLogger.info("Interacting with side door");
            } else if (obj instanceof Object_Small_Chest chest) {
                gp.chestUI.openChest(chest);
                GameLogger.info("Interacting with chest");
            }
        }
    }
}