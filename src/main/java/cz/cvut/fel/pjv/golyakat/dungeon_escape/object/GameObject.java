package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The abstract class {@code GameObject} serves as a base for all interactive objects in the game.
 * <p>
 * Contains basic attributes such as images, position, collision area, and rendering logic.
 * Designed for inheritance – specific objects (chests, doors, crafting table) extend it.
 * </p>
 */
public abstract class GameObject {

    /**
     * Object's image (main appearance).
     */
    public BufferedImage image;
    /**
     * Object's name (used for identification).
     */
    public String name;

    /**
     * Flag determining whether the object causes collision (blocks passage).
     */
    public boolean Collision = true;

    /**
     * X-coordinate of the object in world space.
     */
    public int worldX;

    /**
     * Y-coordinate of the object in world space.
     */
    public int worldY;

    /**
     * Object's collision area – used for detecting contact with player or entities.
     */
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48);

    /**
     * Default X coordinate of the collision area.
     */
    public int solidAreaDefaultX = 0;

    /**
     * Default Y coordinate of the collision area.
     */
    public int solidAreaDefaultY = 0;

    protected final boolean isOpen = false;

    /**
     * Renders the object on screen if it's within the visible area relative to the player.
     *
     * @param g2d graphics context for rendering
     * @param gp  main game panel from which player position is obtained
     */
    public void draw(Graphics2D g2d, gamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {
            if (image != null) {
                g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            } else {
                GameLogger.error("Nelze vykreslit objekt '" + name + "': obrázek je null");
            }
        }
    }

    /**
     * Pokusí se odemknout objekt pomocí klíče nebo stříbrného klíče z inventáře hráče.
     *
     * @param gp          instance hlavního panelu hry (kvůli přístupu k hráči a loggeru)
     * @param requiresKey zda objekt vyžaduje klíč
     * @param unlockLogic kód pro odemknutí objektu, pokud je klíč úspěšně použit
     */
    public void tryUnlockWithKey(gamePanel gp, boolean requiresKey, Runnable unlockLogic) {
        if (!requiresKey) {
            unlockLogic.run();
            return;
        }

        boolean usedKey = gp.player.removeItemByName("Key");
        boolean usedSilver = !usedKey && gp.player.removeItemByName("SilverKey");

        if (usedKey || usedSilver) {
            GameLogger.info(name + " otevřen pomocí " + (usedKey ? "Key." : "SilverKey."));
            unlockLogic.run();
        } else {
            GameLogger.info("Dveře vyžadují klíč, ale žádný není v inventáři.");
        }
    }

    /**
     * Возвращает, открыт ли объект.
     *
     * @return {@code true}, если объект открыт
     */
    public boolean isOpen() {
        return isOpen;
    }

}
