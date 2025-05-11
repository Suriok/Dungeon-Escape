package cz.cvut.fel.pjv.golyakat.dungeon_escape;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Datová třída reprezentující strukturu uložené hry.
 * <p>
 * Umožňuje serializaci/deserializaci pomocí knihovny Jackson ve formátu XML.
 * Obsahuje informace o hráči, monstrech, truhlách a aktuální mapě.
 * </p>
 */
@JacksonXmlRootElement(localName = "save")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveData {

    /**
     * Index aktuálně aktivní mapy.
     * Používá se k přepínání mezi různými úrovněmi.
     */
    @JacksonXmlProperty(isAttribute = true)
    public int currentMap;

    /**
     * Pole příznaků označujících, zda již byly jednotlivé úrovně "spawnuté"
     * (např. vygenerování monster pouze při prvním vstupu).
     */
    @JacksonXmlElementWrapper(localName = "levelsSpawned")
    @JacksonXmlProperty(localName = "level")
    public boolean[] levelSpawned;

    /**
     * Vnitřní datová třída reprezentující předmět v inventáři nebo truhle.
     */
    public static class ItemData {
        /**
         * Název předmětu.
         */
        @JacksonXmlProperty(isAttribute = true)
        public String name;

        /**
         * Množství daného předmětu.
         */
        @JacksonXmlProperty(isAttribute = true)
        public int qty;

        /**
         * Výchozí konstruktor (nutný pro deserializaci).
         */
        public ItemData() {}

        /**
         * Konstruktor nastavující název a množství.
         *
         * @param name název předmětu
         * @param qty počet kusů
         */
        public ItemData(String name, int qty) {
            this.name = name;
            this.qty = qty;
        }
    }

    /**
     * Datová třída uchovávající informace o hráči.
     */
    public static class PlayerData {
        /**
         * X-ová pozice hráče ve světových souřadnicích.
         */
        public int worldX;

        /**
         * Y-ová pozice hráče ve světových souřadnicích.
         */
        public int worldY;

        /**
         * Aktuální počet životů hráče.
         */
        public int life;

        /**
         * Seznam předmětů v batohu hráče.
         */
        @JacksonXmlElementWrapper(localName = "backpack")
        @JacksonXmlProperty(localName = "backpackItem")
        public List<ItemData> backpack;

        /**
         * Seznam kusů brnění nasazených hráčem.
         */
        @JacksonXmlElementWrapper(localName = "armor")
        @JacksonXmlProperty(localName = "armorItem")
        public List<ItemData> armor;

        /**
         * Aktuálně vybraná zbraň hráče.
         */
        public ItemData weapon;

        /**
         * Úroveň nebo vylepšení hráče.
         */
        public ItemData grade;

        /**
         * Výchozí konstruktor – inicializuje prázdný batoh i brnění.
         */
        public PlayerData() {
            backpack = new ArrayList<>();
            armor = new ArrayList<>();
        }
    }

    /**
     * Datová třída reprezentující jedno monstrum ve hře.
     */
    public static class MonsterData {
        /**
         * Typ monstra (např. "zombie", "skeleton").
         */
        @JacksonXmlProperty(isAttribute = true)
        public String type;

        /**
         * X-ová pozice monstra.
         */
        public int worldX;

        /**
         * Y-ová pozice monstra.
         */
        public int worldY;

        /**
         * Aktuální počet životů monstra.
         */
        public int life;

        /**
         * Příznak, zda je monstrum mrtvé.
         */
        public boolean dead;
    }

    /**
     * Datová třída reprezentující jednu truhlu na mapě.
     */
    public static class ChestData {
        /**
         * Jedinečné ID truhly, které slouží k jejímu rozpoznání.
         */
        @JacksonXmlProperty(isAttribute = true)
        public int id;

        /**
         * Seznam předmětů uložených v této truhle.
         */
        @JacksonXmlElementWrapper(localName = "items")
        @JacksonXmlProperty(localName = "item")
        public List<ItemData> items;

        /**
         * Výchozí konstruktor – inicializuje prázdný seznam předmětů.
         */
        public ChestData() {
            items = new ArrayList<>();
        }
    }

    /**
     * Objekt uchovávající kompletní stav hráče.
     */
    public PlayerData player;

    /**
     * Seznam všech monster aktuálně existujících na mapě.
     */
    @JacksonXmlElementWrapper(localName = "monsters")
    @JacksonXmlProperty(localName = "monster")
    public List<MonsterData> monsters;

    /**
     * Seznam všech truhl na aktuální mapě a jejich obsahu.
     */
    @JacksonXmlElementWrapper(localName = "chests")
    @JacksonXmlProperty(localName = "chest")
    public List<ChestData> chests;

    /**
     * Výchozí konstruktor – inicializuje prázdného hráče a prázdné seznamy monster a truhel.
     */
    public SaveData() {
        player = new PlayerData();
        monsters = new ArrayList<>();
        chests = new ArrayList<>();
    }
}
