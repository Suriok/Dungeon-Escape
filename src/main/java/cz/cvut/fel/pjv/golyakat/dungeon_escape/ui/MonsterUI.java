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
        if (monster.isDead && monster.fadeAlpha <= 0) return;

        int screenX = monster.worldX - gp.player.worldX + gp.player.screenX;
        int screenY = monster.worldY - gp.player.worldY + gp.player.screenY;

        boolean isBossGoblin = monster instanceof Boss_Goblin;
        boolean isBossEye = monster instanceof Boss_Eye;
        boolean isBoss = isBossGoblin || isBossEye;

        if (!monster.isDead) {
            float healthPercentage = (float) monster.life / monster.maxLife;
            int redValue = (int) (255 * healthPercentage);
            int grayValue = (int) (128 * (1 - healthPercentage));
            Color healthColor = new Color(redValue, grayValue, grayValue);

            int barWidth, barHeight, barX, barY;

            if (isBoss) {
                // Boss bar dimensions and position
                barWidth = 300;
                barHeight = 10;
                barX = (gp.screenWidth - barWidth) / 2;
                barY = 20;
            } else {
                // Regular monster bar dimensions and position
                barWidth = 30;
                barHeight = 5;
                int offsetY = -10;
                barX = screenX + (gp.tileSize - barWidth) / 2;
                barY = screenY + offsetY;
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
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.setColor(Color.WHITE);
                int textWidth = g2.getFontMetrics().stringWidth(bossName);
                int textX = (gp.screenWidth - textWidth) / 2;
                int textY = barY + barHeight + 25;
                g2.drawString(bossName, textX, textY);
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
