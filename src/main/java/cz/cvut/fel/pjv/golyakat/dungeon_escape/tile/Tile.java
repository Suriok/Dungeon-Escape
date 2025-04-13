package cz.cvut.fel.pjv.golyakat.dungeon_escape.tile;

import java.awt.image.BufferedImage;

// Třída Tile reprezentuje jednu dlaždici na mapě
public class Tile {

    public BufferedImage image; // Obrázek dlaždice (jak vypadá vizuálně)

    public boolean collision = false; // Označuje, jestli má dlaždice kolizi (true = neprochodná, false = volný průchod)
}
