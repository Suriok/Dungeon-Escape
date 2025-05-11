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

/**
 * Třída {@code TitleScreenUI} slouží k vykreslení úvodní obrazovky a obrazovky Game Over.
 * <p>
 * Obsahuje logiku pro vykreslení pozadí, nadpisu, tlačítek a jejich interakci.
 * </p>
 */
public class TitleScreenUI {

    /** Odkaz na hlavní panel hry, ze kterého získáváme velikosti, stav hry atd. */
    private final gamePanel gp;

    /** Obrázek pozadí hlavního menu */
    private BufferedImage background;

    /** Průhlednost pozadí tlačítek */
    private static final float BTN_ALPHA = 0.60f;

    /** Tloušťka rámečku tlačítka */
    private static final int BTN_BORDER = 2;

    /** Font používaný pro text tlačítek */
    private static final Font BTN_FONT = new Font("Arial", Font.BOLD, 24);

    /** Font používaný pro hlavní nadpis hry */
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 48);

    /**
     * Vnitřní třída reprezentující jedno tlačítko na obrazovce.
     */
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

    /** Seznam všech tlačítek na obrazovce */
    private final List<UIButton> buttons = new ArrayList<>();

    /**
     * Vytvoří novou úvodní obrazovku s tlačítky pro spuštění hry.
     *
     * @param gp hlavní instance hry
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
    }

    /**
     * Načte pozadí pro titulní obrazovku.
     * <p>
     * Pokud se nepodaří načíst obrázek, vytvoří se jednoduchý černý obdélník jako záloha.
     * </p>
     */
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

    /**
     * Vykreslí hlavní titulní obrazovku včetně pozadí, nadpisu a všech tlačítek.
     *
     * @param g2 grafický kontext
     */
    public void draw(Graphics2D g2) {
        g2.drawImage(background, 0, 0, gp.screenWidth, gp.screenHeight, null);

        // Titulek
        g2.setFont(TITLE_FONT);
        g2.setColor(Color.WHITE);
        String title = "Dungeon Escape";
        FontMetrics titleFM = g2.getFontMetrics();
        int titleX = (gp.screenWidth - titleFM.stringWidth(title)) / 2;
        int titleY = gp.tileSize * 3;
        g2.drawString(title, titleX, titleY);

        // Tlačítka
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

        if (gp.gameState == gp.gameOverState) {
            drawGameOverScreen(g2);
        }
    }

    /**
     * Vykreslí obrazovku Game Over s možnostmi výběru.
     *
     * @param g2 grafický kontext
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

        int spacing = 10;
        int totalContentHeight = titleHeight + spacing + options.length * optionHeight + (options.length - 1) * spacing;

        int contentStartY = boxY + (boxHeight - totalContentHeight) / 2;

        int titleX = boxX + (boxWidth - titleWidth) / 2;
        int titleY = contentStartY + titleFM.getAscent();
        g2.setFont(titleFont);
        g2.drawString(title, titleX, titleY);

        g2.setFont(optionFont);
        int optionY = titleY + spacing;
        for (String opt : options) {
            optionY += optionHeight + spacing;
            int x = boxX + (boxWidth - optionFM.stringWidth(opt)) / 2;
            g2.drawString(opt, x, optionY);
        }
    }

    /**
     * Přepočítá souřadnice tlačítka podle daného měřítka.
     *
     * @param src původní obdélník
     * @param k   škálovací koeficient
     * @return nový obdélník
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
     * Ošetřuje pohyb myši a zajišťuje efekt hover (zvýraznění) tlačítka.
     *
     * @param p bod kurzoru myši
     */
    public void mouseMoved(Point p) {
        for (UIButton b : buttons) b.hovered = b.bounds.contains(p);
    }

    /**
     * Označí tlačítko jako stisknuté, pokud bylo kliknuto.
     *
     * @param p pozice kliknutí myši
     */
    public void mousePressed(Point p) {
        buttons.forEach(b -> b.pressed = b.bounds.contains(p));
    }

    /**
     * Ošetřuje uvolnění tlačítka myši – buď v Game Over menu, nebo v hlavní nabídce.
     *
     * @param p pozice uvolnění myši
     */
    public void mouseReleased(Point p) {
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
     * Zpracuje kliknutí na některé z tlačítek – obecná obálka.
     *
     * @param p pozice kliknutí
     */
    public void handleClick(Point p) {
        for (UIButton b : buttons) {
            if (b.bounds.contains(p)) {
                switch (b.text) {
                    case "Start Game"       -> gp.startNewGame();
                    case "Start Saved Game" -> gp.loadSavedGame();
                    case "Exit"             -> System.exit(0);
                }
                break;
            }
        }
    }

    /**
     * Zpracuje kliknutí na možnosti v obrazovce Game Over.
     *
     * @param p pozice kliknutí
     */
    public void handleGameOverClick(Point p) {
        handleClick(p);
    }
}
