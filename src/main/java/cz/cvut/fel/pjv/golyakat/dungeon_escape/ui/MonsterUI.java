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
        // If the monster is dead and has fully faded, do nothing
        if (monster.isDead && monster.fadeAlpha <= 0) return;

        // Convert the monster's position to screen coordinates
        int screenX = monster.worldX - gp.player.worldX + gp.player.screenX;
        int screenY = monster.worldY - gp.player.worldY + gp.player.screenY;

        // Check if the monster is a boss
        boolean isBossGoblin = monster instanceof Boss_Goblin;
        boolean isBossEye = monster instanceof Boss_Eye;

        if (!monster.isDead) {
            if (isBossGoblin || isBossEye) {
                // === Boss health bar ===
                /** Width of the health bar for bosses. */
                int bossBarWidth = 300;
                int bossBarX = (gp.screenWidth - bossBarWidth) / 2;

                // Background
                g2.setColor(Color.GRAY);
                /** Distance of the boss health bar from the top edge of the screen. */
                int bossBarY = 20;
                /** Height of the health bar for bosses. */
                int bossBarHeight = 10;
                g2.fillRect(bossBarX, bossBarY, bossBarWidth, bossBarHeight);

                // Calculate health percentage
                float healthPercentage = (float) monster.life / monster.maxLife;
                int redValue = (int) (255 * healthPercentage);
                int grayValue = (int) (128 * (1 - healthPercentage));

                // Fill based on health
                g2.setColor(new Color(redValue, grayValue, grayValue));
                int healthWidth = (int) (bossBarWidth * healthPercentage);
                g2.fillRect(bossBarX, bossBarY, healthWidth, bossBarHeight);

                // Border
                g2.setColor(Color.WHITE);
                g2.drawRect(bossBarX, bossBarY, bossBarWidth, bossBarHeight);

                // Boss name
                String bossName = isBossGoblin ? "Goblin" : "Eye";
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.setColor(Color.WHITE);
                int textWidth = g2.getFontMetrics().stringWidth(bossName);
                int textX = (gp.screenWidth - textWidth) / 2;
                int textY = bossBarY + bossBarHeight + 25;
                g2.drawString(bossName, textX, textY);

            } else {
                // === Regular monster ===
                g2.setColor(Color.GRAY);
                /** Vertical offset of the health bar relative to the monster. */
                int offsetY = -10;
                /** Height of the health bar for a regular monster. */
                int regularBarHeight = 5;
                /** Width of the health bar for a regular monster. */
                int regularBarWidth = 30;
                g2.fillRect(screenX + (gp.tileSize - regularBarWidth) / 2, screenY + offsetY,
                        regularBarWidth, regularBarHeight);

                float healthPercentage = (float) monster.life / monster.maxLife;
                int redValue = (int) (255 * healthPercentage);
                int grayValue = (int) (128 * (1 - healthPercentage));

                g2.setColor(new Color(redValue, grayValue, grayValue));
                int healthWidth = (int) (regularBarWidth * healthPercentage);
                g2.fillRect(screenX + (gp.tileSize - regularBarWidth) / 2, screenY + offsetY,
                        healthWidth, regularBarHeight);

                g2.setColor(Color.WHITE);
                g2.drawRect(screenX + (gp.tileSize - regularBarWidth) / 2, screenY + offsetY,
                        regularBarWidth, regularBarHeight);
            }
        }

        // === Fade-out effect after death ===
        if (monster.isDead && monster.fadeAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, monster.fadeAlpha));
            g2.drawImage(monster.image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}