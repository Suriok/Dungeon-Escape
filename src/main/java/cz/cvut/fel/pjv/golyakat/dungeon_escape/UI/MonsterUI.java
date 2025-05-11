package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss.Boss_Eye;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss.Boss_Goblin;

import java.awt.*;

public class MonsterUI {
    private gamePanel gp;
    private final int regularBarWidth = 30;
    private final int regularBarHeight = 5;
    private final int offsetY = -10;
    private final int bossBarWidth = 300;
    private final int bossBarHeight = 10;
    private final int bossBarY = 20;

    public MonsterUI(gamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2, Entity monster) {
        if (monster.isDead && monster.fadeAlpha <= 0) return;

        int screenX = monster.worldX - gp.player.worldX + gp.player.screenX;
        int screenY = monster.worldY - gp.player.worldY + gp.player.screenY;

        boolean isVisible = screenX >= -gp.tileSize && screenX <= gp.screenWidth &&
                screenY >= -gp.tileSize && screenY <= gp.screenHeight;

        boolean isBossGoblin = monster instanceof Boss_Goblin;
        boolean isBossEye = monster instanceof Boss_Eye;

        if (!monster.isDead && isVisible) {
            if (isBossGoblin || isBossEye) {
                int bossBarX = (gp.screenWidth - bossBarWidth) / 2;

                // Background
                g2.setColor(Color.GRAY);
                g2.fillRect(bossBarX, bossBarY, bossBarWidth, bossBarHeight);

                // Health
                float healthPercentage = (float) monster.life / monster.maxLife;
                int redValue = (int) (255 * healthPercentage);
                int grayValue = (int) (128 * (1 - healthPercentage));
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
                // Regular monster health bar
                g2.setColor(Color.GRAY);
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

        // Fade out effect
        if (monster.isDead && monster.fadeAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, monster.fadeAlpha));
            g2.drawImage(monster.image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}
