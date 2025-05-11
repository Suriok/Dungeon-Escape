package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

/**
 * Třída {@code Monster_Zombie} reprezentuje nepřátelskou entitu zombie.
 * <p>
 * Obsahuje logiku pohybu, útoku, detekce hráče, animace a vizuální efekt po smrti.
 * Dědí od třídy {@link Entity}, čímž získává základní schopnosti entity ve hře.
 * </p>
 */
public class Monster_Zombie extends Entity {

    /** Odkaz na hlavní herní panel. */
    private gamePanel gp;

    /** Počítadlo akcí pro řízení změn směru. */
    public int actionLockCounter = 0;

    /**
     * Maximální vzdálenost, na kterou může zombie detekovat hráče.
     * Jednotky odpovídají pixelům (5 tile = 240 px).
     */
    private static final int DETECTION_RANGE = 5 * 48;

    /** Vzdálenost, na kterou může zombie zaútočit na hráče. */
    private static final int ATTACK_RANGE = 32;

    /** Doba mezi jednotlivými útoky ve snímcích (při 60 FPS = 1s). */
    private static final int ATTACK_COOLDOWN = 60;

    /** Počítadlo cooldownu mezi útoky. */
    private int attackCounter = 4;

    /** Množství poškození, které zombie způsobí hráči. */
    private static final int ATTACK_DAMAGE = 5;

    /**
     * Konstruktor vytvoří novou zombie a nastaví výchozí atributy včetně obrázků a kolize.
     *
     * @param gp hlavní panel hry
     */
    public Monster_Zombie(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Zombie";
        speed = 2;
        maxLife = 5;
        life = maxLife;
        direction = "down";

        solidArea = new Rectangle();
        solidArea.x = 3;
        solidArea.y = 10;
        solidArea.width = 20;
        solidArea.height = 30;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    /**
     * Načte sprite obrázky zombie pro animace pohybu ve všech směrech.
     */
    public void getImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_up1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_up2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_front1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_front2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_left1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_left2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_rigth1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/zombie_right2.png")));
        } catch (Exception e) {
            GameLogger.error("Chyba při načítání sprite obrázků zombie: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Nastavuje akci zombie – buď se náhodně pohybuje, nebo pronásleduje hráče,
     * pokud je v dosahu detekce.
     */
    public void setAction() {
        actionLockCounter++;

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= DETECTION_RANGE && !isDead) {
            // Směr k hráči
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? "right" : "left";
            } else {
                direction = dy > 0 ? "down" : "up";
            }
        } else {
            // Náhodný pohyb
            if (actionLockCounter >= 120) {
                Random random = new Random();
                int i = random.nextInt(100) + 1;
                if (i <= 25) direction = "up";
                else if (i <= 50) direction = "down";
                else if (i <= 75) direction = "left";
                else direction = "right";
                actionLockCounter = 0;
            }
        }
    }

    /**
     * Aktualizuje stav zombie – pohyb, kolize, útok a animace.
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
            case "up"    -> worldY -= speed;
            case "down"  -> worldY += speed;
            case "left"  -> worldX -= speed;
            case "right" -> worldX += speed;
        }

        collisionOn = false;
        gp.collisionChecker.checkTiles(this);
        if (collisionOn) {
            worldX = oldX;
            worldY = oldY;
            actionLockCounter = 120;
        }

        attackCounter++;
        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= ATTACK_RANGE && attackCounter >= ATTACK_COOLDOWN) {
            gp.player.receiveDamage(ATTACK_DAMAGE);
            attackCounter = 0;
            GameLogger.info(name + " zaútočil na hráče! HP hráče: " + gp.player.life);
        }

        spriteCounter++;
        if (spriteCounter > 15) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }

        if (life <= 0) {
            isDead = true;
            fadeAlpha = 1.0f;
            fadeCounter = 0;
        }
    }

    /**
     * Vykreslí zombie pomocí správného sprite dle směru a fáze animace.
     *
     * @param g2d grafický kontext
     */
    @Override
    public void draw(Graphics2D g2d) {
        if (isDead && fadeAlpha <= 0) return;

        if (direction == null) direction = "down";

        BufferedImage imageToDraw = switch (direction) {
            case "up"    -> (spriteNum == 1) ? up1 : up2;
            case "down"  -> (spriteNum == 1) ? down1 : down2;
            case "left"  -> (spriteNum == 1) ? left1 : left2;
            case "right" -> (spriteNum == 1) ? right1 : right2;
            default      -> down1;
        };

        this.image = imageToDraw;

        if (!isDead) {
            super.draw(g2d);
        }
        // Fade & health bar handled elsewhere (e.g. MonsterUI)
    }
}
