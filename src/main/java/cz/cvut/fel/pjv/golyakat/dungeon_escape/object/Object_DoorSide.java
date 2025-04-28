package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Object_DoorSide extends GameObject {
    public boolean requiresKey = false; // New field to indicate if the door requires a key
    private boolean isOpen = false;
    private BufferedImage closedImage;
    private BufferedImage openImage;

    public Object_DoorSide() {
        name = "DoorSide";
        Collision = true; // Collidable when closed
        solidArea = new java.awt.Rectangle(0, 0, 48, 48); // Assuming tileSize = 48
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        try {
            // Attempt to load closed door image
            BufferedImage tempClosed = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side.png"));
            if (tempClosed == null) {
                System.err.println("Failed to load door_side.png for Object_DoorSide");
                // Create a 1x1 transparent pixel as fallback
                tempClosed = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            closedImage = tempClosed;

            // Attempt to load open door image
            BufferedImage tempOpen = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side_open.png"));
            if (tempOpen == null) {
                System.err.println("Failed to load door_side_open.png for Object_DoorSide");
                // Use closed image as fallback
                tempOpen = closedImage;
            }
            openImage = tempOpen;

            image = closedImage; // Start with closed door
        } catch (Exception e) {
            System.err.println("Error loading DoorSide images: " + e.getMessage());
            e.printStackTrace();
            // Create a 1x1 transparent pixel as fallback
            closedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            openImage = closedImage;
            image = closedImage;
        }
    }

    public void interact() {
        if (!requiresKey) {
            isOpen = true;
            image = openImage;
            Collision = false; // No longer collidable when open
            System.out.println("DoorSide opened!");
        } else {
            System.out.println("This door requires a key to open.");
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void unlock() {
        isOpen = true;
        image = openImage;
        Collision = false;
        System.out.println("DoorSide unlocked and opened with a key!");
    }
}