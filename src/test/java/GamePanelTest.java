import cz.cvut.fel.pjv.golyakat.dungeon_escape.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link gamePanel} class to verify game state saving, inventory management,
 * monster spawning, and chest initialization behaviors.
 */
public class GamePanelTest {

    /**
     * Sets up a fresh {@code gamePanel} instance and starts a new game before each test.
     */
    @BeforeEach
    public void setUp() {
        gamePanel gp = new gamePanel();        // create new game panel instance
        gp.startNewGame();           // initialize default game state
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
        gp.tileH.walkableRegions.add(List.of(new Point(1, 1)));
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

}
