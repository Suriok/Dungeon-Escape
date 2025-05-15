package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * The {@code Object_DoorFront} class represents a front-facing door in the game,
 * which can be opened either directly or with a special key - e.g., SilverKey.
 * <p>
 * Contains graphics for both closed and open states, collision logic, and player interaction support.
 * </p>
 */
public class Object_DoorFront extends GameObject {
    private final gamePanel gp;
    /**
     * Determines whether the door requires a key (e.g., SilverKey) to open.
     */
    public final boolean requiresKey;

    /**
     * Flag indicating whether the door is open.
     */
    private boolean isOpen = false;

    /**
     * Image of the open door.
     */
    private BufferedImage openImage;

    /**
     * Creates a new door instance, loads images and sets the default state (closed).
     */
    public Object_DoorFront(gamePanel gp, boolean requiresKey) {
        this.gp = gp;
        name = "DoorFront";
        this.requiresKey = requiresKey;
        Collision = true; // Collision enabled when closed
        solidArea = new java.awt.Rectangle(0, 0, 48, 48); // Assumption: tileSize = 48
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        BufferedImage closedImage;
        try {
            // === Loading the closed door image ===
            BufferedImage tempClosed = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_front.png")));
            if (tempClosed == null) {
                GameLogger.error("Nepodařilo se načíst door_front.png");
                tempClosed = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            closedImage = tempClosed;

            // === Loading the open door image ===
            BufferedImage tempOpen = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side.png")));
            if (tempOpen == null) {
                GameLogger.error("Nepodařilo se načíst door_side.png pro otevřené dveře");
                tempOpen = closedImage;
            }
            openImage = tempOpen;

            // === Default state - door closed ===
            image = closedImage;
        } catch (Exception e) {
            GameLogger.error("Chyba při načítání obrázků pro dveře: " + e.getMessage());
            closedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            openImage = closedImage;
            image = closedImage;
        }
    }


    /**
     * Handles player interaction with the door.
     * <ul>
     *     <li>If the door doesn't require a key, it opens immediately.</li>
     *     <li>If the door requires a key, it only notifies about the need to use it.</li>
     * </ul>
     */
    public void interact() {
        if (isOpen) return;

        tryUnlockWithKey(gp, requiresKey, this::unlock);
    }



    /**
     * Returns whether the door is open.
     *
     * @return {@code true} if the door is open
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Unlocks the door if it requires a key and is closed.
     * <p>
     * Sets the open door image and disables collision.
     * </p>
     */
    public void unlock() {
        isOpen = true;
        image = openImage;
        Collision = false;
        GameLogger.info("DoorFront byl odemčen pomocí klíče!");

    }
}
