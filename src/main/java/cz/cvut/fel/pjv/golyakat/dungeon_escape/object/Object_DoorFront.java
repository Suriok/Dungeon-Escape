package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Object_DoorFront extends GameObject {
    public boolean requiresKey = false; // Indicates if the door requires a key
    private boolean isOpen = false;
    private BufferedImage closedImage;
    private BufferedImage openImage;

    public Object_DoorFront() {
        name = "DoorFront";
        Collision = true; // Collidable when closed
        solidArea = new java.awt.Rectangle(0, 0, 48, 48); // Assuming tileSize = 48
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        try {
            // Load closed door image
            BufferedImage tempClosed = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_front.png"));
            if (tempClosed == null) {
                System.err.println("Failed to load door_front.png for Object_DoorFront");
                tempClosed = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            closedImage = tempClosed;

            // Load open door image (using door_side.png as specified)
            BufferedImage tempOpen = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side.png"));
            if (tempOpen == null) {
                System.err.println("Failed to load door_side.png for Object_DoorFront");
                tempOpen = closedImage;
            }
            openImage = tempOpen;

            image = closedImage; // Start with closed door
        } catch (Exception e) {
            System.err.println("Error loading DoorFront images: " + e.getMessage());
            e.printStackTrace();
            closedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            openImage = closedImage;
            image = closedImage;
        }
    }

    public void interact() {
        if (!requiresKey && !isOpen) {
            isOpen = true;
            image = openImage;
            Collision = false; // No longer collidable when open
            System.out.println("DoorFront opened!");
        } else if (requiresKey && !isOpen) {
            System.out.println("This door requires a Silver Key to open.");
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void unlock() {
        if (requiresKey && !isOpen) {
            isOpen = true;
            image = openImage;
            Collision = false;
            System.out.println("DoorFront unlocked and opened with a Silver Key!");
        }
    }
}