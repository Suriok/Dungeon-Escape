package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class PlayerUI {
    private gamePanel gp;
    private BufferedImage playerInventory;
    private BufferedImage sideArmor;
    private BufferedImage weaponInventory;
    private final float scaleFactor = 3.0f; // Масштаб для изображений

    public PlayerUI(gamePanel gp) {
        this.gp = gp;
        loadImages();
    }

    private void loadImages() {
        try {
            playerInventory = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/player_inven.png")));
            sideArmor = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/side_armor.png")));
            weaponInventory = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/weapon_inv.png")));
        } catch (Exception e) {
            System.out.println("Error loading UI images: " + e.getMessage());
        }
    }

    public void draw(Graphics2D g2d) {
        if (playerInventory == null || sideArmor == null || weaponInventory == null) {
            System.out.println("PlayerUI: Some images are not loaded");
            return;
        }

        // Размеры масштабированных изображений
        int playerInvWidth = (int)(playerInventory.getWidth() * scaleFactor);
        int playerInvHeight = (int)(playerInventory.getHeight() * scaleFactor);
        int sideArmorWidth = (int)(sideArmor.getWidth() * scaleFactor);
        int sideArmorHeight = (int)(sideArmor.getHeight() * scaleFactor);
        int weaponInvWidth = (int)(weaponInventory.getWidth() * scaleFactor);
        int weaponInvHeight = (int)(weaponInventory.getHeight() * scaleFactor);

        // Позиция инвентаря игрока (внизу по центру)
        int playerInvX = gp.screenWidth/2 - playerInvWidth/2;
        int playerInvY = gp.screenHeight - playerInvHeight - 10; // 10px отступ снизу

        // Позиция брони и оружия (слева, друг под другом)
        int sideX = 10; // Отступ слева
        int startY = gp.screenHeight/2 - (sideArmorHeight + 15 + weaponInvHeight)/2; // Центрируем по вертикали

        // Рисуем изображения
        g2d.drawImage(playerInventory, playerInvX, playerInvY, playerInvWidth, playerInvHeight, null);
        g2d.drawImage(sideArmor, sideX, startY, sideArmorWidth, sideArmorHeight, null);
        g2d.drawImage(weaponInventory, sideX, startY + sideArmorHeight + 15, weaponInvWidth, weaponInvHeight, null);
    }
}
