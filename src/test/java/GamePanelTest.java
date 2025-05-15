import cz.cvut.fel.pjv.golyakat.dungeon_escape.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Slime;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_DoorFront;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link gamePanel} class to verify game state saving, inventory management,
 * monster spawning, chest initialization behaviors, and other game mechanics.
 */
public class GamePanelTest {

    private gamePanel gp;

    /**
     * Sets up a fresh {@code gamePanel} instance and starts a new game before each test.
     */
    @BeforeEach
    public void setUp() {
        gp = new gamePanel(); // create new game panel instance
        gp.startNewGame();    // initialize default game state
    }

    /**
     * Tests that clicking to pick up an item correctly adds it to the player's inventory.
     */
    @Test
    public void testAddItem() {
        // === Create an item to add to inventory ===
        ChestInventoryManager.ItemData item = new ChestInventoryManager.ItemData("Apple", 1);

        // === Simulate player clicking to pick up the item ===
        gp.player.addItem(item);

        // === Get the player's inventory ===
        List<ChestInventoryManager.ItemData> inventory = gp.player.getInventory();
        assertNotNull(inventory, "Inventory list should not be null");
        assertEquals(1, inventory.size(), "Inventory should contain one item");

        // === Verify the stored item details ===
        ChestInventoryManager.ItemData storedItem = inventory.get(0);
        assertEquals("Apple", storedItem.getName(), "Stored item name must be 'Apple'");
        assertEquals(1, storedItem.getQuantity(), "Stored item quantity should be 1");
        assertEquals(ItemType.HEALING, storedItem.getType(), "Item type should be HEALING");
    }

    /**
     * Ensures that {@link AssetSetter#setMonster()} spawns the correct boss entity
     * depending on the current map index (0 for Boss_Goblin, 1 for Boss_Eye).
     */
    @Test
    public void testSetMonster() {
        // === Add a walkable region and initialize asset setter ===
        gp.tileH.walkableRegions.add(List.of(new Point(1, 1)));
        gp.assetSetter = new AssetSetter(gp);

        // === Set map to 0 and spawn monster ===
        gp.currentMap = 0;
        gp.assetSetter.setMonster();
        assertNotNull(gp.monster[0][0], "Boss_Goblin should be spawned on map 0");
        assertEquals("Boss_Goblin", gp.monster[0][0].getClass().getSimpleName());

        // === Set map to 1 and spawn monster ===
        gp.currentMap = 1;
        gp.assetSetter.setMonster();
        assertNotNull(gp.monster[1][0], "Boss_Eye should be spawned on map 1");
        assertEquals("Boss_Eye", gp.monster[1][0].getClass().getSimpleName());
    }

    /**
     * Tests that the player's initial position is correctly set after starting a new game.
     */
    @Test
    public void testStartNewGame() {
        // === Verify map, level state, and player position ===
        assertEquals(0, gp.currentMap, "Current map should be 0 after starting a new game");
        assertTrue(gp.levelSpawned[0], "Level 0 should be marked as spawned");
        assertEquals(gp.tileSize * 15, gp.player.worldX, "Player's initial X position should be 10 tiles");
        assertEquals(gp.tileSize * 22, gp.player.worldY, "Player's initial Y position should be 10 tiles");
    }

    /**
     * Tests that a door can be interacted with when the player is in range and the 'E' key is pressed.
     */
    @Test
    public void testDoorInteraction() {
        // === Set up a door on the map ===
        gp.obj[0][0] = new Object_DoorFront(gp, false);
        gp.obj[0][0].worldX = gp.player.worldX;
        gp.obj[0][0].worldY = gp.player.worldY + gp.tileSize;

        // === Simulate 'E' key press ===
        gp.keyH.ePressed = true;
        gp.update();

        // === Verify door state ===
        assertTrue(((Object_DoorFront) gp.obj[0][0]).isOpen(), "Door should be opened after interaction");
    }

    /**
     * Tests that a chest can be opened when the player is in range and the 'E' key is pressed.
     */
    @Test
    public void testChestInteraction() {
        // === Set up a chest on the map with an item ===
        Map<String, Integer> chestItems = new HashMap<>();
        chestItems.put("Apple", 1);
        gp.obj[0][0] = new Object_Small_Chest(gp, 0, chestItems);
        gp.obj[0][0].worldX = gp.player.worldX;
        gp.obj[0][0].worldY = gp.player.worldY + gp.tileSize;

        // === Simulate 'E' key press ===
        gp.keyH.ePressed = true;
        gp.update();

        // === Verify chest UI state ===
        assertTrue(gp.chestUI.isShowingInventory(), "Chest UI should be open after interaction");
    }

    /**
     * Tests that the game state transitions to game over when the player's life reaches 0.
     */
    @Test
    public void testPlayerDeath() {
        // === Set player's life to 0 ===
        gp.player.life = 0;
        gp.update();

        // === Verify game state ===
        assertEquals(gp.gameOverState, gp.gameState, "Game state should be game over when player life is 0");
    }

    /**
     * Tests that saving and loading the game preserves the player's position and inventory.
     */
    @Test
    public void testSaveLoadGame() throws Exception {
        // === Modify player position ===
        gp.player.worldX = 20 * gp.tileSize;
        gp.player.worldY = 15 * gp.tileSize;

        // === Simulate picking up 2 apples separately ===
        gp.player.addItem(new ChestInventoryManager.ItemData("Apple", 1));
        gp.player.addItem(new ChestInventoryManager.ItemData("Apple", 1));

        // === Save the game ===
        gp.saveGame();

        // === Load into a new game panel ===
        gamePanel newGp = new gamePanel();
        newGp.loadGame();

        // === Verify loaded position ===
        assertEquals(20 * gp.tileSize, newGp.player.worldX, "Loaded player's X position should match saved position");
        assertEquals(15 * gp.tileSize, newGp.player.worldY, "Loaded player's Y position should match saved position");

        // === Count total quantity of Apples ===
        int totalApple = newGp.player.getInventory().stream()
                .filter(i -> i.getName().equals("Apple"))
                .mapToInt(ChestInventoryManager.ItemData::getQuantity)
                .sum();

        assertEquals(2, totalApple, "Total quantity of Apples should be 2");

        // === Clean up save file ===
        Files.deleteIfExists(Path.of("saved_game.xml"));
    }

    /**
     * Tests that consuming a healing item increases the player's life.
     */
    @Test
    public void testConsumeHealingItem() {
        // === Set player's life to a non-max value ===
        gp.player.life = 4;
        int initialLife = gp.player.life;

        // === List of healing items to test ===
        String[] healingItems = { "potion", "Apple", "blubbery" };

        for (String itemName : healingItems) {
            // === Create item through factory ===
            ChestInventoryManager.ItemData item = new ChestInventoryManager.ItemData(itemName, 1);
            gp.player.addItem(item);

            // === Heal the player ===
            gp.player.consumeHealingItem(item);

            // === Verify life increase ===
            int afterLife = gp.player.life;
            assertTrue(afterLife > initialLife,
                    "Healing item '" + itemName + "' should increase player's life");

            // === Reset life for the next iteration ===
            gp.player.life = 4;
        }
    }

    /**
     * Tests that equipping a weapon updates the player's equipped weapon.
     */
    @Test
    public void testEquipWeapon_updatesEquippedWeapon() {
        // === Add a weapon item to inventory ===
        ChestInventoryManager.ItemData weaponItem = new ChestInventoryManager.ItemData("iron_sword", 1);
        gp.player.addItem(weaponItem);

        // === Simulate equipping the weapon ===
        gp.player.equipWeapon(weaponItem.getItem());

        // === Verify equipped weapon ===
        assertNotNull(gp.player.getEquippedWeapon(), "Player should have an equipped weapon");
        assertEquals("iron_sword", gp.player.getEquippedWeapon().name, "Equipped weapon should be iron_sword");
    }

    /**
     * Tests that monsters are updated only when the game is in the player state.
     */
    @Test
    public void testMonsterUpdate() {
        // === Set up a monster ===
        gp.monster[0][1] = new Monster_Slime(gp);
        gp.monster[0][1].worldX = 15 * gp.tileSize;
        gp.monster[0][1].worldY = 15 * gp.tileSize;
        int initialX = gp.monster[0][1].worldX;

        // === Set game state to title state (not player state) ===
        gp.gameState = gp.titleState;
        gp.update();

        // === Verify monster position ===
        assertEquals(initialX, gp.monster[0][1].worldX, "Monster position should not change when not in player state");
    }

    /**
     * Tests that the hint message system displays the correct message for a door.
     */
    @Test
    public void testHintMessage_displaysDoorMessage() {
        // === Set up a door close to the player ===
        gp.obj[0][0] = new Object_DoorFront(gp, false);
        gp.obj[0][0].worldX = gp.player.worldX;
        gp.obj[0][0].worldY = gp.player.worldY + gp.tileSize;

        // === Update game state to trigger hint message ===
        gp.update();

        // === Verify hint message ===
        assertTrue(gp.doorHintMessage.isVisible(), "Door hint message should be visible");
        assertEquals("Press E to open", gp.doorHintMessage.text, "Door hint message should prompt to open the door");
    }

    /**
     * Tests that the player's collision with tiles prevents movement.
     */
    @Test
    public void testPlayerCollisionWithTiles() {
        // === List of solid tile indices ===
        int[] solidTileIndices = {7, 8, 6, 4, 3, 2, 1};
        int initialX = gp.tileSize * 10;
        int initialY = gp.tileSize * 10;
        int tileCol = initialX / gp.tileSize;
        int tileRow = initialY / gp.tileSize;

        // === Save the original map state ===
        int[][][] originalMap = new int[gp.maxMap][][];
        for (int map = 0; map < gp.maxMap; map++) {
            originalMap[map] = new int[gp.maxWorldRow][gp.maxWorldCol];
            for (int row = 0; row < gp.maxWorldRow; row++) {
                originalMap[map][row] = gp.tileH.mapTileNum[map][row].clone();
            }
        }

        for (int tileIndex : solidTileIndices) {
            // === Verify tile properties ===
            var tile = gp.tileH.tiles[tileIndex];
            assertNotNull(tile, "Tile with index " + tileIndex + " should not be null");
            assertTrue(tile.collision, "Tile with index " + tileIndex + " should have collision=true");

            // === Reset player position ===
            gp.player.worldX = initialX;
            gp.player.worldY = initialY;

            // === Test all directions ===
            String[] directions = {"up", "down", "left", "right"};
            for (String direction : directions) {
                gp.player.direction = direction;

                // === Place solid tile in the direction of movement ===
                int targetRow = tileRow;
                int targetCol = tileCol;
                switch (direction) {
                    case "up" -> targetRow--;
                    case "down" -> targetRow++;
                    case "left" -> targetCol--;
                    case "right" -> targetCol++;
                }

                if (targetRow >= 0 && targetRow < gp.maxWorldRow && targetCol >= 0 && targetCol < gp.maxWorldCol) {
                    gp.tileH.mapTileNum[gp.currentMap][targetRow][targetCol] = tileIndex;

                    // === Save initial coordinates ===
                    int oldX = gp.player.worldX;
                    int oldY = gp.player.worldY;

                    // === Perform update ===
                    gp.player.update();

                    // === Verify player didn't move ===
                    boolean shouldCollide = (direction.equals("up") && oldY > gp.player.worldY) ||
                            (direction.equals("down") && oldY < gp.player.worldY) ||
                            (direction.equals("left") && oldX > gp.player.worldX) ||
                            (direction.equals("right") && oldX < gp.player.worldX);
                    assertFalse(shouldCollide, "Player should not move through solid tile " + tileIndex + " in " + direction + " direction");
                }
            }

            // === Reset map after test ===
            for (int row = 0; row < gp.maxWorldRow; row++) {
                gp.tileH.mapTileNum[gp.currentMap][row] = originalMap[gp.currentMap][row].clone();
            }
        }
    }

    /**
     * Tests player-monster combat mechanics, including damage exchange and health updates.
     */
    @Test
    public void testPlayerAttackHitsMonsterBelow() {
        // === Set player's initial position (in tiles) ===
        gp.player.worldX = gp.tileSize * 10;
        gp.player.worldY = gp.tileSize * 10;
        gp.player.direction = "down";
        gp.player.equipWeapon(gp.makeItem("iron_sword"));

        // === Create and place a slime monster below the player ===
        Monster_Slime slime = new Monster_Slime(gp);
        slime.worldX = gp.tileSize * 10;
        slime.worldY = gp.tileSize * 11;
        gp.monster[0][0] = slime;

        // === Store initial monster life ===
        int initialMonsterLife = slime.life;

        // === Player attacks ===
        gp.player.attack();

        // === Verify monster took damage ===
        assertTrue(slime.life < initialMonsterLife,
                "Monster should receive damage from attack directed downward");

        // === Verify monster is still alive ===
        assertFalse(slime.isDead, "Monster should survive the first hit");
    }
}