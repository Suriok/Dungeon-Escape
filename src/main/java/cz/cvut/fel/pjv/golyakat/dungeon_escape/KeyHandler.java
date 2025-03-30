package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import javafx.scene.input.KeyCode;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed;


    @Override
    public void keyTyped(KeyEvent e) {} // Do not use

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Sprite go up
        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }

        // Sprite go down
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        //Sprite go to the left
        if(code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        //Sprite go to the right
        if(code == KeyEvent.VK_D) {
            rightPressed = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        // Sprite go up
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }

        // Sprite go down
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        //Sprite go to the left
        if(code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        //Sprite go to the right
        if(code == KeyEvent.VK_D) {
            rightPressed = false;
        }

    }
}
