package cz.cvut.fel.pjv.golyakat.dungeon_escape.Sprite;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.KeyHandler;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorSide;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

// Třída hráče — rozšiřuje Entity a přidává logiku ovládání hráče
public class Player extends Entity {
    gamePanel gp; // Odkaz na herní panel
    KeyHandler keyH; // Ovladač klávesnice

    public final int screenX; // Pozice hráče na obrazovce (střed kamery)
    public final int screenY;

    // Obrázky pro animaci pohybu hráče
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;

    public String direction; // Aktuální směr pohybu
    public int spriteCounter = 0; // Počítadlo pro změnu snímku animace
    public int spriteNum = 1; // Číslo snímku animace (1 nebo 2)

    // Konstruktor hráče
    public Player(gamePanel gp, KeyHandler keyH) {
        super(gp);
        this.gp = gp;
        this.keyH = keyH;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        // Nastavení pozice hráče na obrazovce do středu
        screenX = gp.screenWidth / 2;
        screenY = gp.screenHeight / 2;

        // Nastavení hitboxu (kolizní oblasti) hráče
        solidArea = new Rectangle(8, 16, gp.tileSize - 24, gp.tileSize - 24);

        setDefaulteValues(); // Výchozí hodnoty (pozice, rychlost atd.)
        getPlayerImage(); // Načtení animací hráče
    }

    // Nastaví výchozí hodnoty hráče
    public void setDefaulteValues() {
        worldX = gp.tileSize * 15; // Výchozí X pozice ve světě
        worldY = gp.tileSize * 22; // Výchozí Y pozice ve světě
        speed = 4; // Rychlost pohybu hráče
        direction = "down"; // Výchozí směr

        maxLife = 4; // Maximální život hráče
        life = maxLife; // Aktuální život hráče
    }

    // Načítá obrázky hráče pro animaci pohybu
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

    // Aktualizace stavu hráče každý snímek
    public void update() {
        // Reset collision flag
        collisionOn = false;

        // Store old position
        int oldX = worldX;
        int oldY = worldY;

        // Initialize interaction index
        int interactionIndex = 999;

        // Store original hitbox
        Rectangle originalSolidArea = new Rectangle(solidArea);

        // Enlarge hitbox for object interaction checks
        solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);

        // Check for door interaction before movement (for collision-based prompt)
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            // Determine direction
            if (keyH.upPressed) {
                direction = "up";
            } else if (keyH.downPressed) {
                direction = "down";
            } else if (keyH.leftPressed) {
                direction = "left";
            } else if (keyH.rightPressed) {
                direction = "right";
            }

            // Check objects at intended position
            int objIndex = gp.collisionChecker.checkObject(this, true);
            if (objIndex != 999 && gp.obj[objIndex] != null) {
                String objName = gp.obj[objIndex].name;
                if (objName.equals("DoorFront") && !((Object_DoorFront) gp.obj[objIndex]).isOpen() ||
                        objName.equals("DoorSide") && !((Object_DoorSide) gp.obj[objIndex]).isOpen()) {
                    interactionIndex = objIndex;
                    gp.doorMessage = "Press E to open door";
                    gp.doorMessageCounter = 120;
                    System.out.println("Near door (pre-movement): " + objName + " at index " + objIndex + " at (" + gp.obj[objIndex].worldX/gp.tileSize + ", " + gp.obj[objIndex].worldY/gp.tileSize + ")");
                }
            }

            // Restore original hitbox for movement collision
            solidArea = originalSolidArea;

            // Move player
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

            // Check collisions with tiles
            gp.collisionChecker.checkTiles(this);

            // Check collisions with objects for movement blocking
            objIndex = gp.collisionChecker.checkObject(this, true);

            // If collision occurred, revert position
            if (collisionOn) {
                worldX = oldX;
                worldY = oldY;
                System.out.println("Collision detected, reverting to position: (" + oldX/gp.tileSize + ", " + oldY/gp.tileSize + ")");
            }

            // Update sprite animation
            spriteCounter++;
            if (spriteCounter > 15) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }

        // Enlarge hitbox again for post-movement object check
        solidArea = new Rectangle(0, 0, gp.tileSize, gp.tileSize);

        // Check for door interaction at current position (for standing interaction)
        int currentObjIndex = gp.collisionChecker.checkObject(this, true);
        if (currentObjIndex != 999 && gp.obj[currentObjIndex] != null) {
            String objName = gp.obj[currentObjIndex].name;
            if (objName.equals("DoorFront") && !((Object_DoorFront) gp.obj[currentObjIndex]).isOpen() ||
                    objName.equals("DoorSide") && !((Object_DoorSide) gp.obj[currentObjIndex]).isOpen()) {
                interactionIndex = currentObjIndex;
                gp.doorMessage = "Press E to open door";
                gp.doorMessageCounter = 120;
                System.out.println("Near door (post-movement): " + objName + " at index " + currentObjIndex + " at (" + gp.obj[currentObjIndex].worldX/gp.tileSize + ", " + gp.obj[currentObjIndex].worldY/gp.tileSize + ")");
            }
        }

        // Restore original hitbox
        solidArea = originalSolidArea;

        // Handle door interaction
        if (keyH.ePressed && interactionIndex != 999 && gp.obj[interactionIndex] != null) {
            System.out.println("Attempting to interact with object at index: " + interactionIndex + " (" + gp.obj[interactionIndex].name + ")");
            gp.collisionChecker.handleDoorInteraction(this, interactionIndex);
            keyH.ePressed = false;
        } else if (keyH.ePressed) {
            System.out.println("E pressed but no interactable object found (index: " + interactionIndex + ")");
            keyH.ePressed = false;
        }

    }

    public void pickUpObject(int i) {
        // Can be implemented for other object interactions
    }

    // Metoda pro vykreslení hráče
    @Override
    public void draw(Graphics2D g2d) {
        BufferedImage imageToDraw = null;

        // Vybereme správný snímek animace podle směru a aktuálního snímku
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

        // Nastavíme image, aby ji GameObject.draw mohl použít
        this.image = imageToDraw;

        // Voláme metodu draw ze základní třídy GameObject
        super.draw(g2d, gp);
    }
}