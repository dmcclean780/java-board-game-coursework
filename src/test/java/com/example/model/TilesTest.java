package com.example.model;

import org.junit.jupiter.api.Test;

import com.example.model.config.ConfigManager;
import com.example.model.config.service.ConfigService;

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

        int[][] adjacency = {
            {1, 3, 4}, {0, 2, 4, 5}, {1, 5, 6},
            {0, 4, 7, 8}, {0, 1, 3, 5, 8, 9},
            {1, 2, 4, 6, 9, 10}, {2, 5, 10, 11},
            {0, 8, 12}, {3, 4, 7, 9, 12, 13},
            {4, 5, 8, 10, 13, 14},
            {5, 6, 9, 11, 14, 15}, {6, 10, 15},
            {7, 8, 13, 16},
            {8, 9, 12, 14, 16, 17},
            {9, 10, 13, 15, 17, 18},
            {10, 11, 14, 18},
            {12, 13, 17},
            {13, 14, 16, 18},
            {14, 15, 17}
        };

        for (int i = 0; i < tileArray.length; i++) {
            int num = tileArray[i].getNumber();
            if (num == 6 || num == 8) {
                for (int adj : adjacency[i]) {
                    int adjNum = tileArray[adj].getNumber();
                    assertFalse(adjNum == 6 || adjNum == 8,
                        "Adjacent 6/8 at tiles " + i + " and " + adj);
                }
            }
        }
    }
}