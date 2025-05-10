package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

public class leather_helmet extends GameObject implements Armor {
    private final float defensAmount;
    public SlotType getSlot() { return SlotType.HEAD; }

    public leather_helmet() {
        name = "leather_helmet";
        defensAmount = 1;
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_helmet.png");
            if (stream == null) {
                System.err.println("Error: leather_helmet.png not found in resources.");
            } else {
                image = ImageIO.read(stream);
                System.out.println("Successfully loaded leather_helmet.png");
            }
        } catch (Exception e) {
            System.err.println("Error loading leather_helmet image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}