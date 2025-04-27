package cz.cvut.fel.pjv.golyakat.dungeon_escape.bars;

import cz.cvut.fel.pjv.golyakat.dungeon_escape.gamePanel;
import cz.cvut.fel.pjv.golyakat.dungeon_escape.object.GameObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

// Třída reprezentující health bar (ukazatel zdraví hráče)
public class HealthBar extends GameObject {
    // Obrázky pro různé stavy srdce (plné, částečné zásahy, prázdné)
    private BufferedImage fullHp, hit1, hit2, hit3, die;

    private gamePanel gp; // Odkaz na herní panel pro získání např. velikosti dlaždice
    private int maxHp = 8; // Maximální počet "života" (např. 4 plná srdce po 2 HP)
    private int currentHp; // Aktuální život hráče

    // Konstruktor health baru
    public HealthBar(gamePanel gp) {
        this.gp = gp;
        this.currentHp = maxHp; // Na začátku má hráč plné zdraví
        loadImages(); // Načtení obrázků health baru
    }

    // Načte všechny obrázky health baru z resource složky
    private void loadImages() {
        try {
            fullHp = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/Full_Hp.jpg"));
            hit1 = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/1_hit.jpg"));
            hit2 = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/2_hit.jpg"));
            hit3 = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/3_hit.jpg"));
            die = ImageIO.read(getClass().getResourceAsStream("/cz/cvut/fel/pjv/golyakat/dungeon_escape/objects/heath_bar/die.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metoda pro aktualizaci hodnoty aktuálního zdraví hráče
    public void update(int playerHp) {
        this.currentHp = playerHp;
    }

    // Metoda pro vykreslení health baru na obrazovku
    public void draw(Graphics2D g2) {
        int x = 10; // Počáteční X pozice health baru
        int y = 10; // Počáteční Y pozice health baru
        int spacing = 1; // Mezery mezi srdci

        // Spočítáme, kolik je plných srdcí (každé plné srdce = 2 HP)
        int fullHearts = currentHp / 2;
        int remainingHp = currentHp % 2; // Pokud HP není dělitelné dvěma, je potřeba vykreslit částečné srdce

        // Vykreslení plných srdcí
        for (int i = 0; i < fullHearts; i++) {
            g2.drawImage(fullHp, x + (i * (gp.tileSize + spacing)), y, gp.tileSize, gp.tileSize, null);
        }

        // Pokud zbyla část HP, vykreslíme částečné srdce (např. po zásahu)
        if (remainingHp > 0) {
            BufferedImage partialHeart = getPartialHeartImage(remainingHp);
            g2.drawImage(partialHeart, x + (fullHearts * (gp.tileSize + spacing)), y, gp.tileSize, gp.tileSize, null);
        }

        // Vykreslení prázdných srdcí pro zbývající sloty
        int emptyHearts = (maxHp / 2) - fullHearts - (remainingHp > 0 ? 1 : 0);
        for (int i = 0; i < emptyHearts; i++) {
            g2.drawImage(die, x + ((fullHearts + (remainingHp > 0 ? 1 : 0) + i) * (gp.tileSize + spacing)), y, gp.tileSize, gp.tileSize, null);
        }
    }

    // Vrátí správný obrázek pro částečné srdce podle zbylého HP
    private BufferedImage getPartialHeartImage(int remainingHp) {
        switch (remainingHp) {
            case 1:
                return hit1; // Například 1/2 srdce
            case 2:
                return hit2; // Pokud máš custom "2_hit" obrázek (není běžné u 2 HP, protože 2 HP = celé srdce)
            case 3:
                return hit3; // Pokud by HP bylo ve větších krocích
            default:
                return die; // Pokud něco selže, vykreslíme prázdné srdce
        }
    }
}
