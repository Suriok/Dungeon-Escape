package cz.cvut.fel.pjv.golyakat.dungeon_escape.bars;


import java.awt.*;

/**
 * The {@code DefensBar} class serves to visually display the player's total defense.
 * <p>
 * The defense indicator appears as a horizontal bar that changes based on
 * the strength of equipped armor. The fill color is blue, the border is white.
 * </p>
 */
public class DefensBar {
    /**
     * Current player's defense value (0-10).
     */
    private float defense;

    /**
     * X coordinate of the top-left corner of the defense indicator.
     */
    final private int x;

    /**
     * Y coordinate of the top-left corner of the defense indicator.
     */
    final private int y;

    /**
     * Width of the defense indicator.
     */
    final private int barWidth;

    /**
     * Height of the defense indicator.
     */
    final private int barHeight;

    /**
     * Constructor initializes the defense indicator with default coordinates and dimensions.
     */
    public DefensBar() {
        this.x = 10;
        this.y = 80;
        this.barWidth = 200;
        this.barHeight = 10;
        this.defense = 0;
    }

    /**
     * Updates the player's defense value for rendering.
     *
     * @param defense current defense value (0-10)
     */
    public void update(float defense) {
        this.defense = defense;
    }

    /**
     * Renders the defense indicator on screen – background, fill, and value text.
     *
     * @param g2 graphics context
     */
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

    /**
     * Returns the vertical Y position of the top edge of the defense indicator.
     *
     * @return Y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the height of the defense indicator.
     *
     * @return height in pixels
     */
    public int getBarHeight() {
        return barHeight;
    }
}
