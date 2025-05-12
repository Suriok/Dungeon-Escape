package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * The {@code KeyHandler} class handles keyboard input.
 * <p>
 * Implements the {@link KeyListener} interface and stores information
 * about pressed and released keys, which are important for controlling
 * movement and interactions in the game.
 * </p>
 */
public class KeyHandler implements KeyListener {

    /**
     * Flag indicating whether the W key (move up) is currently pressed.
     */
    public boolean upPressed;

    /**
     * Flag indicating whether the S key (move down) is currently pressed.
     */
    public boolean downPressed;

    /**
     * Flag indicating whether the A key (move left) is currently pressed.
     */
    public boolean leftPressed;

    /**
     * Flag indicating whether the D key (move right) is currently pressed.
     */
    public boolean rightPressed;

    /**
     * Flag indicating whether the E key (e.g., open chest, interaction) is currently pressed.
     */
    public boolean ePressed;

    /**
     * Flag indicating whether the F key (e.g., attack, action) is currently pressed.
     */
    public boolean fPressed;

    /**
     * Flag indicating whether the Q key (e.g., drop item, craft, etc.) is currently pressed.
     */
    public boolean qPressed;

    /**
     * This method is automatically called when a key is typed,
     * but it is not used in this implementation.
     *
     * @param e object representing the key typed event
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    /**
     * This method is called when a key is pressed.
     * Sets the corresponding boolean flag to {@code true}.
     *
     * @param e object representing the key pressed event
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
     * This method is called when a key is released.
     * Sets the corresponding boolean flag to {@code false}.
     *
     * @param e object representing the key released event
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