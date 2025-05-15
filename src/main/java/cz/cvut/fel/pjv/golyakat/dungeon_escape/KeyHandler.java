package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The {@code KeyHandler} class handles keyboard input for the game.
 * <p>
 * It captures directional movement (WASD) and interaction keys (E and Q),
 * storing their states in public booleans used by the main game loop.
 * </p>
 */
public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean ePressed, qPressed;

    @Override public void keyTyped(KeyEvent e) { /* ignore */ }

    @Override public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> upPressed = true;
            case KeyEvent.VK_S -> downPressed = true;
            case KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_E -> { ePressed = true; GameLogger.info("E pressed"); }
            case KeyEvent.VK_Q -> { qPressed = true; GameLogger.info("Q pressed"); }
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> upPressed = false;
            case KeyEvent.VK_S -> downPressed = false;
            case KeyEvent.VK_A -> leftPressed = false;
            case KeyEvent.VK_D -> rightPressed = false;
            case KeyEvent.VK_E -> ePressed = false;
            case KeyEvent.VK_Q -> qPressed = false;
        }
    }
}
