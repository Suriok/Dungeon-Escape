package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, ePressed, fPressed, qPressed;

    @Override
    public void keyTyped(KeyEvent e) {}

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
            System.out.println("E key pressed.");
        }
        if (code == KeyEvent.VK_F) {
            fPressed = true;
        }
        if (code == KeyEvent.VK_Q) {
            qPressed = true;
            System.out.println("Q key pressed.");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        switch(code) {
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