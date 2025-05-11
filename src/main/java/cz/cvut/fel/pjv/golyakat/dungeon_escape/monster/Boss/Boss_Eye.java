package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.Item_Key; // Import the correct Item_Key class

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Třída {@code Boss_Eye} reprezentuje jednoho z hlavních bossů ve hře – Oko.
 * <p>
 * Tento boss má vyšší zdraví, způsobuje silné poškození a sleduje hráče v daném dosahu.
 * Po porážce upouští klíč nezbytný pro další postup ve hře.
 * </p>
 */
public class Boss_Eye extends Entity {

    /** Odkaz na hlavní herní panel. */
    private gamePanel gp;

    /** Počítadlo blokující časté změny směru. */
    public int actionLockCounter = 0;

    /** Vzdálenost, na kterou boss detekuje hráče. */
    private static final int DETECTION_RANGE = 5 * 48;

    /** Vzdálenost, na kterou boss útočí na hráče. */
    private static final int ATTACK_RANGE = 48;

    /** Počet snímků, které musí uběhnout mezi útoky (cooldown). */
    private static final int ATTACK_COOLDOWN = 60;

    /** Interní počítadlo cooldownu útoků. */
    private int attackCounter = 0;

    /** Poškození způsobené útokem. */
    private static final int ATTACK_DAMAGE = 15;

    /** Příznak označující, že boss již upustil klíč (zabraňuje opakovanému dropu). */
    private boolean hasDroppedKey = false;

    /**
     * Konstruktor inicializuje bosse, nastavuje zdraví, rychlost, obrázky a kolize.
     *
     * @param gp odkaz na {@link gamePanel}
     */
    public Boss_Eye(gamePanel gp) {
        super(gp);
        this.gp = gp;

        name = "Boss_Eye";
        speed = 2;
        maxLife = 15;
        life = maxLife;
        direction = "down";

        solidArea = new Rectangle(3, 10, 20, 50);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        getImage();
    }

    /**
     * Načítá sprite obrázky pro různé směry pohybu bosse.
     */
    public void getImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Eye.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/eye_1.png")));
            down1 = up1;
            down2 = up2;
            left1 = up1;
            left2 = up2;
            right1 = up1;
            right2 = up2;
        } catch (Exception e) {
            GameLogger.error("Chyba při načítání sprite obrázků bosse Eye: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Určuje směr bosse podle polohy hráče nebo náhodně, pokud je hráč mimo dosah.
     */
    public void setAction() {
        actionLockCounter++;

        int dx = gp.player.worldX - worldX;
        int dy = gp.player.worldY - worldY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= DETECTION_RANGE && !isDead) {
            direction = (Math.abs(dx) > Math.abs(dy)) ? (dx > 0 ? "right" : "left") : (dy > 0 ? "down" : "up");
        } else if (actionLockCounter >= 120) {
            Random random = new Random();
            int i = random.nextInt(100) + 1;
            direction = (i <= 25) ? "up" : (i <= 50) ? "down" : (i <= 75) ? "left" : "right";
            actionLockCounter = 0;
        }
    }

    /**
     * Hlavní metoda aktualizace – volá se v každém snímku hry.
     * Zahrnuje pohyb, útok, kolize, animace a smrt bosse.
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

        if (life <= 0 && !isDead) {
            isDead = true;
            fadeAlpha = 1.0f;
            fadeCounter = 0;

            if (!hasDroppedKey) {
                ChestInventoryManager.ItemData keyItem = new ChestInventoryManager.ItemData("Key", 1);
                keyItem.setItem(new Item_Key());
                gp.player.addItem(keyItem);
                hasDroppedKey = true;
                GameLogger.info(name + " byl poražen a upustil klíč!");
            }
        }
    }

    /**
     * Vykreslí bosse Eye na obrazovku podle směru a stavu.
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

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        int scaledSize = gp.tileSize * 2;

        if (screenX > -scaledSize && screenX < gp.screenWidth &&
                screenY > -scaledSize && screenY < gp.screenHeight && !isDead) {
            g2d.drawImage(image, screenX, screenY, scaledSize, scaledSize, null);
        }
    }
}
