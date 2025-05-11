package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.items_chest.key.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.leather.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.armour.iron.*;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.weapon.*;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;

/**
 * Třída {@code ChestInventoryManager} spravuje obsah a stav truhlic ve hře.
 * <p>
 * Umožňuje ukládání a načítání dat o truhlách, včetně jejich předmětů.
 * Každá truhla má své ID a seznam předmětů.
 * </p>
 */
public class ChestInventoryManager {

    /**
     * Mapa všech známých truhel ve hře, klíčem je jejich unikátní ID.
     */
    private final Map<Integer, ChestData> chestDataMap = new HashMap<>();

    /**
     * Konstruktor správce inventáře truhlic. Zatím neprovádí žádnou inicializaci.
     */
    public ChestInventoryManager() {}

    /**
     * Třída {@code ChestData} reprezentuje jeden konkrétní stav truhly:
     * zda byla otevřena a jaké obsahuje předměty.
     */
    public static class ChestData implements Serializable {

        /**
         * Příznak označující, zda je truhla otevřená.
         */
        private boolean isOpen;

        /**
         * Seznam předmětů, které jsou v truhle uložené.
         */
        private List<ItemData> items;

        /**
         * Vytváří nový objekt truhly se stavem a seznamem předmětů.
         *
         * @param isOpen zda je truhla otevřená
         * @param items seznam položek uvnitř
         */
        public ChestData(boolean isOpen, List<ItemData> items) {
            this.isOpen = isOpen;
            this.items = items;
        }

        /**
         * Vrací stav otevřenosti truhly.
         *
         * @return {@code true}, pokud je truhla otevřená
         */
        public boolean isOpen() {
            return isOpen;
        }

        /**
         * Vrací seznam předmětů v truhle.
         *
         * @return seznam {@link ItemData}
         */
        public List<ItemData> getItems() {
            return items;
        }
    }

    /**
     * Třída {@code ItemData} představuje jeden konkrétní předmět,
     * jeho název, množství a odkaz na samotný objekt {@link GameObject}.
     */
    public static class ItemData implements Serializable {

        private String name;
        private int quantity;

        /**
         * Předmět ve hře – instanci třídy {@link GameObject}.
         * Je označen jako {@code transient}, protože se nesérializuje.
         */
        private transient GameObject item;

        /**
         * Vytváří nový záznam předmětu s daným názvem a počtem.
         *
         * @param name název položky
         * @param quantity počet kusů
         */
        public ItemData(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
            this.item = createItemFromName(name);
        }

        /** @return název položky */
        public String getName() { return name; }

        /** @return počet kusů položky */
        public int getQuantity() { return quantity; }

        /** @param quantity nastaví nové množství položky */
        public void setQuantity(int quantity) { this.quantity = quantity; }

        /**
         * Vrací herní objekt spojený s touto položkou.
         * Pokud není inicializovaný, vytvoří jej podle názvu.
         *
         * @return instance {@link GameObject}
         */
        public GameObject getItem() {
            if (item == null) {
                item = createItemFromName(name);
            }
            return item;
        }

        /** @param item ručně nastaví objekt položky */
        public void setItem(GameObject item) {
            this.item = item;
        }
    }

    /**
     * Vytvoří konkrétní instanci předmětu na základě jeho názvu.
     *
     * @param name název předmětu jako string
     * @return odpovídající instance {@link GameObject}, nebo {@code null}, pokud není známý
     */
    private static GameObject createItemFromName(String name) {
        switch (name) {
            case "Apple": return new Item_Apple();
            case "blubbery": return new Item_Blubbery();
            case "potion": return new Item_HealthePotion();
            case "leather_pants": return new leather_pants();
            case "leather_bib": return new leather_bib();
            case "leather_helmet": return new leather_helmet();
            case "leather_boots": return new leather_boots();
            case "iron_pants": return new iron_pants();
            case "iron_helmet": return new iron_helmet();
            case "iron_boots": return new iron_boots();
            case "iron_bib": return new iron_bib();
            case "iron_sword": return new Iron_sword(2);
            case "emerald_sword": return new Emerald_sword(3);
            case "Key": return new Item_Key();
            case "Key1": return new Item_partKey1();
            case "Key2": return new Item_partKey2();
            case "Key3": return new Item_partKey3();
            case "SilverKey": return new Item_SilverKey();
            default:
                GameLogger.error("Unknown item name: " + name);
                return null;
        }
    }

    /**
     * Vytvoří nová data truhly se zadanými výchozími předměty.
     *
     * @param id ID truhly
     * @param defaultItems mapa názvů a množství výchozích předmětů
     * @return nově vytvořená data truhly
     */
    public ChestData getChestData(int id, Map<String, Integer> defaultItems) {
        List<ItemData> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : defaultItems.entrySet()) {
            ItemData itemData = new ItemData(entry.getKey(), entry.getValue());
            if (itemData.getItem() != null) {
                items.add(itemData);
            }
        }
        ChestData chestData = new ChestData(false, items);
        chestDataMap.put(id, chestData);
        return chestData;
    }

    /**
     * Aktualizuje stav truhly v interní mapě a uloží data na disk.
     *
     * @param id ID truhly
     * @param chestData aktualizovaná data truhly
     */
    public void updateChestData(int id, ChestData chestData) {
        chestDataMap.put(id, chestData);
        saveChestData();
    }

    /**
     * Uloží všechna data o truhlách do souboru na disku.
     * <p>
     * Aktuálně používá objektovou serializaci do souboru {@code chest_data.xml}.
     * </p>
     */
    public void saveChestData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("chest_data.xml"))) {
            oos.writeObject(chestDataMap);
            GameLogger.info("Chest data saved successfully.");
        } catch (IOException e) {
            GameLogger.error("Error saving chest data: " + e.getMessage());
        }
    }

    /**
     * Načte data o truhlách ze souboru – zatím neimplementováno.
     * <p>V budoucnu bude číst z uloženého souboru.</p>
     */
    private void loadChestData() {
        GameLogger.info("Starting fresh, no previous chest data loaded.");
    }

    /**
     * Provede zadanou akci nad každou truhlou v mapě.
     *
     * @param action funkce přebírající ID a seznam předmětů
     */
    public void forEachChest(BiConsumer<Integer, List<ItemData>> action) {
        chestDataMap.forEach((id, chest) -> action.accept(id, chest.items));
    }

    /**
     * Nahradí obsah konkrétní truhly novým seznamem předmětů.
     * <p>
     * Používá se při načítání uložené hry.
     * </p>
     *
     * @param id ID truhly
     * @param items nový seznam předmětů
     */
    public void overrideChest(int id, List<ItemData> items) {
        chestDataMap.put(id, new ChestData(true, items));
    }

    /**
     * Resetuje veškerá data o truhlách – použije se při začátku nové hry.
     */
    public void resetChestData() {
        chestDataMap.clear();
    }
}
