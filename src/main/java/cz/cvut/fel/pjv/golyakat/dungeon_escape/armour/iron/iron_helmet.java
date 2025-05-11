package cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.Armor;

import javax.imageio.ImageIO;
import java.util.Objects;

/**
 * Třída {@code iron_helmet} reprezentuje železnou přilbu, která poskytuje střední úroveň ochrany pro hlavu hráče.
 * <p>
 *  Poskytuje hodnotu obrany 2.
 * </p>
 * <p>
 * Obrázek je načítán z resource složky a používá se pro vykreslení v inventáři i na postavě.
 * </p>
 */

public class iron_helmet extends GameObject implements Armor {
    private final float defensAmount;

    public iron_helmet() {
        name = "iron_helmet";
        defensAmount = 2;
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