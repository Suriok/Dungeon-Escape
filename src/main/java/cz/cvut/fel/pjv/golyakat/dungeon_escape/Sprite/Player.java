package cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.KeyHandler;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Player extends Entity {
    gamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;

    public String direction;
    public int spriteCounter = 0;
    public int spriteNum = 1;

    public Player(gamePanel gp, KeyHandler keyH) {
        super(gp);
        this.gp = gp;
        this.keyH = keyH;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        screenX = gp.screenWidth / 2;
        screenY = gp.screenHeight / 2;

        solidArea = new Rectangle(8, 16, gp.tileSize - 24, gp.tileSize - 24);

        setDefaulteValues();
        getPlayerImage();
    }

    public void setDefaulteValues() {
        worldX = gp.tileSize * 15;
        worldY = gp.tileSize * 22;
        speed = 4;
        direction = "down";

        maxLife = 4;
        life = maxLife;
    }

    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_up_1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_up_2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_down_1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_down_2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_right_1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_right_2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_left_1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/player/run_left_2.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        collisionOn = false;
        int oldX = worldX;
        int oldY = worldY;
        int interactionIndex = 999;

        // Проверяем движение
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

            // Проверяем коллизию для движения
            switch (direction) {
                case "up":
                    worldY -= speed;
                    break;
                case "down":
                    worldY += speed;
                    break;
                case "left":
                    worldX -= speed;
                    break;
                case "right":
                    worldX += speed;
                    break;
            }

            gp.collisionChecker.checkTiles(this);
            int objIndex = gp.collisionChecker.checkObject(this, true);

            if (collisionOn) {
                worldX = oldX;
                worldY = oldY;
                System.out.println("Collision detected, reverting to position: (" + oldX/gp.tileSize + ", " + oldY/gp.tileSize + ")");
            }

            spriteCounter++;
            if (spriteCounter > 15) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }

        // Проверяем взаимодействие с объектами (в текущем положении, не зависимо от движения)
        interactionIndex = gp.collisionChecker.checkObjectForInteraction(this, true);
        if (interactionIndex != 999 && gp.obj[interactionIndex] != null) {
            String objName = gp.obj[interactionIndex].name;
            if (objName.equals("DoorFront") && !((Object_DoorFront) gp.obj[interactionIndex]).isOpen() ||
                    objName.equals("DoorSide") && !((Object_DoorSide) gp.obj[interactionIndex]).isOpen()) {
                gp.doorMessage = "Press E to open door";
                gp.doorMessageCounter = 120;
                System.out.println("Near door: " + objName + " at index " + interactionIndex);
            } else if (objName.equals("small_chest")) {
                // Проверяем состояние сундука и задаем соответствующее сообщение
                if (((Object_Small_Chest) gp.obj[interactionIndex]).isOpen()) {
                    gp.chestMessage = "Press E to close the chest";
                } else {
                    gp.chestMessage = "Press E to open the chest";
                }
                gp.chestMessageCounter = 120;
            }
        }

        // Обрабатываем нажатие клавиши E
        if (keyH.ePressed && interactionIndex != 999 && gp.obj[interactionIndex] != null) {
            System.out.println("Attempting to interact with object at index: " + interactionIndex + " (" + gp.obj[interactionIndex].name + ")");
            gp.collisionChecker.handleObjectInteraction(this, interactionIndex);
            keyH.ePressed = false;
        } else if (keyH.ePressed) {
            System.out.println("E pressed but no interactable object found (index: " + interactionIndex + ")");
            keyH.ePressed = false;
        }
    }

    public void pickUpObject(int i) {
    }

    public void draw(Graphics2D g2d) {
        BufferedImage imageToDraw = null;

        switch (direction) {
            case "up":
                imageToDraw = (spriteNum == 1) ? up1 : up2;
                break;
            case "down":
                imageToDraw = (spriteNum == 1) ? down1 : down2;
                break;
            case "left":
                imageToDraw = (spriteNum == 1) ? left1 : left2;
                break;
            case "right":
                imageToDraw = (spriteNum == 1) ? right1 : right2;
                break;
        }

        this.image = imageToDraw;
        super.draw(g2d, gp);
    }
}