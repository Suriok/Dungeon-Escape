package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class TitleScreenUI {

    private final gamePanel gp;
    private BufferedImage background;

    // MAIN TITEL PANEL
    private static final float BTN_ALPHA = 0.60f;
    private static final int BTN_BORDER = 2;
    private static final Font BTN_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 48);




    private static class UIButton {
        Rectangle bounds;
        String text;
        boolean hovered = false;
        boolean pressed = false;

        UIButton(Rectangle b, String t) { this.bounds = b; this.text = t; }
    }

    private final List<UIButton> buttons = new ArrayList<>();

    public TitleScreenUI(gamePanel gp) {

        this.gp = gp;
        loadBackground();

        int btnWidth = gp.tileSize * 8;
        int btnHeight = gp.tileSize * 2;
        int gap = gp.tileSize / 2;
        int centerX = gp.screenWidth / 2 - btnWidth / 2;


        int firstY = gp.tileSize * 4;

        buttons.add(new UIButton(
                new Rectangle(centerX, firstY, btnWidth, btnHeight), "Start Game"));
        buttons.add(new UIButton(
                new Rectangle(centerX, firstY + (btnHeight + gap), btnWidth, btnHeight), "Start Saved Game"));
        buttons.add(new UIButton(
                new Rectangle(centerX, firstY + 2 * (btnHeight + gap), btnWidth, btnHeight), "Exit"));
    }


    private void loadBackground() {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/titel background.png"));
        } catch (IOException | IllegalArgumentException e) {
            background = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = background.createGraphics();
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, 2, 2);
            g.dispose();
        }
    }




    public void draw(Graphics2D g2) {

        g2.drawImage(background, 0, 0, gp.screenWidth, gp.screenHeight, null);


        g2.setFont(TITLE_FONT);
        g2.setColor(Color.WHITE);
        String title = "Dungeon Escape";
        FontMetrics titleFM = g2.getFontMetrics();
        int titleX = (gp.screenWidth - titleFM.stringWidth(title)) / 2;
        int titleY = gp.tileSize * 3;
        g2.drawString(title, titleX, titleY);


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
            int tx = r.x + (r.width  - (int)strRect.getWidth())  / 2;
            int ty = r.y + (r.height - (int)strRect.getHeight()) / 2 + fm.getAscent();
            g2.drawString(btn.text, tx, ty);
        }

        // GAME OVER
        if(gp.gameState == gp.gameOverState){
            drawGameOverScreen(g2);
        }

    }

    // GAME OVER
    public void drawGameOverScreen(Graphics2D g2) {
        // Полупрозрачное затемнение фона
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Размер и положение основного блока
        int boxWidth = gp.tileSize * 10;
        int boxHeight = gp.tileSize * 9; // ← немного уменьшен
        int boxX = (gp.screenWidth - boxWidth) / 2;
        int boxY = (gp.screenHeight - boxHeight) / 2;

        // Прямоугольник
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(boxX, boxY, boxWidth, boxHeight);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4));
        g2.drawRect(boxX, boxY, boxWidth, boxHeight);

        // Шрифты и строки
        String title = "GAME OVER";
        String[] options = {"Try Again", "Back to Menu", "Exit"};

        Font titleFont = new Font("Arial", Font.BOLD, 48);
        Font optionFont = new Font("Arial", Font.PLAIN, 28);

        g2.setFont(titleFont);
        FontMetrics titleFM = g2.getFontMetrics();
        int titleHeight = titleFM.getHeight();
        int titleWidth = titleFM.stringWidth(title);

        g2.setFont(optionFont);
        FontMetrics optionFM = g2.getFontMetrics();
        int optionHeight = optionFM.getHeight();

        int spacing = 10; // отступ между строками
        int totalContentHeight = titleHeight + spacing + options.length * optionHeight + (options.length - 1) * spacing;

        int contentStartY = boxY + (boxHeight - totalContentHeight) / 2;

        // Рисуем заголовок
        g2.setFont(titleFont);
        int titleX = boxX + (boxWidth - titleWidth) / 2;
        int titleY = contentStartY + titleFM.getAscent();
        g2.drawString(title, titleX, titleY);

        // Рисуем опции
        g2.setFont(optionFont);
        int optionY = titleY + spacing;

        for (int i = 0; i < options.length; i++) {
            optionY += optionHeight + spacing;
            String opt = options[i];
            int textW = optionFM.stringWidth(opt);
            int x = boxX + (boxWidth - textW) / 2;
            g2.drawString(opt, x, optionY);
        }
    }


    private static Rectangle scale(Rectangle src, float k) {
        if (k == 1f) return src;
        int newW = Math.round(src.width  * k);
        int newH = Math.round(src.height * k);
        int newX = src.x - (newW - src.width) / 2;
        int newY = src.y - (newH - src.height) / 2;
        return new Rectangle(newX, newY, newW, newH);
    }


    public void mouseMoved(Point p) {
        for (UIButton b : buttons) b.hovered = b.bounds.contains(p);
    }

    public void mousePressed(Point p) {
        buttons.forEach(b -> b.pressed = b.bounds.contains(p));

    }

    public void mouseReleased(Point p) {
        // game Over
        if (gp.gameState == gp.gameOverState) {
            int boxWidth = gp.tileSize * 10;
            int boxHeight = gp.tileSize * 9;
            int boxX = (gp.screenWidth - boxWidth) / 2;
            int boxY = (gp.screenHeight - boxHeight) / 2;

            // те же шрифты
            Font titleFont = new Font("Arial", Font.BOLD, 48);
            Font optionFont = new Font("Arial", Font.PLAIN, 28);

            FontMetrics titleFM = gp.getFontMetrics(titleFont);
            FontMetrics optionFM = gp.getFontMetrics(optionFont);

            int titleHeight = titleFM.getHeight();
            int optionHeight = optionFM.getHeight();
            int spacing = 10;
            int totalContentHeight = titleHeight + spacing + 3 * optionHeight + 2 * spacing;
            int contentStartY = boxY + (boxHeight - totalContentHeight) / 2;
            int titleY = contentStartY + titleFM.getAscent();
            int optionY = titleY + spacing;

            for (int i = 0; i < 3; i++) {
                optionY += optionHeight + spacing;
                Rectangle r = new Rectangle(boxX, optionY - optionHeight, boxWidth, optionHeight);
                if (r.contains(p)) {
                    switch (i) {
                        case 0 -> gp.startNewGame();         // Try Again
                        case 1 -> gp.gameState = gp.titleState;  // Back to Menu
                        case 2 -> System.exit(0);            // Exit
                    }
                }
            }
            return;
        }

        // Main Menu
        for (UIButton b : buttons) {
            boolean click = b.pressed && b.bounds.contains(p);
            b.pressed = false;
            if (click) {
                switch (b.text) {
                    case "Start Game" ->
                            gp.startNewGame();

                    case "Start Saved Game" ->
                            gp.loadSavedGame();

                    case "Exit" -> {
                        gp.saveGame();

                        Window w = SwingUtilities.getWindowAncestor(gp);
                        if (w != null) w.dispatchEvent(
                                new java.awt.event.WindowEvent(
                                        w, java.awt.event.WindowEvent.WINDOW_CLOSING));
                    }
                }
            }
        }
    }

    public void handleClick(Point p) {
        for (UIButton b : buttons) {               // buttons – это ваш список кнопок
            if (b.bounds.contains(p)) {
                switch (b.text) {                  // текст на кнопке
                    case "Start Game"       -> gp.startNewGame();
                    case "Start Saved Game" -> gp.loadSavedGame();
                    case "Exit"             -> System.exit(0);
                }
                break;
            }
        }
    }

    /* Если у вас есть отдельный экран Game Over – аналогичная обёртка: */
    public void handleGameOverClick(Point p) {
        handleClick(p);            // в моём примере действия одинаковые
    }


}
