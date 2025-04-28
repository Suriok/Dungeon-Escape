package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Boss.Boss_Goblin;

import java.awt.*;

public class MonsterUI {
    private gamePanel gp;
    private int regularBarWidth = 30;
    private int regularBarHeight = 5;
    private int offsetY = -10; // Offset above the monster's head for regular monsters
    private int bossBarWidth = 300; // Boss health bar width
    private int bossBarHeight = 10; // Boss health bar height
    private int bossBarY = 20; // Position from the top of the screen

    public MonsterUI(gamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2, Entity monster) {
        // Skip drawing if monster is fully faded
        if (monster.isDead && monster.fadeAlpha <= 0) {
            return;
        }

        // Calculate screen position of the monster
        int screenX = monster.worldX - gp.player.worldX + gp.player.screenX;
        int screenY = monster.worldY - gp.player.worldY + gp.player.screenY;

        // Check if the monster is visible on the screen
        boolean isVisible = screenX >= -gp.tileSize && screenX <= gp.screenWidth &&
                screenY >= -gp.tileSize && screenY <= gp.screenHeight;

        // Determine if the monster is a Boss Goblin
        boolean isBossGoblin = monster instanceof Boss_Goblin;

        // Draw health bar
        if (!monster.isDead && isVisible) {
            if (isBossGoblin) {
                // Boss Goblin health bar at the top center of the screen
                int bossBarX = (gp.screenWidth - bossBarWidth) / 2; // Center the bar

                // Background (gray)
                g2.setColor(Color.GRAY);
                g2.fillRect(bossBarX, bossBarY, bossBarWidth, bossBarHeight);

                // Health (transition from red to gray based on health percentage)
                float healthPercentage = (float) monster.life / monster.maxLife;
                int redValue = (int) (255 * healthPercentage); // Red decreases as health decreases
                int grayValue = (int) (128 * (1 - healthPercentage)); // Gray increases as health decreases
                g2.setColor(new Color(redValue, grayValue, grayValue));
                int healthWidth = (int) (bossBarWidth * healthPercentage);
                g2.fillRect(bossBarX, bossBarY, healthWidth, bossBarHeight);

                // Border
                g2.setColor(Color.WHITE);
                g2.drawRect(bossBarX, bossBarY, bossBarWidth, bossBarHeight);

                // Draw "Goblin" text below the health bar
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.setColor(Color.WHITE);
                String bossName = "Goblin";
                int textWidth = g2.getFontMetrics().stringWidth(bossName);
                int textX = (gp.screenWidth - textWidth) / 2; // Center the text
                int textY = bossBarY + bossBarHeight + 25; // Position below the health bar
                g2.drawString(bossName, textX, textY);
            } else {
                // Regular monsters (Skeleton, Zombie, Slime) health bar above their head
                // Background (gray)
                g2.setColor(Color.GRAY);
                g2.fillRect(screenX + (gp.tileSize - regularBarWidth) / 2, screenY + offsetY, regularBarWidth, regularBarHeight);

                // Health (transition from red to gray based on health percentage)
                float healthPercentage = (float) monster.life / monster.maxLife;
                int redValue = (int) (255 * healthPercentage); // Red decreases as health decreases
                int grayValue = (int) (128 * (1 - healthPercentage)); // Gray increases as health decreases
                g2.setColor(new Color(redValue, grayValue, grayValue));
                int healthWidth = (int) (regularBarWidth * healthPercentage);
                g2.fillRect(screenX + (gp.tileSize - regularBarWidth) / 2, screenY + offsetY, healthWidth, regularBarHeight);

                // Border
                g2.setColor(Color.WHITE);
                g2.drawRect(screenX + (gp.tileSize - regularBarWidth) / 2, screenY + offsetY, regularBarWidth, regularBarHeight);
            }
        }

        // Apply fade effect if monster is dead
        if (monster.isDead && monster.fadeAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, monster.fadeAlpha));
            g2.drawImage(monster.image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Reset alpha
        }
    }
}