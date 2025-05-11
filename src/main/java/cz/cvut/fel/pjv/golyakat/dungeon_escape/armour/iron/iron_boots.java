package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Třída {@code iron_boots} představuje železné boty, které poskytují základní ochranu nohou.
 * Obranná hodnota bot činí 2.
 *
 */
public class iron_boots extends GameObject implements Armor {
    private final float defensAmount;

    public iron_boots() {
        name = "iron_boots";
        defensAmount = 2;
        try {
            java.io.InputStream stream = getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/armour/iron_boots.png");
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