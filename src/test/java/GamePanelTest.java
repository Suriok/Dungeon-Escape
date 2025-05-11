import cz.cvut.fel.pjv.golyakat.dungeon_escape.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
public class GamePanelTest {

    private gamePanel gp;

    /**
     * Metoda se provede před každým testem.
     * Inicializuje novou instanci gamePanelu a spustí novou hru.
     */
    @BeforeEach
    public void setUp() {
        gp = new gamePanel();        // vytvoření instance hlavního herního panelu
        gp.startNewGame();          // nastavení výchozího stavu hry
    }

    /**
     * Testuje metodu buildSaveData(), která vytváří objekt reprezentující aktuální stav hry.
     * Tento test ověřuje, že údaje o hráči (pozice, životy) jsou správně uloženy do výsledného objektu SaveData.
     * Díky tomu je zajištěno, že při uložení hry se zaznamená aktuální pozice a stav hráče.
     */
    @Test
    public void testBuildSaveData_containsPlayerData() {
        SaveData saveData = gp.buildSaveData(); // vytvoření dat pro uložení

        assertNotNull(saveData, "SaveData by nemělo být null");
        assertNotNull(saveData.player, "Player data by neměla být null");
        assertTrue(saveData.player.life > 0, "Hráč by měl mít životy větší než 0");
        assertEquals(gp.player.worldX, saveData.player.worldX, "Pozice hráče X se musí shodovat");
        assertEquals(gp.player.worldY, saveData.player.worldY, "Pozice hráče Y se musí shodovat");
    }


    /**
     * Test ověřuje správnou správu hráčova inventáře.
     * Konkrétně se testuje, že po přidání položky do inventáře je tato položka správně uložena
     * a že lze vytvořit přetahovatelný objekt (napodobení chování drag-and-drop v GUI).
     */
    @Test
    public void testAddItem_createsDragItem() {
        ChestInventoryManager.ItemData item = new ChestInventoryManager.ItemData("Apple", 1);
        gamePanel gp = new gamePanel();
        gp.player.addItem(item);

        // Rozšíření pro simulaci dragování
        ChestInventoryManager.ItemData dragged = new ChestInventoryManager.ItemData(item.getName(), 1);

        assertNotNull(gp.player.getInventory());
        assertEquals(1, gp.player.getInventory().size());
        assertEquals("Apple", gp.player.getInventory().get(0).getName());
        assertEquals("Apple", dragged.getName());
    }



    /**
     * Tento test ověřuje správné fungování metody setMonster() v třídě AssetSetter.
     * Metoda má za úkol spawnovat správného bosse v závislosti na aktivní mapě (0 nebo 1).
     * Test zajišťuje, že bossové se správně generují a jsou přiřazeni do pole monster.
     */
    @Test
    public void testSetMonster_spawnsCorrectBoss() {
        gamePanel gp = new gamePanel();
        gp.tileH.walkableRegions.add(Arrays.asList(new Point(1, 1)));
        gp.assetSetter = new AssetSetter(gp);

        gp.currentMap = 0;
        gp.assetSetter.setMonster();

        assertNotNull(gp.monster[0][0]);
        assertEquals("Boss_Goblin", gp.monster[0][0].getClass().getSimpleName());

        gp.currentMap = 1;
        gp.assetSetter.setMonster();
        assertNotNull(gp.monster[1][0]);
        assertEquals("Boss_Eye", gp.monster[1][0].getClass().getSimpleName());
    }

    /**
     * Testuje metodu getChestData(), která inicializuje obsah truhly.
     * Ověřuje, že truhla obsahuje správné položky se zadaným názvem a množstvím,
     * což je klíčové při generování lootů a ukládání/obnovování herního stavu.
     */
    @Test
    public void testGetChestData_correctItemsAdded() {
        ChestInventoryManager manager = new ChestInventoryManager();

        Map<String, Integer> items = new HashMap<>();
        items.put("Apple", 2);
        items.put("potion", 1);

        ChestInventoryManager.ChestData chest = manager.getChestData(1, items);

        assertTrue(chest.isOpen() == false);
        assertEquals(2, chest.getItems().size());
        assertEquals("Apple", chest.getItems().get(0).getName());
    }


}
