package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

// Třída reprezentující příšeru "Slime"
public class Monster_Slime extends Entity {
    private gamePanel gp; // Odkaz na herní panel
    public int actionLockCounter = 0; // Počítadlo pro změnu směru pohybu

    // Konstruktor příšery Slime
    public Monster_Slime(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Slime"; // Název příšery
        speed = 1; // Rychlost pohybu
        maxLife = 4; // Maximální životy
        life = maxLife; // Aktuální životy

        direction = "down"; // Výchozí směr

        // Nastavení kolizní oblasti (hitbox)
        solidArea = new Rectangle();
        solidArea.x = 3;
        solidArea.y = 10;
        solidArea.width = 20;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // Načtení obrázků pro animace příšery
        getImage();
    }

    // Metoda pro načtení obrázků příšery
    public void getImage() {
        try {
            // Načítáme jednotlivé snímky animace pro různé směry
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_2.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_3.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_2.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_3.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_2.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_3.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_2.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_3.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Logika pro nastavení směru pohybu příšery
    public void setAction() {
        actionLockCounter++;

        // Každých 120 cyklů (~2 sekundy při 60 FPS) změníme směr
        if (actionLockCounter >= 120) {
            Random random = new Random();
            int i = random.nextInt(100) + 1;

            // Náhodně vybereme nový směr
            if (i <= 25) {
                direction = "up";
            } else if (i <= 50) {
                direction = "down";
            } else if (i <= 75) {
                direction = "left";
            } else {
                direction = "right";
            }
            actionLockCounter = 0;
        }
    }

    // Aktualizace stavu příšery každý snímek
    public void update() {
        setAction(); // Nastavení směru

        // Kontrola, aby direction nebylo null
        if (direction == null) {
            direction = "down";
        }

        // Uložíme si starou pozici
        int oldX = worldX;
        int oldY = worldY;

        // Pohyb příšery podle směru
        switch (direction) {
            case "up":
                worldY -= speed;
                break;
            case "down":
                worldY += speed;
                break;
            case "left":
                worldX -= speed;
                break;
            case "right":
                worldX += speed;
                break;
            default:
                break;
        }

        // Kontrola kolize s dlaždicemi
        collisionOn = false;
        gp.collisionChecker.checkTiles(this);
        if (collisionOn) {
            // Pokud došlo ke kolizi, vrátíme se na původní pozici
            worldX = oldX;
            worldY = oldY;
            // Okamžitě vybereme nový směr
            actionLockCounter = 120;
        }

        // Animace příšery (střídání snímků)
        spriteCounter++;
        if (spriteCounter > 15) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }

    // Metoda pro vykreslení příšery na obrazovku
    @Override
    public void draw(Graphics2D g2d) {
        // Zajištění, že direction není null
        if (direction == null) {
            direction = "down";
        }

        BufferedImage imageToDraw = null;
        // Výběr správného obrázku podle směru a fáze animace
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
            default:
                imageToDraw = down1; // Výchozí obrázek
                break;
        }

        // Nastavíme obrázek, aby ho metoda draw správně vykreslila
        this.image = imageToDraw;

        // Voláme rodičovskou metodu draw pro vykreslení příšery
        super.draw(g2d, gp);
    }
}
