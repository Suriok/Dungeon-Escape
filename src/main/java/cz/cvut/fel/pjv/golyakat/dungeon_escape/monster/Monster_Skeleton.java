package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

/**
 * Třída {@code Monster_Skeleton} reprezentuje nepřátelskou jednotku typu kostlivec.
 * <p>
 * Kostlivec se pohybuje po mapě, detekuje hráče v dosahu a útočí, pokud se přiblíží.
 * Obsahuje logiku pohybu, kolizí, útoku i základní animace.
 * </p>
 */
public class Monster_Skeleton extends Entity {

    /** Odkaz na herní panel, který obsahuje mapu a hráče. */
    private gamePanel gp;

    /** Počítadlo, které určuje, kdy může změnit směr nebo začít novou akci. */
    public int actionLockCounter = 0;

    /** Maximální vzdálenost detekce hráče (v pixelech). */
    private static final int DETECTION_RANGE = 5 * 48;

    /** Vzdálenost, na kterou může monster zaútočit. */
    private static final int ATTACK_RANGE = 32;

    /** Počet snímků, které musí uběhnout mezi dvěma útoky. */
    private static final int ATTACK_COOLDOWN = 60;

    /** Počítadlo od posledního útoku (pro cooldown). */
    private int attackCounter = 0;

    /** Poškození způsobené útokem. */
    private static final int ATTACK_DAMAGE = 5;

    /**
     * Vytváří instanci kostlivce s výchozím nastavením a načte grafiku.
     *
     * @param gp herní panel
     */
    public Monster_Skeleton(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Skeleton";
        speed = 2;
        maxLife = 4;
        life = maxLife;
        direction = "down";

        solidArea = new Rectangle(3, 10, 20, 30);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    /**
     * Načte sprite obrázky kostlivce pro všechny směry pohybu.
     */
    public void getImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_up1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_up2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_down1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_down2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_right1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_right2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_left1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/skeleton_left2.png")));
        } catch (Exception e) {
            System.err.println("Chyba při načítání sprite obrázků kostlivce: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Určuje další akci kostlivce – sleduje hráče nebo se pohybuje náhodně.
     */
    public void setAction() {
        actionLockCounter++;

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= DETECTION_RANGE && !isDead) {
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? "right" : "left";
            } else {
                direction = dy > 0 ? "down" : "up";
            }
        } else {
            if (actionLockCounter >= 120) {
                Random random = new Random();
                int i = random.nextInt(100) + 1;
                direction = switch (i / 25) {
                    case 0 -> "up";
                    case 1 -> "down";
                    case 2 -> "left";
                    default -> "right";
                };
                actionLockCounter = 0;
            }
        }
    }

    /**
     * Aktualizuje chování kostlivce: pohyb, kolize, útok, animace a efekt smrti.
     */
    public void update() {
        if (isDead) {
            super.update(); // fade efekt
            return;
        }

        setAction();

        if (direction == null) direction = "down";

        int oldX = worldX;
        int oldY = worldY;

        switch (direction) {
            case "up" -> worldY -= speed;
            case "down" -> worldY += speed;
            case "left" -> worldX -= speed;
            case "right" -> worldX += speed;
        }

        collisionOn = false;
        gp.collisionChecker.checkTiles(this);
        if (collisionOn) {
            worldX = oldX;
            worldY = oldY;
            actionLockCounter = 120;
        }

        // Útok na hráče
        attackCounter++;
        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance <= ATTACK_RANGE && attackCounter >= ATTACK_COOLDOWN) {
            gp.player.receiveDamage(ATTACK_DAMAGE);
            attackCounter = 0;
            System.out.println(name + " zaútočil na hráče! HP hráče: " + gp.player.life);
        }

        // Animace
        spriteCounter++;
        if (spriteCounter > 15) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }

        // Smrt
        if (life <= 0) {
            isDead = true;
            fadeAlpha = 1.0f;
            fadeCounter = 0;
        }
    }

    /**
     * Vykreslí kostlivce pomocí aktuálního sprite obrázku a směru.
     *
     * @param g2d grafický kontext
     */
    @Override
    public void draw(Graphics2D g2d) {
        if (isDead && fadeAlpha <= 0) return;

        if (direction == null) direction = "down";

        BufferedImage imageToDraw = switch (direction) {
            case "up" -> (spriteNum == 1) ? up1 : up2;
            case "down" -> (spriteNum == 1) ? down1 : down2;
            case "left" -> (spriteNum == 1) ? left1 : left2;
            case "right" -> (spriteNum == 1) ? right1 : right2;
            default -> down1;
        };

        this.image = imageToDraw;

        if (!isDead) {
            super.draw(g2d);
        }
    }
}
