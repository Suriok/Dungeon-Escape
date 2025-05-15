package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Interaktivní malá truhla. */
public class Object_Small_Chest extends GameObject {

    private final List<ChestInventoryManager.ItemData> items;   // ← seznam předmětů
    private boolean showInventory = false;

    private BufferedImage inventoryImage;

    public Object_Small_Chest(gamePanel gp,
                              int id,
                              Map<String, Integer> defaultItems) {

        this.items = gp.chestInventoryManager.getChestData(id, defaultItems); // ← získáme z manageru

        name = "small_chest";
        Collision = true;

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/small_chest.png")));
            inventoryImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/case_inventory.png")));
        } catch (Exception ignored) {}
    }

    /* ---------- getter, který potřebuje ChestUI ---------- */
    public List<ChestInventoryManager.ItemData> getItems() {
        return items;
    }

    /* ---------- inventář on/off ---------- */
    public void open()  { showInventory = true;  }
    public void close() { showInventory = false; }
    public boolean isShowingInventory() { return showInventory; }
    public BufferedImage getInventoryImage() { return inventoryImage; }

    /* ---------- vykreslení truhly ---------- */
    @Override
    public void draw(java.awt.Graphics2D g2d, gamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        if (image != null) {
            g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
