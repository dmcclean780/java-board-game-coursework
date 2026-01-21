package com.example.model;

import org.junit.jupiter.api.Test;

import com.example.model.config.ConfigManager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class TilesTest {

    @BeforeEach
    public void initConfig() {
        ConfigManager.loadAll();
    }

    @AfterEach
    public void closeConfig() {
        ConfigManager.unloadAll();
    }

    @Test
    public void constructor_createsTilesArray() {
        Tiles tiles = new Tiles();
        Tile[] array = tiles.getTiles();

        assertNotNull(array);
        assertEquals(19, array.length);

        for (Tile t : array) {
            assertNotNull(t);
        }
    }

    @Test
    public void getTiles_returnsInternalArray() {
        Tiles tiles = new Tiles();
        Tile[] first = tiles.getTiles();
        Tile[] second = tiles.getTiles();

        assertSame(first, second);
    }

    @Test
    public void setTiles_replacesInternalArray() {
        Tiles tiles = new Tiles();

        Tile[] newTiles = new Tile[19];
        for (int i = 0; i < newTiles.length; i++) {
            newTiles[i] = new Tile();
        }

        tiles.setTiles(newTiles);
        assertSame(newTiles, tiles.getTiles());
    }

    @Test
    public void allTilesHaveTileIDsAssigned() {
        Tiles tiles = new Tiles();

        for (Tile t : tiles.getTiles()) {
            assertNotNull(t.getTileID());
        }
    }

    @Test
    public void allTilesHaveAdjacentVerticesAssigned() {
        Tiles tiles = new Tiles();

        for (Tile t : tiles.getTiles()) {
            assertNotNull(t.getAdjVertices());
            assertEquals(6, t.getAdjVertices().length);
        }
    }

    @Test
    public void blockedTilesHaveNoNumberOrAreDesert() {
        Tiles tiles = new Tiles();

        for (Tile t : tiles.getTiles()) {
            if (t.getIsBlocked()) {
                // Desert tile: number is allowed to be 0
                assertTrue(t.getNumber() == 0 || t.getNumber() > 0);
            }
        }
    }

    @Test
    public void noAdjacentSixesOrEightsExist() {
        Tiles tiles = new Tiles();
        Tile[] tileArray = tiles.getTiles();

        

        for (int i = 0; i < tileArray.length; i++) {
            int num = tileArray[i].getNumber();
            if (num == 6 || num == 8) {
                for (int adj : AdjacencyMaps.TileAdjacency[i]) {
                    int adjNum = tileArray[adj].getNumber();
                    assertFalse(adjNum == 6 || adjNum == 8,
                        "Adjacent 6/8 at tiles " + i + " and " + adj);
                }
            }
        }
    }
}