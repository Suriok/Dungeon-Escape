package cz.cvut.fel.pjv.golyakat.dungeon_escape.tile;

import java.awt.image.BufferedImage;

/**
 * Třída {@code Tile} reprezentuje jednu dlaždici (tile) na mapě herního světa.
 * <p>
 * Obsahuje grafický obrázek dlaždice a příznak, zda se přes tuto dlaždici
 * může entita (např. hráč nebo monstrum) pohybovat.
 * </p>
 */
public class Tile {

    /**
     * Obrázek představující vizuální podobu dlaždice.
     * <p>
     * Tento obrázek je vykreslován při vykreslování mapy.
     * </p>
     */
    public BufferedImage image;

    /**
     * Příznak, zda je dlaždice kolizní.
     * <ul>
     *     <li>{@code true} – entita nemůže dlaždicí projít (např. zeď)</li>
     *     <li>{@code false} – průchozí dlaždice (např. podlaha)</li>
     * </ul>
     */
    public boolean collision = false;
}
