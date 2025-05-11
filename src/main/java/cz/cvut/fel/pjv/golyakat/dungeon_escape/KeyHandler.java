package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Třída {@code KeyHandler} zpracovává vstup z klávesnice.
 * <p>
 * Implementuje rozhraní {@link KeyListener} a uchovává informace
 * o stisknutých a uvolněných klávesách, které jsou důležité pro
 * ovládání pohybu a interakcí ve hře.
 * </p>
 */
public class KeyHandler implements KeyListener {

    /**
     * Příznak, zda je aktuálně stisknuto tlačítko W (pohyb nahoru).
     */
    public boolean upPressed;

    /**
     * Příznak, zda je aktuálně stisknuto tlačítko S (pohyb dolů).
     */
    public boolean downPressed;

    /**
     * Příznak, zda je aktuálně stisknuto tlačítko A (pohyb doleva).
     */
    public boolean leftPressed;

    /**
     * Příznak, zda je aktuálně stisknuto tlačítko D (pohyb doprava).
     */
    public boolean rightPressed;

    /**
     * Příznak, zda je aktuálně stisknuto tlačítko E (např. otevření truhly, interakce).
     */
    public boolean ePressed;

    /**
     * Příznak, zda je aktuálně stisknuto tlačítko F (např. útok, akce).
     */
    public boolean fPressed;

    /**
     * Příznak, zda je aktuálně stisknuto tlačítko Q (např. zahodit předmět, craftit apod.).
     */
    public boolean qPressed;

    /**
     * Tato metoda je volána automaticky při stisknutí klávesy,
     * ale není v této implementaci využita.
     *
     * @param e objekt reprezentující událost stisknutí klávesy
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Nevyužito
    }

    /**
     * Tato metoda je volána při stisknutí klávesy.
     * Nastaví příslušný boolean příznak na {@code true}.
     *
     * @param e objekt reprezentující událost stisknutí klávesy
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_E) {
            ePressed = true;
            GameLogger.info("E key pressed.");
        }
        if (code == KeyEvent.VK_F) {
            fPressed = true;
        }
        if (code == KeyEvent.VK_Q) {
            qPressed = true;
            GameLogger.info("Q key pressed.");
        }
    }

    /**
     * Tato metoda je volána při uvolnění klávesy.
     * Nastaví příslušný boolean příznak na {@code false}.
     *
     * @param e objekt reprezentující událost uvolnění klávesy
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch (code) {
            case KeyEvent.VK_W:
                upPressed = false;
                break;
            case KeyEvent.VK_S:
                downPressed = false;
                break;
            case KeyEvent.VK_A:
                leftPressed = false;
                break;
            case KeyEvent.VK_D:
                rightPressed = false;
                break;
            case KeyEvent.VK_E:
                ePressed = false;
                break;
            case KeyEvent.VK_F:
                fPressed = false;
                break;
            case KeyEvent.VK_Q:
                qPressed = false;
                break;
        }
    }
}
