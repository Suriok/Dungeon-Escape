package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

import java.awt.*;

/**
 * Třída {@code Collision} zajišťuje detekci kolizí mezi entitami, dlaždicemi a objekty ve hře.
 * <p>
 * Obsahuje logiku pro kolize pohybu, interakce a nastavuje příznaky kolize
 * pro entity typu {@link Entity}.
 * </p>
 */
public class Collision {

    /**
     * Odkaz na hlavní herní panel {@link gamePanel}, ze kterého získáváme mapu, objekty a dlaždice.
     */
    gamePanel gp;

    /**
     * Vytváří novou instanci kolizního manažeru pro daný herní panel.
     *
     * @param gp hlavní instance hry (gamePanel)
     */
    public Collision(gamePanel gp) {
        this.gp = gp;
    }

    /**
     * Kontroluje, zda entita narazí do kolizních dlaždic v závislosti na směru pohybu.
     * <p>
     * Pokud kolize existuje, nastaví {@code entity.collisionOn = true}.
     * </p>
     *
     * @param entity entita, pro kterou detekujeme kolizi s dlaždicemi
     */
    public void checkTiles(Entity entity) {
        // Výpočet hraničních bodů solidní oblasti entity
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        // Přepočet na sloupce a řádky mapy
        int entityLeftCol = entityLeftWorldX / gp.tileSize;
        int entityRightCol = entityRightWorldX / gp.tileSize;
        int entityTopRow = entityTopWorldY / gp.tileSize;
        int entityBottomRow = entityBottomWorldY / gp.tileSize;

        String direction = (entity.direction != null) ? entity.direction : "down";
        entity.collisionOn = false;

        // Kolizní logika podle směru pohybu
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

        // U nehráče detekujeme kolize s objekty také
        if (!entity.getClass().getSimpleName().equals("Player")) {
            checkObject(entity, false);
        }
    }

    /**
     * Detekuje kolizi entity s objektem a případně vrátí index kolidujícího objektu.
     * <p>
     * Pokud parametr {@code player} je {@code true}, metoda vrací index kolidujícího objektu.
     * Jinak jen nastavuje příznak kolize.
     * </p>
     *
     * @param entity entita, pro kterou detekujeme kolizi
     * @param player zda se jedná o hráče (používá se pro interakci)
     * @return index objektu, se kterým došlo ke kolizi, nebo 999 pokud k žádné nedošlo
     */
    public int checkObject(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] != null) {
                GameObject obj = gp.obj[gp.currentMap][i];

                // Nastavení aktuálních pozic
                entity.solidArea.x = entity.worldX + entity.solidArea.x;
                entity.solidArea.y = entity.worldY + entity.solidArea.y;
                obj.solidArea.x = obj.worldX + obj.solidArea.x;
                obj.solidArea.y = obj.worldY + obj.solidArea.y;

                // Simulace pohybu
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

                // Reset pozic
                entity.solidArea.x = entity.solidAreaDefaultX;
                entity.solidArea.y = entity.solidAreaDefaultY;
                obj.solidArea.x = obj.solidAreaDefaultX;
                obj.solidArea.y = obj.solidAreaDefaultY;
            }
        }
        return index;
    }

    /**
     * Detekuje, zda se entita nachází v oblasti interakce některého objektu.
     * <p>
     * Na rozdíl od {@link #checkObject} rozšiřuje detekční oblast.
     * Používá se např. pro otevření truhly nebo dveří při stisknutí klávesy.
     * </p>
     *
     * @param entity entita, která interaguje
     * @param player zda jde o hráče
     * @return index objektu, se kterým lze interagovat, nebo 999
     */
    public int checkObjectForInteraction(Entity entity, boolean player) {
        int index = 999;

        for (int i = 0; i < gp.obj[gp.currentMap].length; i++) {
            if (gp.obj[gp.currentMap][i] != null) {
                GameObject obj = gp.obj[gp.currentMap][i];

                // Rozšíření kolizní oblasti objektu
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

                // Reset kolizní oblasti
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
     * Zajišťuje zpracování interakce hráče s objektem.
     * <p>
     * Např. otevření dveří, otevření truhly atd.
     * </p>
     *
     * @param entity entita (hráč), která interaguje
     * @param objectIndex index objektu v poli {@code gp.obj}, se kterým dochází k interakci
     */
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
