package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Třída {@code Object_DoorFront} reprezentuje přední (čelní) dveře ve hře,
 * které lze otevřít buď přímo, nebo pomocí speciálního klíče – např. SilverKey.
 * <p>
 * Obsahuje grafiku pro zavřený i otevřený stav, kolizní logiku a podporu interakce hráče.
 * </p>
 */
public class Object_DoorFront extends GameObject {

    /**
     * Určuje, zda dveře vyžadují klíč (např. SilverKey) k otevření.
     */
    public boolean requiresKey = false;

    /**
     * Příznak označující, zda jsou dveře otevřené.
     */
    private boolean isOpen = false;

    /**
     * Obrázek otevřených dveří.
     */
    private BufferedImage openImage;

    /**
     * Vytvoří novou instanci dveří, načte obrázky a nastaví výchozí stav (zavřeno).
     */
    public Object_DoorFront() {
        name = "DoorFront";
        Collision = true; // Kolizní, pokud jsou zavřené
        solidArea = new java.awt.Rectangle(0, 0, 48, 48); // Předpoklad: tileSize = 48
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        /*
          Obrázek zavřených dveří.
         */
        BufferedImage closedImage;
        try {
            // Načtení obrázku pro zavřené dveře
            BufferedImage tempClosed = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_front.png")));
            if (tempClosed == null) {
                GameLogger.error("Nepodařilo se načíst door_front.png");
                tempClosed = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            }
            closedImage = tempClosed;

            // Načtení obrázku pro otevřené dveře (recykluje se obrázek bočních dveří)
            BufferedImage tempOpen = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/door_side.png")));
            if (tempOpen == null) {
                GameLogger.error("Nepodařilo se načíst door_side.png pro otevřené dveře");
                tempOpen = closedImage;
            }
            openImage = tempOpen;

            image = closedImage;
        } catch (Exception e) {
            GameLogger.error("Chyba při načítání obrázků pro dveře: " + e.getMessage());
            closedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            openImage = closedImage;
            image = closedImage;
        }
    }

    /**
     * Zpracovává interakci hráče s dveřmi.
     * <ul>
     *     <li>Pokud dveře nevyžadují klíč, otevřou se ihned.</li>
     *     <li>Pokud dveře vyžadují klíč, pouze oznámí nutnost jeho použití.</li>
     * </ul>
     */
    public void interact() {
        if (!requiresKey && !isOpen) {
            isOpen = true;
            image = openImage;
            Collision = false;
            GameLogger.info("DoorFront otevřen!");
        } else if (requiresKey && !isOpen) {
            GameLogger.info("Tyto dveře vyžadují Silver Key.");
        }
    }

    /**
     * Vrací, zda jsou dveře otevřené.
     *
     * @return {@code true}, pokud jsou otevřené
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * Odemkne dveře, pokud vyžadují klíč a jsou zavřené.
     * <p>
     * Nastaví obrázek otevřených dveří a vypne kolizi.
     * </p>
     */
    public void unlock() {
        if (requiresKey && !isOpen) {
            isOpen = true;
            image = openImage;
            Collision = false;
            GameLogger.info("DoorFront byl odemčen pomocí Silver Key.");
        }
    }
}
