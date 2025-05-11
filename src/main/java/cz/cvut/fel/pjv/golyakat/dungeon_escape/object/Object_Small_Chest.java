package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.AssetSetter;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * Třída {@code Object_Small_Chest} reprezentuje interaktivní truhlu,
 * která obsahuje náhodně nebo pevně definované předměty a lze ji otevřít.
 * <p>
 * Truhla má grafické zobrazení a vlastní inventář, který lze zobrazit hráči.
 * </p>
 */
public class Object_Small_Chest extends GameObject {

    /**
     * Určuje, zda je truhla již otevřena.
     */
    private boolean isOpen;

    /**
     * Obrázek grafického rozhraní truhly (inventáře).
     */
    private BufferedImage inventoryImage;

    /**
     * Příznak určující, zda se má právě zobrazovat inventář truhly.
     */
    private boolean showInventory;

    /**
     * Jedinečný identifikátor truhly pro ukládání stavu.
     */
    private int id;

    /**
     * Seznam předmětů aktuálně obsažených v truhle.
     */
    private List<ChestInventoryManager.ItemData> items;

    /**
     * Odkaz na správce truhlových inventářů, který zajišťuje ukládání a načítání stavu.
     */
    private ChestInventoryManager chestInventoryManager;

    /**
     * Mapa s pevně definovanými (neměnnými) předměty pro danou truhlu.
     */
    private Map<String, Integer> fixedArmor;

    /**
     * Vytvoří novou instanci malé truhly s předdefinovaným obsahem.
     *
     * @param gp         odkaz na {@link AssetSetter}, přes který získáme správce inventářů
     * @param id         jedinečný identifikátor truhly
     * @param fixedArmor mapa s pevně definovanými předměty (např. brnění)
     */
    public Object_Small_Chest(AssetSetter gp, int id, Map<String, Integer> fixedArmor) {
        this.id = id;
        this.fixedArmor = (fixedArmor != null) ? fixedArmor : new HashMap<>();
        this.chestInventoryManager = gp.chestInventoryManager;

        name = "small_chest";
        Collision = true;
        isOpen = false;
        showInventory = false;

        Map<String, Integer> randomItems = generateRandomItems();
        Map<String, Integer> allItems = new HashMap<>(randomItems);
        allItems.putAll(this.fixedArmor);

        ChestInventoryManager.ChestData chestData = chestInventoryManager.getChestData(id, allItems);
        this.items = chestData.getItems();
        this.isOpen = chestData.isOpen();

        try {
            var chestStream = getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/small_chest.png");
            if (chestStream != null) image = ImageIO.read(chestStream);

            var inventoryStream = getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/inventory/case_inventory.png");
            if (inventoryStream != null) inventoryImage = ImageIO.read(inventoryStream);
        } catch (Exception e) {
            GameLogger.error("Chyba při načítání obrázků truhly: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Vygeneruje náhodný obsah truhly (spotřební předměty).
     *
     * @return mapa názvů předmětů a jejich množství
     */
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

    /**
     * Otevře truhlu a zobrazí její obsah hráči.
     * <p>Stav se uloží do správce truhlic.</p>
     */
    public void open() {
        showInventory = true;
        if (!isOpen) isOpen = true;
        chestInventoryManager.updateChestData(id, new ChestInventoryManager.ChestData(isOpen, items));
    }

    /**
     * Zavře truhlu a aktualizuje její stav.
     */
    public void close() {
        showInventory = false;
        chestInventoryManager.updateChestData(id, new ChestInventoryManager.ChestData(isOpen, items));
    }

    /** @return {@code true}, pokud je truhla otevřená */
    public boolean isOpen() {
        return isOpen;
    }

    /** @return {@code true}, pokud se má zobrazovat inventář truhly */
    public boolean isShowingInventory() {
        return showInventory;
    }

    /** @return obrázek inventáře truhly */
    public BufferedImage getInventoryImage() {
        return inventoryImage;
    }

    /** @return seznam předmětů v truhle */
    public List<ChestInventoryManager.ItemData> getItems() {
        return items;
    }

    /**
     * Odstraní 1 kus z dané položky v truhle a případně ji zcela odstraní.
     *
     * @param item položka, která má být odebrána
     */
    public void removeItem(ChestInventoryManager.ItemData item) {
        item.setQuantity(item.getQuantity() - 1);
        if (item.getQuantity() <= 0) items.remove(item);
        chestInventoryManager.updateChestData(id, new ChestInventoryManager.ChestData(isOpen, items));
    }

    /** @return identifikátor truhly */
    public int getId() {
        return id;
    }

    /**
     * Odstraní předmět ze seznamu podle jeho indexu.
     *
     * @param index pozice položky v seznamu
     */
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    /**
     * Vykreslí truhlu na mapě podle její pozice vůči hráči.
     *
     * @param g2d grafický kontext
     * @param gp  instance herního panelu
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
