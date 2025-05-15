package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a small interactive chest object that holds inventory items.
 * <p>
 * When opened by the player, it displays a UI inventory with its contents.
 * Items are initialized on first access via {@link ChestInventoryManager}.
 * </p>
 */
public class Object_Small_Chest extends GameObject {

    /** List of items stored in the chest. */
    private final List<ChestInventoryManager.ItemData> items;

    /** Flag indicating whether the chest inventory is currently open. */
    private boolean showInventory = false;

    /** Background image used for rendering the chest inventory UI. */
    private BufferedImage inventoryImage;

    /**
     * Constructs a new small chest object with specific contents.
     *
     * @param gp           reference to the main game panel
     * @param id           unique chest ID used for persistence
     * @param defaultItems map of item names and their quantities used as initial contents
     */
    public Object_Small_Chest(gamePanel gp,
                              int id,
                              Map<String, Integer> defaultItems) {

        this.items = gp.chestInventoryManager.getChestData(id, defaultItems);

        name = "small_chest";
        Collision = true;

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/small_chest.png")));
            inventoryImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/case_inventory.png")));
        } catch (Exception ignored) {}
    }

    /**
     * Returns the list of items contained in this chest.
     * <p>
     * Used by the inventory UI to render item icons and quantities.
     * </p>
     *
     * @return list of item data objects
     */
    public List<ChestInventoryManager.ItemData> getItems() {
        return items;
    }

    /**
     * Opens the inventory interface of the chest.
     */
    public void open() {
        showInventory = true;
    }

    /**
     * Closes the inventory interface of the chest.
     */
    public void close() {
        showInventory = false;
    }

    /**
     * Indicates whether the inventory of this chest is currently shown.
     *
     * @return {@code true} if inventory is open, {@code false} otherwise
     */
    public boolean isShowingInventory() {
        return showInventory;
    }

    /**
     * Returns the image used as the inventory background when the chest is opened.
     *
     * @return the chest inventory background image
     */
    public BufferedImage getInventoryImage() {
        return inventoryImage;
    }

    /**
     * Draws the chest on the screen relative to the player's current position.
     *
     * @param g2d graphics context
     * @param gp  reference to the game panel
     */
    @Override
    public void draw(java.awt.Graphics2D g2d, gamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        if (image != null) {
            g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}
