package cz.cvut.fel.pjv.golyakat.dungeon_escape.ui;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.boss.Boss_Eye;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.boss.Boss_Goblin;

import java.awt.*;

/**
 * The {@code MonsterUI} class handles the rendering of health bars above monsters
 * and their fade-out effect upon death.
 * <p>
 * It supports both regular monsters and special health bars for bosses.
 * </p>
 */
public class MonsterUI {

    /** Reference to the main game panel, which contains information about the player and the map. */
    final private gamePanel gp;

    // === Health bar constants ===
    private static final int BOSS_BAR_WIDTH = 300;
    private static final int BOSS_BAR_HEIGHT = 10;
    private static final int BOSS_BAR_Y = 20;
    private static final int REGULAR_BAR_WIDTH = 30;
    private static final int REGULAR_BAR_HEIGHT = 5;
    private static final int REGULAR_BAR_OFFSET_Y = -10;
    private static final int BOSS_NAME_FONT_SIZE = 20;
    private static final int BOSS_NAME_Y_OFFSET = 25;

    /**
     * Constructor creates an instance of {@code MonsterUI} for the given {@link gamePanel}.
     *
     * @param gp the main game panel
     */
    public MonsterUI(gamePanel gp) {
        this.gp = gp;
    }

    /**
     * Renders the health bar of the given monster based on its type and health.
     *
     * @param g2      the graphics context for rendering
     * @param monster the entity (monster) to be rendered
     */
    public void draw(Graphics2D g2, Entity monster) {
        if (monster.isDead) return;

        int screenX = monster.worldX - gp.player.worldX + gp.player.screenX;
        int screenY = monster.worldY - gp.player.worldY + gp.player.screenY;

        boolean isBossGoblin = monster instanceof Boss_Goblin;
        boolean isBossEye = monster instanceof Boss_Eye;
        boolean isBoss = isBossGoblin || isBossEye;

        float healthPercentage = (float) monster.life / monster.maxLife;
        int redValue = (int) (255 * healthPercentage);
        int grayValue = (int) (128 * (1 - healthPercentage));
        Color healthColor = new Color(redValue, grayValue, grayValue);

        int barWidth, barHeight, barX, barY;

        if (isBoss) {
            // === Use boss-specific constants for bar dimensions and position ===
            barWidth = BOSS_BAR_WIDTH;
            barHeight = BOSS_BAR_HEIGHT;
            barX = (gp.screenWidth - barWidth) / 2;
            barY = BOSS_BAR_Y;
        } else {
            // === Use regular monster-specific constants for bar dimensions and position ===
            barWidth = REGULAR_BAR_WIDTH;
            barHeight = REGULAR_BAR_HEIGHT;
            barX = screenX + (gp.tileSize - barWidth) / 2;
            barY = screenY + REGULAR_BAR_OFFSET_Y;
        }

        // === Background ===
        g2.setColor(Color.GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);

        // === Health fill ===
        g2.setColor(healthColor);
        int healthWidth = (int) (barWidth * healthPercentage);
        g2.fillRect(barX, barY, healthWidth, barHeight);

        // === Border ===
        g2.setColor(Color.WHITE);
        g2.drawRect(barX, barY, barWidth, barHeight);

        // === Boss name ===
        if (isBoss) {
            String bossName = isBossGoblin ? "Goblin" : "Eye";
            g2.setFont(new Font("Arial", Font.BOLD, BOSS_NAME_FONT_SIZE));
            g2.setColor(Color.WHITE);
            int textWidth = g2.getFontMetrics().stringWidth(bossName);
            int textX = (gp.screenWidth - textWidth) / 2;
            int textY = barY + barHeight + BOSS_NAME_Y_OFFSET;
            g2.drawString(bossName, textX, textY);
        }
    }
}