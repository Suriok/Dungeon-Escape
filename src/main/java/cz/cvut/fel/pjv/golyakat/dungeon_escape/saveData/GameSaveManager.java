package cz.cvut.fel.pjv.golyakat.dungeon_escape.saveData;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.sprite.Entity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Manages saving and loading of game state using XML serialization.
 * <p>
 * Utilizes Jackson's {@link XmlMapper} to convert Java objects to and from XML.
 * Game state is saved to a file {@code saved_game.xml} in the project root.
 * </p>
 */
public class GameSaveManager {

    /** File path where the game is saved. */
    private static final Path SAVE_PATH = Path.of("saved_game.xml");

    /** Jackson XML mapper used for (de)serialization. */
    private static final XmlMapper xml = new XmlMapper();

    /** Reference to the main game panel, used for accessing runtime state. */
    private final gamePanel gp;

    /**
     * Constructs a new game save manager.
     *
     * @param gp reference to the main game panel
     */
    public GameSaveManager(gamePanel gp) {
        this.gp = gp;
    }

    /**
     * Saves the current game state to an XML file.
     * <p>
     * If the target directory doesn't exist, it will be created.
     * All player stats, inventory, equipped items, and monsters are stored.
     * </p>
     */
    public void saveGame() {
        try {
            SaveData d = buildSaveData();
            Path parent = SAVE_PATH.getParent();
            if (parent != null) Files.createDirectories(parent);
            xml.writerWithDefaultPrettyPrinter().writeValue(SAVE_PATH.toFile(), d);
            GameLogger.info("Game saved to " + SAVE_PATH.toAbsolutePath());
        } catch (IOException ex) {
            GameLogger.error("Failed to save game: " + ex.getMessage());
        }
    }

    /**
     * Loads the game state from an XML file if available.
     * <p>
     * If no save file exists, starts a new game instead.
     * </p>
     */
    public void loadGame() {
        if (!Files.exists(SAVE_PATH)) {
            gp.doorHintMessage.show("No saved game found. Starting a new game.", 120);
            gp.startNewGame();
            return;
        }
        try {
            SaveData d = xml.readValue(SAVE_PATH.toFile(), SaveData.class);
            restoreFromSave(d);
            gp.gameState = gp.playerState;
            gp.repaint();
            GameLogger.info("Game loaded from " + SAVE_PATH.toAbsolutePath());
        } catch (IOException ex) {
            GameLogger.error("Failed to load save: " + ex.getMessage());
            gp.startNewGame();
        }
    }

    // === Internal method to collect data to be saved ===
    private SaveData buildSaveData() {
        SaveData data = new SaveData();
        data.player.worldX = gp.player.worldX;
        data.player.worldY = gp.player.worldY;
        data.player.life = gp.player.life;
        data.currentMap = gp.currentMap;
        data.levelSpawned = gp.levelSpawned.clone();

        for (ChestInventoryManager.ItemData it : gp.player.getInventory()) {
            data.player.backpack.add(new SaveData.ItemData(it.getName(), it.getQuantity()));
        }

        for (GameObject armor : gp.player.getEquippedArmor()) {
            if (armor != null) data.player.armor.add(new SaveData.ItemData(armor.name, 1));
        }

        if (gp.player.getEquippedWeapon() != null)
            data.player.weapon = new SaveData.ItemData(gp.player.getEquippedWeapon().name, 1);

        if (gp.player.getEquippedGrade() != null)
            data.player.grade = new SaveData.ItemData(gp.player.getEquippedGrade().name, 1);

        for (Entity m : gp.monster[gp.currentMap]) {
            if (m != null) {
                SaveData.MonsterData md = new SaveData.MonsterData();
                md.type = m.getClass().getSimpleName();
                md.worldX = m.worldX;
                md.worldY = m.worldY;
                md.life = m.life;
                md.dead = m.isDead;
                data.monsters.add(md);
            }
        }

        return data;
    }

    // === Internal method to restore the game state from SaveData ===
    private void restoreFromSave(SaveData d) {
        gp.currentMap = d.currentMap;
        gp.levelSpawned = (d.levelSpawned != null) ? d.levelSpawned.clone() : new boolean[2];
        gp.assetSetter.setObg();
        gp.tileH.findWalkableRegions();
        if (!gp.levelSpawned[gp.currentMap]) {
            gp.assetSetter.setMonster();
            gp.levelSpawned[gp.currentMap] = true;
        }

        gp.player.reset();
        gp.player.worldX = d.player.worldX;
        gp.player.worldY = d.player.worldY;
        gp.player.life = d.player.life;

        d.player.backpack.forEach(it ->
                gp.player.addItem(new ChestInventoryManager.ItemData(it.name, it.qty)));

        for (int i = 0; i < d.player.armor.size(); i++) {
            GameObject armor = gp.makeItem(d.player.armor.get(i).name);
            gp.player.equipArmor(armor, i);
        }

        if (d.player.weapon != null)
            gp.player.equipWeapon(gp.makeItem(d.player.weapon.name));

        if (d.player.grade != null)
            gp.player.equipGrade(gp.makeItem(d.player.grade.name));

        Arrays.stream(gp.monster).forEach(row -> Arrays.fill(row, null));
        for (int i = 0; i < d.monsters.size(); i++) {
            SaveData.MonsterData md = d.monsters.get(i);
            Entity m = switch (md.type) {
                case "Boss_Goblin" -> new cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.boss.Boss_Goblin(gp);
                case "Boss_Eye" -> new cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.boss.Boss_Eye(gp);
                case "Monster_Slime" -> new cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Slime(gp);
                case "Monster_Zombie" -> new cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Zombie(gp);
                case "Monster_Skeleton" -> new cz.cvut.fel.pjv.golyakat.dungeon_escape.monster.Monster_Skeleton(gp);
                default -> null;
            };
            if (m != null) {
                m.worldX = md.worldX;
                m.worldY = md.worldY;
                m.life = md.life;
                m.isDead = md.dead;
                gp.monster[gp.currentMap][i] = m;
            }
        }

        gp.chestInventoryManager.resetChestData();
    }
}
