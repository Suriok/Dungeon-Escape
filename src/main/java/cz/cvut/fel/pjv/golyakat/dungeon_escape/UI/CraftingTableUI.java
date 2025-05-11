package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Třída {@code CraftingTableUI} reprezentuje grafické rozhraní pro craftování (výrobu) předmětů.
 * <p>
 * V aktuální implementaci slouží k vytvoření předmětu {@code SilverKey} ze tří částí klíče
 * – Key1, Key2 a Key3.
 * </p>
 */
public class CraftingTableUI {

    /** Odkaz na hlavní herní panel pro přístup k hráči, rozměrům a přemalování. */
    private gamePanel gp;

    /** Určuje, zda je okno craftování zrovna zobrazené. */
    private boolean isShowing;

    /** Pole slotů používaných pro výrobu. Očekává se přesně 3 části klíče. */
    private ChestInventoryManager.ItemData[] craftingSlots;

    /** Obdélníky reprezentující interaktivní oblasti pro sloty. */
    private Rectangle[] slotBounds;

    /** Obdélník pro tlačítko "Craft". */
    private Rectangle craftButtonBounds;

    /**
     * Inicializuje craftovací UI, nastaví prázdné sloty a nezobrazený stav.
     *
     * @param gp hlavní herní panel
     */
    public CraftingTableUI(gamePanel gp) {
        this.gp = gp;
        this.isShowing = false;
        this.craftingSlots = new ChestInventoryManager.ItemData[3];
        this.slotBounds = new Rectangle[3];
        this.craftButtonBounds = null;
    }

    /**
     * Přidá položku do seznamu. Pokud již existuje, navýší množství.
     *
     * @param list seznam, do kterého přidáváme
     * @param item položka, kterou přidáváme nebo zvyšujeme množství
     */
    public static void addOrIncrement(java.util.List<ChestInventoryManager.ItemData> list,
                                      ChestInventoryManager.ItemData item) {
        for (ChestInventoryManager.ItemData d : list) {
            if (d.getName().equals(item.getName())) {
                d.setQuantity(d.getQuantity() + item.getQuantity());
                return;
            }
        }
        list.add(item);
    }

    /** Zobrazí okno craftování. */
    public void open() {
        isShowing = true;
        System.out.println("CraftingTableUI: Opened");
    }

    /** Skryje okno craftování. */
    public void close() {
        isShowing = false;
        System.out.println("CraftingTableUI: Closed");
    }

    /** @return zda je právě okno craftingu zobrazeno */
    public boolean isShowing() {
        return isShowing;
    }

    /** @return pole obdélníků představujících sloty pro předměty */
    public Rectangle[] getSlotBounds() {
        return slotBounds;
    }

    /** @return obdélník představující tlačítko "Craft" */
    public Rectangle getCraftButtonBounds() {
        return craftButtonBounds;
    }

    /**
     * Vrací předmět ve slotu na daném indexu.
     *
     * @param index číslo slotu (0–2)
     * @return instance předmětu nebo {@code null}
     */
    public ChestInventoryManager.ItemData getCraftingSlot(int index) {
        if (index >= 0 && index < craftingSlots.length) {
            return craftingSlots[index];
        }
        return null;
    }

    /**
     * Nastaví předmět do konkrétního slotu craftingu.
     *
     * @param index index slotu (0–2)
     * @param item předmět k vložení
     */
    public void setCraftingSlot(int index, ChestInventoryManager.ItemData item) {
        if (index >= 0 && index < craftingSlots.length) {
            craftingSlots[index] = item;
        }
    }

    /**
     * Určuje, zda daný název patří části klíče.
     *
     * @param name název předmětu
     * @return {@code true}, pokud je to "Key1", "Key2" nebo "Key3"
     */
    public boolean isKeyPart(String name) {
        return "Key1".equals(name) || "Key2".equals(name) || "Key3".equals(name);
    }

    /**
     * Určuje, zda daná část klíče se již nachází ve craftovacím slotu.
     *
     * @param name název části klíče
     * @return {@code true}, pokud je slot již obsažen touto částí
     */
    public boolean containsPart(String name) {
        for (ChestInventoryManager.ItemData d : craftingSlots)
            if (d != null && d.getName().equals(name)) return true;
        return false;
    }

    /**
     * Pokusí se vycraftit předmět "SilverKey" ze všech tří částí.
     * <p>
     * Pokud jsou přítomny Key1, Key2 a Key3, vytvoří nový předmět, přidá ho do inventáře hráče
     * a odstraní části z obou: craftovacích slotů i inventáře hráče.
     * </p>
     */
    public void craftSilverKey() {
        boolean hasKey1 = false, hasKey2 = false, hasKey3 = false;

        for (ChestInventoryManager.ItemData slot : craftingSlots) {
            if (slot == null) continue;
            switch (slot.getName()) {
                case "Key1" -> hasKey1 = true;
                case "Key2" -> hasKey2 = true;
                case "Key3" -> hasKey3 = true;
            }
        }

        if (!(hasKey1 && hasKey2 && hasKey3)) {
            gp.repaint();
            return;
        }

        gp.player.addItem(new ChestInventoryManager.ItemData("SilverKey", 1));
        System.out.println("CraftingTableUI: Crafted SilverKey and added to player inventory");

        for (int i = 0; i < craftingSlots.length; i++) {
            craftingSlots[i] = null;
        }

        String[] parts = {"Key1", "Key2", "Key3"};
        for (String part : parts) {
            for (int i = 0; i < gp.player.getInventory().size(); i++) {
                ChestInventoryManager.ItemData it = gp.player.getInventory().get(i);
                if (it.getName().equals(part)) {
                    if (it.getQuantity() > 1) {
                        it.setQuantity(it.getQuantity() - 1);
                    } else {
                        gp.player.getInventory().remove(i);
                    }
                    break;
                }
            }
        }

        gp.repaint();
    }

    /**
     * Vykreslí UI rozhraní pro craftovací stůl.
     *
     * @param g2d grafický kontext
     */
    public void draw(Graphics2D g2d) {
        if (!isShowing) return;

        int windowX = gp.screenWidth / 2 - 200;
        int windowY = gp.screenHeight / 2 - 100;
        int windowWidth = 400;
        int windowHeight = 200;

        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(windowX, windowY, windowWidth, windowHeight, 25, 25);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(windowX, windowY, windowWidth, windowHeight, 25, 25);

        int slotSize = 48;
        int offsetX = windowX + 50;
        int offsetY = windowY + 50;

        for (int i = 0; i < craftingSlots.length; i++) {
            int x = offsetX + i * (slotSize + 10);
            int y = offsetY;
            slotBounds[i] = new Rectangle(x, y, slotSize, slotSize);

            g2d.setColor(Color.GRAY);
            g2d.fillRect(x, y, slotSize, slotSize);
            g2d.setColor(Color.WHITE);
            g2d.drawRect(x, y, slotSize, slotSize);

            if (craftingSlots[i] != null) {
                GameObject item = craftingSlots[i].getItem();
                if (item.image != null) {
                    g2d.drawImage(item.image, x, y, slotSize, slotSize, null);
                }
            }
        }

        craftButtonBounds = new Rectangle(windowX + windowWidth - 100, windowY + windowHeight - 50, 80, 30);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(craftButtonBounds.x, craftButtonBounds.y, craftButtonBounds.width, craftButtonBounds.height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(craftButtonBounds.x, craftButtonBounds.y, craftButtonBounds.width, craftButtonBounds.height);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("Craft", craftButtonBounds.x + 20, craftButtonBounds.y + 20);
    }
}
