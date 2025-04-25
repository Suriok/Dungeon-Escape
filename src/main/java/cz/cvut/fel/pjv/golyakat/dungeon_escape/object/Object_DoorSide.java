package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Object_DoorSide extends GameObject {
    private BufferedImage closedImage;
    private BufferedImage openImage;
    private boolean isOpen;

    public Object_DoorSide() {
        name = "DoorSide";
        try {
            // Attempt to load closed door image
            BufferedImage tempClosed = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side.png"));
            if (tempClosed == null) {
                System.err.println("Failed to load sec.png");
                // Create a 1x1 transparent pixel as fallback
                tempClosed = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            closedImage = tempClosed;

            // Attempt to load open door image
            BufferedImage tempOpen = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side_open.png"));
            if (tempOpen == null) {
                System.err.println("Failed to load door_side_open.png");
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

        isOpen = false;
        Collision = true; // Door starts as solid
    }

    public void interact() {
        if (!isOpen) {
            isOpen = true;
            image = openImage;
            Collision = false; // Remove collision when door is open
            System.out.println("Side door opened");
        }
    }

    public boolean isOpen() {
        return isOpen;
    }
}