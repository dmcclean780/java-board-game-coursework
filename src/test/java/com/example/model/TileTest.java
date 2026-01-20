package com.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    @Test
    public void defaultConstructor_initializesWithDefaults() {
        Tile t = new Tile();

        // Default values for fields
        assertNull(t.getTileID());
        assertEquals(0, t.getNumber());
        assertNull(t.getAdjVertices());
        assertFalse(t.getIsBlocked());
    }

    @Test
    public void setTileID_updatesValue() {
        Tile t = new Tile();

        t.setTileID("tile.brick");
        assertEquals("tile.brick", t.getTileID());

        t.setTileID(null);
        assertNull(t.getTileID());
    }

    @Test
    public void setNumber_updatesValue() {
        Tile t = new Tile();

        t.setNumber(6);
        assertEquals(6, t.getNumber());

        t.setNumber(0);
        assertEquals(0, t.getNumber());

        t.setNumber(-1);
        assertEquals(-1, t.getNumber()); // no validation in class
    }

    @Test
    public void setAdjVertices_updatesValue() {
        Tile t = new Tile();

        int[] vertices = {5, 6, 7};
        t.setAdjVertices(vertices);
        assertArrayEquals(vertices, t.getAdjVertices());

        t.setAdjVertices(null);
        assertNull(t.getAdjVertices());
    }

    @Test
    public void setIsBlocked_updatesValue() {
        Tile t = new Tile();

        assertFalse(t.getIsBlocked());

        t.setIsBlocked(true);
        assertTrue(t.getIsBlocked());

        t.setIsBlocked(false);
        assertFalse(t.getIsBlocked());
    }

    @Test
    public void adjVertices_arrayIsNotCopied() {
        int[] vertices = {10, 11, 12};
        Tile t = new Tile();
        t.setTileID("tile.ore");
        t.setNumber(5);
        t.setAdjVertices(vertices);
        t.setIsBlocked(false);

        // Modify original array
        vertices[0] = 99;

        // Tile reflects change (no defensive copy)
        assertEquals(99, t.getAdjVertices()[0]);
    }
}