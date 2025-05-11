package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * Třída {@code Object_DoorSide} reprezentuje boční dveře ve hře,
 * které lze otevřít buď interakcí, nebo odemknout pomocí klíče.
 * <p>
 * Dveře mají kolizní oblast, rozdílné obrázky pro otevřený/zavřený stav
 * a mohou vyžadovat {@code Key} pro odemčení.
 * </p>
 */
public class Object_DoorSide extends GameObject {

    /**
     * Příznak, zda tyto dveře vyžadují klíč pro otevření.
     */
    public boolean requiresKey = false;

    /**
     * Příznak, zda jsou dveře aktuálně otevřené.
     */
    private boolean isOpen = false;

    /**
     * Obrázek zavřených dveří.
     */
    private BufferedImage closedImage;

    /**
     * Obrázek otevřených dveří.
     */
    private BufferedImage openImage;

    /**
     * Konstruktor inicializuje dveře, načte obrázky a nastaví výchozí stav.
     */
    public Object_DoorSide() {
        name = "DoorSide";
        Collision = true;
        solidArea = new java.awt.Rectangle(0, 0, 48, 48); // Velikost tile
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        try {
            // Načtení obrázku zavřených dveří
            BufferedImage tempClosed = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side.png"));
            if (tempClosed == null) {
                System.err.println("door_side.png nenalezen – používá se záložní prázdný obrázek.");
                tempClosed = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            closedImage = tempClosed;

            // Načtení obrázku otevřených dveří
            BufferedImage tempOpen = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side_open.png"));
            if (tempOpen == null) {
                System.err.println("door_side_open.png nenalezen – použije se zavřený obrázek.");
                tempOpen = closedImage;
            }
            openImage = tempOpen;

            // Výchozí stav – dveře zavřené
            image = closedImage;

        } catch (Exception e) {
            System.err.println("Chyba při načítání obrázků dveří: " + e.getMessage());
            e.printStackTrace();
            closedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            openImage = closedImage;
            image = closedImage;
        }

        System.out.println("Object_DoorSide inicializován: requiresKey=" + requiresKey + ", isOpen=" + isOpen);
    }

    /**
     * Pokusí se otevřít dveře interakcí hráče.
     * <p>
     * Pokud dveře nevyžadují klíč, otevřou se a odstraní kolizi.
     * Pokud je potřeba klíč, zůstávají zavřené.
     * </p>
     */
    public void interact() {
        if (!requiresKey) {
            isOpen = true;
            image = openImage;
            Collision = false;
            System.out.println("DoorSide byl otevřen hráčem.");
        } else {
            System.out.println("Tyto dveře vyžadují klíč.");
        }
    }

    /**
     * Odemkne dveře pomocí klíče – nastaví otevřený stav a deaktivuje kolizi.
     */
    public void unlock() {
        isOpen = true;
        image = openImage;
        Collision = false;
        System.out.println("DoorSide byl odemčen pomocí klíče!");
    }

    /**
     * Zda jsou dveře aktuálně otevřené.
     *
     * @return {@code true}, pokud jsou dveře otevřené
     */
    public boolean isOpen() {
        return isOpen;
    }
}
