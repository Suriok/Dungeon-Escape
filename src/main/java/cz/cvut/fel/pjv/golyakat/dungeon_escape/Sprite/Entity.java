package cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Třída {@code Entity} slouží jako základní nadtřída pro všechny herní entity,
 * které se pohybují a interagují – včetně hráče a monster.
 * <p>
 * Obsahuje atributy pro pohyb, kolize, zdraví, útok i animace.
 * </p>
 */
public class Entity extends GameObject {

    /** Odkaz na hlavní herní panel. */
    protected gamePanel gp;

    /** Rychlost pohybu entity (v pixelech na update). */
    public int speed;

    /** Sprite obrázky pro pohyb ve čtyřech směrech (dva snímky na směr). */
    public BufferedImage up1, up2, down1, down2, right1, right2, left1, left2;

    /** Směr pohybu ("up", "down", "left", "right"). */
    public String direction = "down";

    /** Počítadlo snímků pro animaci. */
    public int spriteCounter = 0;

    /** Číslo sprite snímku, který se zrovna vykresluje (1 nebo 2). */
    public int spriteNum = 1;

    /**
     * Obdélník reprezentující kolizní oblast entity.
     * Slouží k detekci kontaktu s prostředím a objekty.
     */
    public Rectangle solidArea;

    /** Výchozí X souřadnice kolizní oblasti. */
    public int solidAreaDefaultX;

    /** Výchozí Y souřadnice kolizní oblasti. */
    public int solidAreaDefaultY;

    /** Příznak, zda je v aktuálním snímku detekována kolize. */
    public boolean collisionOn = false;

    /** Maximální počet životů entity. */
    public int maxLife;

    /** Aktuální počet životů entity. */
    public int life;

    // === Fade-out efekt při smrti ===

    /** Příznak, zda je entita mrtvá. */
    public boolean isDead = false;

    /** Průhlednost entity při zániku (0.0 až 1.0). */
    public float fadeAlpha = 1.0f;

    /** Počítadlo trvání fade efektu. */
    public int fadeCounter = 0;

    /** Maximální počet snímků pro fade efekt. */
    public static final int FADE_DURATION = 60;

    // === Parametry útoku monster ===

    /** Poškození způsobené útokem této entity. */
    protected int attackDamage = 1;

    /** Maximální vzdálenost, na kterou může entita zaútočit. */
    protected int attackRange = 48;

    /** Počet snímků, po kterých může entita znovu zaútočit. */
    protected int attackCooldown = 60;

    /** Počítadlo od posledního útoku. */
    protected int attackCounter = 0;

    /**
     * Konstruktor základní entity.
     *
     * @param gp instance hlavního panelu
     */
    public Entity(gamePanel gp) {
        this.gp = gp;
        solidArea = new Rectangle();
    }

    /**
     * Aktualizuje stav entity – útok, pohyb, případně fade efekt.
     */
    public void update() {
        if (!isDead) {
            attackCounter++;

            // Pokus o útok na hráče
            if (attackCounter >= attackCooldown) {
                double distance = Math.sqrt(Math.pow(gp.player.worldX - worldX, 2)
                        + Math.pow(gp.player.worldY - worldY, 2));
                if (distance <= attackRange) {
                    attack();
                    attackCounter = 0;
                }
            }

            // (Případně zde může být pohyb AI monstra)
        }

        // Fade efekt po smrti
        if (isDead && fadeAlpha > 0) {
            fadeCounter++;
            fadeAlpha = 1.0f - ((float) fadeCounter / FADE_DURATION);
            if (fadeAlpha < 0) {
                fadeAlpha = 0;
            }
        }
    }

    /**
     * Provede útok entity – standardně útočí na hráče.
     */
    public void attack() {
        GameLogger.info("DEBUG: " + name + " útočí na hráče s poškozením " + attackDamage);
        gp.player.receiveDamage(attackDamage);
    }

    /**
     * Vykreslí entitu na základě její aktuální pozice a stavu.
     *
     * @param g2d grafický kontext
     */
    public void draw(Graphics2D g2d) {
        super.draw(g2d, gp);
    }
}
