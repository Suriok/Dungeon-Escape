package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * The {@code Object_DoorSide} class represents a side door in the game,
 * which can be opened either through interaction or unlocked with a key.
 * <p>
 * The door has a collision area, different images for open/closed states,
 * and may require a {@code Key} for unlocking.
 * </p>
 */
public class Object_DoorSide extends GameObject {
    private final gamePanel gp;

    /**
     * Flag indicating whether this door requires a key to open.
     */
    public final boolean requiresKey;

    /**
     * Flag indicating whether the door is currently open.
     */
    private boolean isOpen = false;

    /**
     * Image of the open door.
     */
    private BufferedImage openImage;

    /**
     * Constructor initializes the door, loads images and sets the default state.
     */
    public Object_DoorSide(gamePanel gp, boolean requiresKey) {
        this.gp = gp;
        name = "DoorSide";
        this.requiresKey = requiresKey;
        Collision = true;
        solidArea = new java.awt.Rectangle(0, 0, 48, 48); // Velikost tile
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        BufferedImage closedImage;
        try {
            // === Loading the closed door image ===
            BufferedImage tempClosed = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side.png")));
            if (tempClosed == null) {
                GameLogger.error("door_side.png nenalezen – používá se záložní prázdný obrázek.");
                tempClosed = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            closedImage = tempClosed;

            // === Loading the open door image ===
            BufferedImage tempOpen = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side_open.png")));
            if (tempOpen == null) {
                GameLogger.error("door_side_open.png nenalezen – použije se zavřený obrázek.");
                tempOpen = closedImage;
            }
            openImage = tempOpen;

            // === Default state - door closed ===
            image = closedImage;

        } catch (Exception e) {
            GameLogger.error("Chyba při načítání obrázků dveří: " + e.getMessage());
            closedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            openImage = closedImage;
            image = closedImage;
        }

        GameLogger.info("Object_DoorSide inicializován: requiresKey=" + requiresKey + ", isOpen=" + isOpen);
    }

    /**
     * Attempts to open the door through player interaction.
     * <p>
     * If the door doesn't require a key, it opens and removes collision.
     * If a key is required, it remains closed.
     * </p>
     */
    public void interact() {
        if (isOpen) return;

        tryUnlockWithKey(gp, requiresKey, this::unlock);
    }

    /**
     * Unlocks the door with a key - sets the open state and deactivates collision.
     */
    public void unlock() {
        isOpen = true;
        image = openImage;
        Collision = false;
        GameLogger.info("DoorSide byl odemčen pomocí klíče!");
    }
}
