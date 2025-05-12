package cz.cvut.fel.pjv.golyakat.dungeon_escape.bars;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * The {@code HealthBar} class serves to visually display the player's health
 * in the form of hearts on the screen.
 * <p>
 * Each heart represents 2 HP units and changes its appearance based on the player's current state.
 * Images of different damage phases are loaded during object construction.
 * </p>
 */
public class HealthBar extends GameObject {

    /**
     * Image of a full heart (2 HP).
     */
    private BufferedImage fullHp;

    /**
     * Image of a heart after light damage (~0.5 HP).
     */
    private BufferedImage hit1;

    /**
     * Image of a heart after medium damage (~1 HP).
     */
    private BufferedImage hit2;

    /**
     * Image of a heart after heavy damage (~1.5 HP).
     */
    private BufferedImage hit3;

    /**
     * Image of an empty (dead) heart (0 HP).
     */
    private BufferedImage die;

    /**
     * Reference to the main game panel, used for obtaining tile size etc.
     */
    private gamePanel gp;

    /**
     * Maximum number of health units (e.g., 8 = 4 hearts).
     */
    private final int maxHp = 8;

    /**
     * Current number of player's health units.
     */
    private int currentHp;

    /**
     * Creates a new health indicator and loads images of all heart states.
     *
     * @param gp game panel from which data is obtained
     */
    public HealthBar(gamePanel gp) {
        this.gp = gp;
        this.currentHp = maxHp;
        loadImages();
    }

    /**
     * Loads images of all heart variants from the resource directory.
     */
    private void loadImages() {
        try {
            fullHp = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/Full_Hp.jpg"));
            hit1 = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/1_hit.jpg"));
            hit2 = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/2_hit.jpg"));
            hit3 = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/3_hit.jpg"));
            die = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/die.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the value of current health that will be visually rendered.
     *
     * @param playerHp current number of player's HP (0-8)
     */
    public void update(int playerHp) {
        this.currentHp = playerHp;
    }

    /**
     * Renders the health indicator in the top-left corner of the screen.
     *
     * @param g2 graphics context to render into
     */
    public void draw(Graphics2D g2) {
        int x = 10;
        int y = 10;
        int spacing = 1;

        for (int i = 0; i < maxHp / 2; i++) {
            int heartHp = Math.min(2, Math.max(0, currentHp - i * 2));
            BufferedImage heartImage = getHeartImage(heartHp);
            g2.drawImage(heartImage, x + (i * (gp.tileSize + spacing)), y, gp.tileSize, gp.tileSize, null);
        }
    }

    /**
     * Returns the appropriate image for a given amount of HP in one heart (0-2).
     *
     * @param heartHp health within one heart (e.g., 1 = half)
     * @return image corresponding to the given heart state
     */
    private BufferedImage getHeartImage(int heartHp) {
        if (heartHp >= 2) {
            return fullHp;
        } else if (heartHp >= 1.5) {
            return hit3;
        } else if (heartHp >= 1) {
            return hit2;
        } else if (heartHp > 0) {
            return hit1;
        } else {
            return die;
        }
    }
}
