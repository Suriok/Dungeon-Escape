package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class Object_Small_Chest extends GameObject {
    private boolean isOpen;
    private BufferedImage inventoryImage;
    private boolean showInventory;

    public Object_Small_Chest() {
        name = "small_chest";
        Collision = true;
        isOpen = false;
        showInventory = false;

        try {
            // Load chest image
            java.io.InputStream chestStream = getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/small_chest.png");
            if (chestStream == null) {
                System.err.println("Error: Resource '/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/small_chest.png' not found.");
            } else {
                image = ImageIO.read(chestStream);
            }

            // Load inventory image
            java.io.InputStream inventoryStream = getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/case_inventory.png");
            if (inventoryStream == null) {
                System.err.println("Error: case_inventory.png not found.");
            } else {
                inventoryImage = ImageIO.read(inventoryStream);
                System.out.println("Successfully loaded case_inventory.png");
            }
        } catch (Exception e) {
            System.err.println("Error loading chest images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void interact() {
        if (!isOpen) {
            isOpen = true;
            showInventory = true;
        } else {
            isOpen = false;
            showInventory = false;
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isShowingInventory() {
        return showInventory;
    }

    public BufferedImage getInventoryImage() {
        return inventoryImage;
    }
}