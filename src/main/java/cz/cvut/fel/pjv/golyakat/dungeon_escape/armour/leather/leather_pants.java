package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Třída {@code leather_pants} představuje kožené kalhoty,
 * které chrání dolní část těla hráče.
 * <p>
 * Brnění má obrannou hodnotu 2 body.
 * Ikona je načítána z interního resource souboru.
 * </p>
 */

public class leather_pants extends GameObject implements Armor {
    private final float defensAmount;


    public leather_pants() {
        name = "leather_pants";
        defensAmount = 2;
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/leather_pants.png");
            if (stream == null) {
                GameLogger.error("Error: leather_pants.png not found in resources.");
            } else {
                image = ImageIO.read(stream);
                GameLogger.info("Successfully loaded leather_pants.png");
            }
        } catch (Exception e) {
            GameLogger.error("Error loading leather_pants image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public float getDefensAmount() {
        return defensAmount;
    }
}