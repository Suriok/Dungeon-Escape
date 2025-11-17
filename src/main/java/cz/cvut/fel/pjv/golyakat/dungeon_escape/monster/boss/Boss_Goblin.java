package cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.boss;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.Objects;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.AlphaComposite;


/**
 * The {@code Boss_Goblin} class represents the main Goblin-type boss in the game.
 * The Goblin has high HP, tracks the player, and drops a key upon death.
 */
public class Boss_Goblin extends Monster {

    private boolean keyDropped = false;

    public Boss_Goblin(gamePanel gp) {
        super(gp, "Boss Goblin", 2, 6, 6,

                new Rectangle(3, 10, 20, 50));// collision box
    }

    /**
     * Loads sprite images for the goblin based on direction and animation.
     */
    @Override
    protected void loadImages() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/Boss/Goblin_1.png")));

            // All directions use same frames
            down1 = up1;  down2 = up2;
            left1 = up1;  left2 = up2;
            right1 = up1; right2 = up2;
        } catch (Exception e) {
            GameLogger.error("Error loading goblin sprites: " + e.getMessage());
        }
    }

    /**
     * Called once after boss death – drops a key for progression.
     */
    @Override
    protected void onDeath() {
        if (keyDropped) return;

        ChestInventoryManager.ItemData key = new ChestInventoryManager.ItemData("Key", 1);

        gp.player.addItem(key);
        keyDropped = true;

        GameLogger.info(name + " dropped a key!");
    }

    /**
     * Рисует босса на экране с увеличенным размером.
     * Этот метод переопределяет стандартный метод draw() из Monster/Entity.
     */
    @Override
    public void draw(Graphics2D g2d) {

        // 1. Выбираем правильный спрайт для анимации
        BufferedImage image = null;

        // Monster.update() обновляет spriteNum
        // loadImages() загружает up1 и up2
        if (spriteNum == 1) {
            image = up1;
        } else {
            image = up2;
        }

        // 2. Рассчитываем позицию на экране (стандартная логика Entity)
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // 3. ЗАДАЕМ НОВЫЙ РАЗМЕР (+3 пикселя)
        int drawWidth = gp.tileSize + 24;
        int drawHeight = gp.tileSize + 24;

        // 4. (Опционально) Немного сдвигаем, чтобы увеличение шло из центра
        // (3 / 2 = 1 пиксель)
        int drawX = screenX - 1;
        int drawY = screenY - 1;

        // 5. Рисуем увеличенное изображение
        if (image != null) {
            g2d.drawImage(image, drawX, drawY, drawWidth, drawHeight, null);
        }
    }

}
