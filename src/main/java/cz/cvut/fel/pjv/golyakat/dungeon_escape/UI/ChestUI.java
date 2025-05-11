package cz.cvut.fel.pjv.golyakat.dungeon_escape.UI;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.ChestInventoryManager;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.Object_Small_Chest;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Třída {@code ChestUI} slouží pro zobrazování a interakci s obsahem truhel ve hře.
 * <p>
 * Zajišťuje grafické vykreslení inventáře truhly a obsluhu jejího otevření nebo zavření.
 * </p>
 */
public class ChestUI {

    /** Hlavní herní panel, odkud získáváme hráče, velikost okna atd. */
    private gamePanel gp;

    /** Aktuálně otevřená truhla. */
    private Object_Small_Chest activeChest;

    /** Pole ohraničení položek zobrazených v truhle. */
    private Rectangle[] itemBounds;

    /** Ohraničení celého inventáře truhly. */
    private Rectangle chestBounds;

    /**
     * Inicializuje správce truhlového UI.
     *
     * @param gp instance hlavního herního panelu
     */
    public ChestUI(gamePanel gp) {
        this.gp = gp;
        this.activeChest = null;
        this.chestBounds = null;
    }

    /**
     * Otevře nebo zavře danou truhlu v závislosti na stavu.
     *
     * @param chest truhla, která se má otevřít nebo zavřít
     */
    public void openChest(Object_Small_Chest chest) {
        if (activeChest == chest && isShowingInventory()) {
            closeInventory();
        } else {
            if (activeChest != null) {
                closeInventory();
            }
            activeChest = chest;
            activeChest.open();
            gp.repaint();
        }
    }

    /**
     * Nastaví aktivní truhlu bez jejího zobrazení.
     *
     * @param chest truhla k nastavení
     */
    public void setActiveChest(Object_Small_Chest chest) {
        this.activeChest = chest;
    }

    /**
     * Zda je momentálně zobrazena inventářová obrazovka truhly.
     *
     * @return {@code true}, pokud je aktivní a otevřená
     */
    public boolean isShowingInventory() {
        return activeChest != null && activeChest.isShowingInventory();
    }

    /**
     * Zavře aktuální inventář truhly.
     */
    public void closeInventory() {
        if (activeChest != null) {
            activeChest.close();
            activeChest = null;
            chestBounds = null;
            gp.repaint();
        }
    }

    /**
     * Vykreslí uživatelské rozhraní truhly a její obsah.
     *
     * @param g2d grafický kontext
     */
    public void draw(Graphics2D g2d) {
        if (!isShowingInventory() || activeChest == null) {
            return;
        }

        BufferedImage inventoryImage = activeChest.getInventoryImage();
        if (inventoryImage == null) {
            System.out.println("ChestUI: No image found");
            return;
        }

        float scaleFactor = 4.0f;
        int imageWidth = (int) (inventoryImage.getWidth() * scaleFactor);
        int imageHeight = (int) (inventoryImage.getHeight() * scaleFactor);
        int windowX = gp.screenWidth / 2 - imageWidth / 2;
        int windowY = gp.screenHeight / 2 - imageHeight / 2;
        int windowWidth = imageWidth + 20;
        int windowHeight = imageHeight + 20;

        chestBounds = new Rectangle(windowX - 10, windowY - 10, windowWidth, windowHeight);

        // Pozadí a rámeček
        g2d.setColor(new Color(0, 0, 0, 200));
        g2d.fillRoundRect(windowX - 10, windowY - 10, windowWidth, windowHeight, 25, 25);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(windowX - 10, windowY - 10, windowWidth, windowHeight, 25, 25);

        // Obrázek truhly
        g2d.drawImage(inventoryImage, windowX, windowY, imageWidth, imageHeight, null);

        // Nadpis
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        String title = "Chest Inventory";
        int titleX = windowX + windowWidth / 2 - g2d.getFontMetrics().stringWidth(title) / 2;
        int titleY = windowY - 25;
        g2d.drawString(title, titleX, titleY);

        // Položky v truhle
        List<ChestInventoryManager.ItemData> items = activeChest.getItems();
        itemBounds = new Rectangle[items.size()];
        int gridSize = 4;
        int cellWidth = imageWidth / gridSize;
        int cellHeight = imageHeight / gridSize;
        int itemSize = Math.min(cellWidth, cellHeight) - 10;
        int offsetX = windowX + 5;
        int offsetY = windowY + 5;

        for (int i = 0; i < items.size(); i++) {
            int row = i / gridSize;
            int col = i % gridSize;
            ChestInventoryManager.ItemData item = items.get(i);
            BufferedImage itemImage = item.getItem().image;
            if (itemImage != null) {
                int x = offsetX + col * cellWidth + (cellWidth - itemSize) / 2;
                int y = offsetY + row * cellHeight + (cellHeight - itemSize) / 2;

                int drawSize = itemSize;
                boolean isKeyPart = item.getName().equals("Key1") ||
                        item.getName().equals("Key2") ||
                        item.getName().equals("Key3");
                if (isKeyPart) {
                    drawSize = (int)(itemSize * 0.6667f);
                    x += (itemSize - drawSize) / 2;
                    y += (itemSize - drawSize) / 2;
                }

                g2d.drawImage(itemImage, x, y, drawSize, drawSize, null);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                g2d.setColor(Color.WHITE);
                String quantityText = "x" + item.getQuantity();
                int textX = x + drawSize - g2d.getFontMetrics().stringWidth(quantityText) - 2;
                int textY = y + drawSize - 2;
                g2d.drawString(quantityText, textX, textY);

                itemBounds[i] = new Rectangle(x, y, drawSize, drawSize);
            }
        }
    }

    /**
     * Vrací pole obdélníků s pozicemi položek v aktuálně otevřené truhle.
     *
     * @return pole {@link Rectangle} odpovídající jednotlivým položkám
     */
    public Rectangle[] getItemBounds() {
        return itemBounds != null ? itemBounds : new Rectangle[0];
    }

    /**
     * Vrací aktuálně aktivní (otevřenou) truhlu.
     *
     * @return instance {@link Object_Small_Chest} nebo {@code null}
     */
    public Object_Small_Chest getActiveChest() {
        return activeChest;
    }

    /**
     * Vrací obdélník pokrývající celé UI truhly.
     *
     * @return {@link Rectangle} nebo {@code null}, pokud není truhla zobrazena
     */
    public Rectangle getChestBounds() {
        return chestBounds;
    }
}
