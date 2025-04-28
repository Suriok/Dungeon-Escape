package cz.cvut.fel.pjv.golyakat.dungeon_escape.bars;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class HealthBar extends GameObject {
    private BufferedImage fullHp, hit1, hit2, hit3, die;
    private gamePanel gp;
    private int maxHp = 8; // 4 hearts, 2 HP per heart
    private int currentHp;

    public HealthBar(gamePanel gp) {
        this.gp = gp;
        this.currentHp = maxHp;
        loadImages();
    }

    private void loadImages() {
        try {
            fullHp = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/Full_Hp.jpg"));
            hit1 = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/1_hit.jpg"));
            hit2 = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/2_hit.jpg"));
            hit3 = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/3_hit.jpg"));
            die = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/die.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(int playerHp) {
        this.currentHp = playerHp; // Direct mapping, as Player.life is 0–8
    }

    public void draw(Graphics2D g2) {
        int x = 10;
        int y = 10;
        int spacing = 1;

        // Each heart represents 2 HP
        for (int i = 0; i < maxHp / 2; i++) {
            int heartHp = Math.min(2, Math.max(0, currentHp - i * 2)); // HP for this heart
            BufferedImage heartImage = getHeartImage(heartHp);
            g2.drawImage(heartImage, x + (i * (gp.tileSize + spacing)), y, gp.tileSize, gp.tileSize, null);
        }
    }

    private BufferedImage getHeartImage(int heartHp) {
        // Map heart HP (0–2) to images
        if (heartHp >= 2) {
            return fullHp; // 2 HP
        } else if (heartHp >= 1.5) {
            return hit3; // 1.5–1.75 HP
        } else if (heartHp >= 1) {
            return hit2; // 1–1.25 HP
        } else if (heartHp > 0) {
            return hit1; // 0.25–0.75 HP
        } else {
            return die; // 0 HP
        }
    }
}