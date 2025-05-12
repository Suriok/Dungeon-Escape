package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Manages and renders the title screen and game-over UI, including buttons and logging toggle.
 */
public class TitleScreenUI {

    private final gamePanel gp;
    private BufferedImage background;
    private static final float BTN_ALPHA = 0.60f;
    private static final int BTN_BORDER = 2;
    private static final Font BTN_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 48);
    private final Rectangle loggingToggleBounds = new Rectangle(20, 0, 120, 25);

    private static class UIButton {
        Rectangle bounds;
        String text;
        boolean hovered = false;
        boolean pressed = false;

        UIButton(Rectangle b, String t) {
            this.bounds = b;
            this.text = t;
        }
    }

    private final List<UIButton> buttons = new ArrayList<>();
    private final List<UIButton> gameOverButtons = new ArrayList<>();

    /**
     * Constructs the title screen UI, initializes buttons and background.
     *
     * @param gp the main GamePanel instance
     */
    public TitleScreenUI(gamePanel gp) {
        this.gp = gp;
        loadBackground();

        int btnWidth = gp.tileSize * 8;
        int btnHeight = gp.tileSize * 2;
        int gap = gp.tileSize / 2;
        int centerX = gp.screenWidth / 2 - btnWidth / 2;
        int firstY = gp.tileSize * 4;

        buttons.add(new UIButton(new Rectangle(centerX, firstY, btnWidth, btnHeight), "Start Game"));
        buttons.add(new UIButton(new Rectangle(centerX, firstY + (btnHeight + gap), btnWidth, btnHeight), "Start Saved Game"));
        buttons.add(new UIButton(new Rectangle(centerX, firstY + 2 * (btnHeight + gap), btnWidth, btnHeight), "Exit"));

        // Game Over buttons (coordinates calculated in draw)
        gameOverButtons.add(new UIButton(new Rectangle(), "Try Again"));
        gameOverButtons.add(new UIButton(new Rectangle(), "Exit"));

        loggingToggleBounds.y = gp.screenHeight - 45;
    }

    /**
     * Loads the background image for the title screen, or creates a fallback.
     */
    private void loadBackground() {
        try {
            background = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/titel background.png")));
        } catch (IOException | IllegalArgumentException e) {
            background = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = background.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 2, 2);
            g.dispose();
        }
    }

    /**
     * Draws the title screen or game-over overlay depending on the current game state.
     *
     * @param g2 the Graphics2D context to draw on
     */
    public void draw(Graphics2D g2) {
        // draw background, title text, main buttons, and logging toggle
        g2.drawImage(background, 0, 0, gp.screenWidth, gp.screenHeight, null);

        // Title
        g2.setFont(TITLE_FONT);
        g2.setColor(Color.WHITE);
        String title = "Dungeon Escape";
        FontMetrics titleFM = g2.getFontMetrics();
        int titleX = (gp.screenWidth - titleFM.stringWidth(title)) / 2;
        int titleY = gp.tileSize * 3;
        g2.drawString(title, titleX, titleY);

        // Main menu buttons
        g2.setFont(BTN_FONT);
        for (UIButton btn : buttons) {
            float scale = btn.hovered ? 1.05f : 1.0f;
            Rectangle r = scale(btn.bounds, scale);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, BTN_ALPHA));
            g2.setColor(Color.BLACK);
            g2.fillRect(r.x, r.y, r.width, r.height);

            g2.setComposite(AlphaComposite.SrcOver);
            g2.setStroke(new BasicStroke(BTN_BORDER));
            g2.setColor(Color.WHITE);
            g2.drawRect(r.x, r.y, r.width, r.height);

            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D strRect = fm.getStringBounds(btn.text, g2);
            int tx = r.x + (r.width - (int) strRect.getWidth()) / 2;
            int ty = r.y + (r.height - (int) strRect.getHeight()) / 2 + fm.getAscent();
            g2.drawString(btn.text, tx, ty);
        }

        // Logging toggle in the bottom left
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(loggingToggleBounds.x, loggingToggleBounds.y, loggingToggleBounds.width, loggingToggleBounds.height);
        g2.setColor(Color.WHITE);
        g2.drawRect(loggingToggleBounds.x, loggingToggleBounds.y, loggingToggleBounds.width, loggingToggleBounds.height);
        String status = "Logging: " + (GameLogger.isEnabled() ? "ON" : "OFF");
        g2.drawString(status, loggingToggleBounds.x + 8, loggingToggleBounds.y + 17);

        if (gp.gameState == gp.gameOverState) {
            drawGameOverScreen(g2);
        }
    }

    /**
     * Draws the game-over overlay with retry and exit options.
     *
     * @param g2 the Graphics2D context to draw on
     */
    public void drawGameOverScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        int boxWidth = gp.tileSize * 10;
        int boxHeight = gp.tileSize * 9;
        int boxX = (gp.screenWidth - boxWidth) / 2;
        int boxY = (gp.screenHeight - boxHeight) / 2;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(boxX, boxY, boxWidth, boxHeight);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(boxX, boxY, boxWidth, boxHeight);

        String title = "GAME OVER";
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics titleFM = g2.getFontMetrics();
        int titleX = boxX + (boxWidth - titleFM.stringWidth(title)) / 2;
        int titleY = boxY + titleFM.getAscent() + 30;
        g2.drawString(title, titleX, titleY);

        // Draw individual options
        g2.setFont(new Font("Arial", Font.PLAIN, 28));
        FontMetrics optionFM = g2.getFontMetrics();
        int spacing = 20;
        int buttonWidth = boxWidth - 60;
        int buttonHeight = gp.tileSize;

        for (int i = 0; i < gameOverButtons.size(); i++) {
            UIButton b = gameOverButtons.get(i);
            int bx = boxX + 30;
            int by = titleY + 40 + i * (buttonHeight + spacing);
            b.bounds = new Rectangle(bx, by, buttonWidth, buttonHeight);

            g2.setColor(Color.BLACK);
            g2.fillRect(bx, by, buttonWidth, buttonHeight);
            g2.setColor(Color.WHITE);
            g2.drawRect(bx, by, buttonWidth, buttonHeight);

            int tx = bx + (buttonWidth - optionFM.stringWidth(b.text)) / 2;
            int ty = by + (buttonHeight - optionFM.getHeight()) / 2 + optionFM.getAscent();
            g2.drawString(b.text, tx, ty);
        }
    }

    /**
     * Scales a rectangle around its center by a given factor.
     *
     * @param src the original Rectangle
     * @param k   the scale factor
     * @return a new Rectangle scaled by k
     */
    private static Rectangle scale(Rectangle src, float k) {
        if (k == 1f) return src;
        int newW = Math.round(src.width * k);
        int newH = Math.round(src.height * k);
        int newX = src.x - (newW - src.width) / 2;
        int newY = src.y - (newH - src.height) / 2;
        return new Rectangle(newX, newY, newW, newH);
    }

    /**
     * Updates hover state of main menu buttons based on mouse movement.
     *
     * @param p the current mouse position
     */
    public void mouseMoved(Point p) {
        for (UIButton b : buttons) b.hovered = b.bounds.contains(p);
    }

    /**
     * Updates pressed state of main menu buttons on mouse press.
     *
     * @param p the mouse press position
     */
    public void mousePressed(Point p) {
        buttons.forEach(b -> b.pressed = b.bounds.contains(p));
    }

    /**
     * Handles mouse release events:
     * <ul>
     *   <li>Toggles logging when clicking the logging area</li>
     *   <li>Processes main menu button actions</li>
     *   <li>Delegates to game-over handler if in game-over state</li>
     * </ul>
     *
     * @param p the mouse release position
     */
    public void mouseReleased(Point p) {
        if (loggingToggleBounds.contains(p)) {
            GameLogger.setEnabled(!GameLogger.isEnabled());
            return;
        }

        if (gp.gameState == gp.gameOverState) {
            handleGameOverClick(p);
            return;
        }

        for (UIButton b : buttons) {
            boolean click = b.pressed && b.bounds.contains(p);
            b.pressed = false;
            if (click) {
                switch (b.text) {
                    case "Start Game" -> gp.startNewGame();
                    case "Start Saved Game" -> gp.loadSavedGame();
                    case "Exit" -> {
                        gp.saveGame();
                        Window w = SwingUtilities.getWindowAncestor(gp);
                        if (w != null) {
                            w.dispatchEvent(new java.awt.event.WindowEvent(w, java.awt.event.WindowEvent.WINDOW_CLOSING));
                        }
                    }
                }
            }
        }
    }

    /**
     * Processes clicks on the game-over screen buttons.
     *
     * @param p the mouse click position
     */
    public void handleGameOverClick(Point p) {
        for (UIButton b : gameOverButtons) {
            if (b.bounds.contains(p)) {
                switch (b.text) {
                    case "Try Again" -> gp.startNewGame();
                    case "Exit" -> System.exit(0);
                }
            }
        }
    }
}