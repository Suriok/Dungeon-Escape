package cz.cvut.fel.pjv.golyakat.dungeon_escape.object;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.GameLogger;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Třída {@code Object_CraftingTable} reprezentuje objekt craftovacího stolu,
 * který slouží k výrobě nových předmětů ze surovin v inventáři.
 * <p>
 * Obsahuje obrázek stolu a logiku pro vykreslení na mapě.
 * </p>
 */
public class Object_CraftingTable extends GameObject {

    /**
     * Vytvoří novou instanci objektu craftovacího stolu.
     * <p>
     * Pokusí se načíst obrázek stolu ze souboru.
     * Pokud se nepodaří, vytvoří záložní obrázek (modrý čtverec).
     * </p>
     */
    public Object_CraftingTable() {
        name = "CraftingTable";
        Collision = true;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(
                    "/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/crafting_tabel.png"));

            if (image == null) {
                GameLogger.error("Nepodařilo se načíst crafting_tabel.png pro Object_CraftingTable");
                image = createFallbackImage();
            }

        } catch (Exception e) {
            GameLogger.error("Chyba při načítání obrázku CraftingTable: " + e.getMessage());
            e.printStackTrace();
            image = createFallbackImage();
        }
    }

    /**
     * Pomocná metoda pro vytvoření záložního (fallback) obrázku, pokud se nepodaří načíst originál.
     *
     * @return modrý čtverec jako náhradní obrázek
     */
    private BufferedImage createFallbackImage() {
        BufferedImage fallback = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = fallback.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 48, 48);
        g2d.dispose();
        return fallback;
    }

    /**
     * Vykreslí craftovací stůl na obrazovku, pokud je v zobrazitelném rozsahu vůči hráči.
     *
     * @param g2d grafický kontext
     * @param gp  hlavní panel hry, ze kterého se berou informace o hráči a zobrazení
     */
    @Override
    public void draw(Graphics2D g2d, gamePanel gp) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Vykreslení pouze pokud je viditelný na obrazovce
        if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {

            if (image != null) {
                g2d.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
            } else {
                // Nouzové vykreslení, pokud chybí obrázek
                g2d.setColor(Color.RED);
                g2d.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                g2d.setColor(Color.BLACK);
                g2d.drawString("CraftingTable", screenX + 5, screenY + gp.tileSize / 2);
                GameLogger.error("Nelze vykreslit CraftingTable: obrázek je null");
            }
        }
    }
}
