package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.AssetSetter;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Object_Small_Chest extends GameObject {
    private boolean isOpen;
    private BufferedImage inventoryImage;
    private boolean showInventory;
    private int id;
    private List<ChestInventoryManager.ItemData> items;
    private ChestInventoryManager chestInventoryManager;
    private Map<String, Integer> fixedArmor;

    public Object_Small_Chest(AssetSetter gp, int id, Map<String, Integer> fixedArmor) {
        this.id = id;
        this.fixedArmor = (fixedArmor != null) ? fixedArmor : new HashMap<>();
        this.chestInventoryManager = gp.chestInventoryManager;

        name = "small_chest";
        Collision = true;
        isOpen = false;
        showInventory = false;

        // Логирование fixedArmor
        System.out.println("Chest " + id + " fixedArmor: " + this.fixedArmor);

        // Генерируем случайные расходуемые предметы
        Map<String, Integer> randomItems = generateRandomItems();
        // Объединяем случайные предметы и фиксированную броню
        Map<String, Integer> allItems = new HashMap<>(randomItems);
        allItems.putAll(this.fixedArmor);

        // Логирование allItems
        System.out.println("Chest " + id + " allItems: " + allItems);

        ChestInventoryManager.ChestData chestData = chestInventoryManager.getChestData(id, allItems);
        this.items = chestData.getItems();
        this.isOpen = chestData.isOpen();
        this.showInventory = false;

        // Логирование итогового списка предметов
        System.out.println("Chest " + id + " generated items: " + items);

        try {
            java.io.InputStream chestStream = getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/small_chest.png");
            if (chestStream == null) {
                System.err.println("Error: Resource '/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/small_chest.png' not found.");
            } else {
                image = ImageIO.read(chestStream);
            }

            java.io.InputStream inventoryStream = getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/case_inventory.png");
            if (inventoryStream == null) {
                System.err.println("Error: case_inventory.png not found.");
            } else {
                inventoryImage = ImageIO.read(inventoryStream);
                System.out.println("Successfully loaded case_inventory.png");
            }
        } catch (Exception e) {
            System.err.println("Error loading chest images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Map<String, Integer> generateRandomItems() {
        Map<String, Integer> randomItems = new HashMap<>();
        Random random = new Random();
        String[] possibleItems = {"Apple", "blubbery", "potion"};

        int numItems = random.nextInt(3) + 1;
        for (int i = 0; i < numItems; i++) {
            String itemName = possibleItems[random.nextInt(possibleItems.length)];
            int quantity = random.nextInt(3) + 1;
            randomItems.put(itemName, randomItems.getOrDefault(itemName, 0) + quantity);
        }
        return randomItems;
    }

    public void open() {
        showInventory = true;
        if (!isOpen) {
            isOpen = true;
        }
        chestInventoryManager.updateChestData(id, new ChestInventoryManager.ChestData(isOpen, items));
    }

    public void close() {
        showInventory = false;
        chestInventoryManager.updateChestData(id, new ChestInventoryManager.ChestData(isOpen, items));
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isShowingInventory() {
        return showInventory;
    }

    public BufferedImage getInventoryImage() {
        return inventoryImage;
    }

    public List<ChestInventoryManager.ItemData> getItems() {
        return items;
    }

    public void removeItem(ChestInventoryManager.ItemData item) {
        item.setQuantity(item.getQuantity() - 1);
        if (item.getQuantity() <= 0) {
            items.remove(item);
        }
        chestInventoryManager.updateChestData(id, new ChestInventoryManager.ChestData(isOpen, items));
    }

    public int getId() {
        return id;
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    @Override
    public void draw(java.awt.Graphics2D g2d, gamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        if (image != null) {
            g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}