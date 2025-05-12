import cz.cvut.fel.pjv.golyakat.dungeon_escape.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link gamePanel} class to verify game state saving, inventory management,
 * monster spawning, and chest initialization behaviors.
 */
public class GamePanelTest {

    private gamePanel gp;

    /**
     * Sets up a fresh {@code gamePanel} instance and starts a new game before each test.
     */
    @BeforeEach
    public void setUp() {
        gp = new gamePanel();        // create new game panel instance
        gp.startNewGame();           // initialize default game state
    }

    /**
     * Verifies that {@link gamePanel#buildSaveData()} correctly captures the player's data
     * (position and life) into the returned {@link SaveData} object.
     */
    @Test
    public void testBuildSaveData_containsPlayerData() {
        SaveData saveData = gp.buildSaveData(); // create save data

        assertNotNull(saveData, "SaveData should not be null");
        assertNotNull(saveData.player, "Player data should not be null");
        assertTrue(saveData.player.life > 0, "Player should have life greater than 0");
        assertEquals(gp.player.worldX, saveData.player.worldX, "Player X position must match");
        assertEquals(gp.player.worldY, saveData.player.worldY, "Player Y position must match");
    }

    /**
     * Tests that adding an item to the player's inventory produces a valid drag-and-drop
     * representation of that item.
     */
    @Test
    public void testAddItem_createsDragItem() {
        ChestInventoryManager.ItemData item = new ChestInventoryManager.ItemData("Apple", 1);
        gamePanel gp = new gamePanel();
        gp.player.addItem(item);

        // Simulate drag creation
        ChestInventoryManager.ItemData dragged = new ChestInventoryManager.ItemData(item.getName(), 1);

        assertNotNull(gp.player.getInventory(), "Inventory list should not be null");
        assertEquals(1, gp.player.getInventory().size(), "Inventory should contain one item");
        assertEquals("Apple", gp.player.getInventory().get(0).getName(), "Stored item name must be 'Apple'");
        assertEquals("Apple", dragged.getName(), "Dragged item name must match");
    }

    /**
     * Ensures that {@link AssetSetter#setMonster()} spawns the correct boss entity
     * depending on the current map index (0 for Boss_Goblin, 1 for Boss_Eye).
     */
    @Test
    public void testSetMonster_spawnsCorrectBoss() {
        gamePanel gp = new gamePanel();
        gp.tileH.walkableRegions.add(Arrays.asList(new Point(1, 1)));
        gp.assetSetter = new AssetSetter(gp);

        gp.currentMap = 0;
        gp.assetSetter.setMonster();
        assertNotNull(gp.monster[0][0], "Boss_Goblin should be spawned on map 0");
        assertEquals("Boss_Goblin", gp.monster[0][0].getClass().getSimpleName());

        gp.currentMap = 1;
        gp.assetSetter.setMonster();
        assertNotNull(gp.monster[1][0], "Boss_Eye should be spawned on map 1");
        assertEquals("Boss_Eye", gp.monster[1][0].getClass().getSimpleName());
    }

    /**
     * Verifies that {@link ChestInventoryManager#getChestData(int, Map)} initializes
     * a chest with the expected items and quantities.
     */
    @Test
    public void testGetChestData_correctItemsAdded() {
        ChestInventoryManager manager = new ChestInventoryManager();

        Map<String, Integer> items = new HashMap<>();
        items.put("Apple", 2);
        items.put("potion", 1);

        ChestInventoryManager.ChestData chest = manager.getChestData(1, items);

        assertFalse(chest.isOpen(), "New chest should start closed");
        assertEquals(2, chest.getItems().size(), "Chest should contain two entries");
        assertEquals("Apple", chest.getItems().get(0).getName(), "First item name must be 'Apple'");
    }

}
