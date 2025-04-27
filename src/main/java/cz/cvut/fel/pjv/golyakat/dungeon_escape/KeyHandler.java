package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// KeyHandler zpracovává vstupy z klávesnice
public class KeyHandler implements KeyListener {

    // Stav kláves: true = klávesa je stisknutá, false = uvolněná
    public boolean upPressed, downPressed, leftPressed, rightPressed, ePressed, fPressed;

    // Tento event nepoužíváme (reaguje na jednotlivé znaky)
    @Override
    public void keyTyped(KeyEvent e) {}

    // Událost: klávesa byla stisknuta
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); // Kód klávesy

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
        }
        if (code == KeyEvent.VK_F) {
            fPressed = true;
        }
    }

    // Událost: klávesa byla uvolněna
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode(); // Kód klávesy

        // Podle uvolněné klávesy nastavíme odpovídající boolean na false
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
        }
    }
}