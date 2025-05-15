package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;

/**
 * The {@code Monster_Slime} class represents a basic enemy of type Slime.
 * Inherits movement, collision, and combat logic from the base {@code Monster} class.
 */
public class Monster_Slime extends Monster {

    public Monster_Slime(gamePanel gp) {
        super(gp, "Slime", 1, 2, 3,

                new Rectangle(3, 10, 20, 30)); // collision box
    }

    @Override
    protected void loadImages() {
        try {
            // === Slime uses the same two frames for all directions ===
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_2.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_3.png")));

            down1 = up1;  down2 = up2;
            left1 = up1;  left2 = up2;
            right1 = up1; right2 = up2;

            GameLogger.info("Successfully loaded slime images");
        } catch (Exception e) {
            GameLogger.error("Error loading slime images: " + e.getMessage());
        }
    }
}
