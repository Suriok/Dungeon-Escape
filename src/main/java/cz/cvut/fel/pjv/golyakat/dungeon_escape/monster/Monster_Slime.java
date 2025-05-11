package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

/**
 * Třída {@code Monster_Slime} reprezentuje nepřátelskou příšeru typu Slime.
 * <p>
 * Obsahuje logiku pro náhodný pohyb nebo sledování hráče, útok na hráče,
 * detekci kolizí a jednoduchou animaci pohybu.
 * </p>
 */
public class Monster_Slime extends Entity {

    /** Odkaz na hlavní herní panel. */
    private gamePanel gp;

    /** Počítadlo pro určení, kdy změnit akci nebo směr pohybu. */
    public int actionLockCounter = 0;

    /** Maximální vzdálenost, na kterou slime detekuje hráče (v pixelech). */
    private static final int DETECTION_RANGE = 5 * 48;

    /** Maximální vzdálenost útoku na hráče. */
    private static final int ATTACK_RANGE = 32;

    /** Počet snímků čekání mezi útoky (cooldown). */
    private static final int ATTACK_COOLDOWN = 60;

    /** Počítadlo pro cooldown útoků. */
    private int attackCounter = 0;

    /** Množství poškození, které slime způsobí hráči. */
    private static final int ATTACK_DAMAGE = 4;

    /**
     * Vytváří novou instanci Slime s výchozím nastavením.
     *
     * @param gp herní panel, do kterého slime patří
     */
    public Monster_Slime(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Slime";
        speed = 1;
        maxLife = 2;
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
     * Načte obrázky (sprite) pro jednotlivé směry pohybu a animace Slime.
     */
    public void getImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_2.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/monsters/slime_3.png")));
            down1 = up1;
            down2 = up2;
            left1 = up1;
            left2 = up2;
            right1 = up1;
            right2 = up2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Určuje chování příšery podle vzdálenosti od hráče.
     * <ul>
     *     <li>Pokud je hráč blízko, slime ho následuje.</li>
     *     <li>Jinak se pohybuje náhodně.</li>
     * </ul>
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
                if (i <= 25) direction = "up";
                else if (i <= 50) direction = "down";
                else if (i <= 75) direction = "left";
                else direction = "right";
                actionLockCounter = 0;
            }
        }
    }

    /**
     * Aktualizuje stav Slime každý snímek: pohyb, kolize, útok, animaci a kontrolu smrti.
     */
    public void update() {
        if (isDead) {
            super.update(); // Fade efekt
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
            System.out.println(name + " zaútočil na hráče! HP: " + gp.player.life);
        }

        // Animace
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
     * Vykreslí Slime na obrazovku pomocí správného obrázku podle směru a fáze animace.
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
