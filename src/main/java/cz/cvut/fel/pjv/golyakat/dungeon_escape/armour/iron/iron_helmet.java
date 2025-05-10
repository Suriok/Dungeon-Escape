package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

public class iron_helmet extends GameObject implements Armor {
    private final float defensAmount;
    public SlotType getSlot() { return SlotType. HEAD; }

    public iron_helmet() {
        name = "iron_helmet";
        defensAmount = 3;
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/iron_helmet.png");
            if (stream == null) {
                System.err.println("Error: leather_pants.png not found in resources.");
            } else {
                image = ImageIO.read(stream);
                System.out.println("Successfully loaded leather_pants.png");
            }
        } catch (Exception e) {
            System.err.println("Error loading leather_pants image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}