package cz.cvut.fel.pjv.golyakat.dungeon_escape.tile;

import java.awt.image.BufferedImage;

/**
 * The {@code Tile} class represents a single tile on the game world map.
 * <p>
 * It contains the tile's image and a flag indicating whether
 * an entity (e.g., player or monster) can move through it.
 * </p>
 */
public class Tile {

    /**
     * The image representing the visual appearance of the tile.
     * <p>
     * This image is rendered when the map is drawn.
     * </p>
     */
    public BufferedImage image;

    /**
     * Flag indicating whether the tile is collidable.
     * <ul>
     *     <li>{@code true} – the entity cannot pass through the tile (e.g., a wall)</li>
     *     <li>{@code false} – passable tile (e.g., floor)</li>
     * </ul>
     */
    public boolean collision = false;
}
