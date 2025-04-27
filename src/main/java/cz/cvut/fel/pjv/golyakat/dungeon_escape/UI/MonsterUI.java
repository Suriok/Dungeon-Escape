package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite.Entity;

import java.awt.*;

public class MonsterUI {
    private gamePanel gp;
    private int barWidth = 30;
    private int barHeight = 5;
    private int offsetY = -10; // Offset above the monster's head

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

        // Draw health bar only if monster is alive
        if (!monster.isDead) {
            // Background (gray)
            g2.setColor(Color.GRAY);
            g2.fillRect(screenX + (gp.tileSize - barWidth) / 2, screenY + offsetY, barWidth, barHeight);

            // Health (red)
            g2.setColor(Color.RED);
            int healthWidth = (int) (barWidth * ((float) monster.life / monster.maxLife));
            g2.fillRect(screenX + (gp.tileSize - barWidth) / 2, screenY + offsetY, healthWidth, barHeight);

            // Border
            g2.setColor(Color.WHITE);
            g2.drawRect(screenX + (gp.tileSize - barWidth) / 2, screenY + offsetY, barWidth, barHeight);
        }

        // Apply fade effect if monster is dead
        if (monster.isDead && monster.fadeAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, monster.fadeAlpha));
            g2.drawImage(monster.image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Reset alpha
        }
    }
}