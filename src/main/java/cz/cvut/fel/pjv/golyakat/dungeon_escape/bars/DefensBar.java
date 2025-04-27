package cz.cvut.fel.pjv.golyakat.dungeon_escape.bars;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import java.awt.*;

public class DefensBar {
    private gamePanel gp;
    private float defense;
    private int x, y, barWidth, barHeight;

    public DefensBar(gamePanel gp) {
        this.gp = gp;
        this.x = 10;
        this.y = 80;
        this.barWidth = 200;
        this.barHeight = 10;
        this.defense = 0;
    }

    public void update(float defense) {
        this.defense = defense;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.GRAY);
        g2.fillRect(x, y, barWidth, barHeight);

        g2.setColor(Color.BLUE);
        int filledWidth = (int) (barWidth * (defense / 10.0f));
        g2.fillRect(x, y, filledWidth, barHeight);

        g2.setColor(Color.WHITE);
        g2.drawRect(x, y, barWidth, barHeight);

        g2.setFont(new Font("Arial", Font.PLAIN, 12));
        g2.drawString("Defense: " + defense, x, y - 5);
    }

    public int getY() {
        return y;
    }

    public int getBarHeight() {
        return barHeight;
    }
}