package cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.KeyHandler;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

// Třída hráče — rozšiřuje Entity a přidává logiku ovládání hráče
public class Player extends Entity {
    gamePanel gp; // Odkaz na herní panel
    KeyHandler keyH; // Ovladač klávesnice

    public final int screenX; // Pozice hráče na obrazovce (střed kamery)
    public final int screenY;

    // Obrázky pro animaci pohybu hráče
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;

    public String direction; // Aktuální směr pohybu
    public int spriteCounter = 0; // Počítadlo pro změnu snímku animace
    public int spriteNum = 1; // Číslo snímku animace (1 nebo 2)

    // Konstruktor hráče
    public Player(gamePanel gp, KeyHandler keyH) {
        super(gp);
        this.gp = gp;
        this.keyH = keyH;

        // Nastavení pozice hráče na obrazovce do středu
        screenX = gp.screenWidth / 2;
        screenY = gp.screenHeight / 2;

        // Nastavení hitboxu (kolizní oblasti) hráče
        solidArea = new Rectangle(12, 12, gp.tileSize - 24, gp.tileSize - 24);

        setDefaulteValues(); // Výchozí hodnoty (pozice, rychlost atd.)
        getPlayerImage(); // Načtení animací hráče
    }

    // Nastaví výchozí hodnoty hráče
    public void setDefaulteValues() {
        worldX = gp.tileSize * 15; // Výchozí X pozice ve světě
        worldY = gp.tileSize * 22; // Výchozí Y pozice ve světě
        speed = 4; // Rychlost pohybu hráče
        direction = "down"; // Výchozí směr

        maxLife = 4; // Maximální život hráče
        life = maxLife; // Aktuální život hráče
    }

    // Načítá obrázky hráče pro animaci pohybu
    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_up_1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_up_2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_down_1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_down_2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_right_1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_right_2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_left_1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_left_2.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Aktualizace stavu hráče každý snímek
    public void update() {
        // Pokud je stisknutá nějaká pohybová klávesa
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {

            // Uložíme starou pozici, abychom mohli vrátit v případě kolize
            int oldX = worldX;
            int oldY = worldY;

            // Nastavíme směr a posuneme hráče
            if (keyH.upPressed) {
                direction = "up";
                worldY -= speed;
            } else if (keyH.downPressed) {
                direction = "down";
                worldY += speed;
            } else if (keyH.leftPressed) {
                direction = "left";
                worldX -= speed;
            } else if (keyH.rightPressed) {
                direction = "right";
                worldX += speed;
            }

            // Kontrola kolizí s prostředím
            gp.collisionChecker.checkTiles(this);
            if (collisionOn) {
                // Pokud došlo ke kolizi, vrátíme se na původní pozici
                worldX = oldX;
                worldY = oldY;
            }

            // Počítadlo animace pohybu
            spriteCounter++;
            if (spriteCounter > 15) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    // Metoda pro vykreslení hráče
    @Override
    public void draw(Graphics2D g2d) {
        BufferedImage imageToDraw = null;

        // Vybereme správný snímek animace podle směru a aktuálního snímku
        switch (direction) {
            case "up":
                imageToDraw = (spriteNum == 1) ? up1 : up2;
                break;
            case "down":
                imageToDraw = (spriteNum == 1) ? down1 : down2;
                break;
            case "left":
                imageToDraw = (spriteNum == 1) ? left1 : left2;
                break;
            case "right":
                imageToDraw = (spriteNum == 1) ? right1 : right2;
                break;
        }

        // Nastavíme image, aby ji GameObject.draw mohl použít
        this.image = imageToDraw;

        // Voláme metodu draw ze základní třídy GameObject
        super.draw(g2d, gp);
    }
}
