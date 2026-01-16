package com.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import java.util.Map;
import java.util.Random;

import com.example.model.config.service.ConfigService;

public class Tiles {

    private Tile[] tiles;

    // Board size, change if a bigger/smaller board is desired
    // 19 is a normal 3 hex-per-side board
    private final int NUMBER_OF_HEXES = 19;

    public Tiles() {
        this.tiles = setUpTiles();
    }

    // getter
    public Tile[] getTiles() {
        return this.tiles;
    }

    // setter
    public void setTiles(Tile[] _tiles) {
        this.tiles = _tiles;
    }

    // set up all the tiles in the gameboard
    private Tile[] setUpTiles() {
        Tile[] tiles = new Tile[NUMBER_OF_HEXES];

        // Instantiate tiles
        for (int i = 0; i < NUMBER_OF_HEXES; i++) {
            tiles[i] = new Tile();
        }

        // Build the bag of tile IDs with correct quantities
        ArrayList<String> tileBag = new ArrayList<>();
        for (String tileID : ConfigService.getAllTileIDs()) {
            int maxQty = ConfigService.getTile(tileID).maxQuantity;
            for (int j = 0; j < maxQty; j++) {
                tileBag.add(tileID);
            }
        }

        // Shuffle the bag
        java.util.Collections.shuffle(tileBag);

        // Assign shuffled tiles to the tile array
        for (int i = 0; i < NUMBER_OF_HEXES; i++) {
            tiles[i].setTileID(tileBag.get(i));
        }

        // Assign numbers and block deserts
        int[] numberSequence = generateTileNumberSequence(); // should have 19 numbers, 0 for deserts
        int numberIndex = 0;

        for (int i = 0; i < NUMBER_OF_HEXES; i++) {
            Tile tile = tiles[i];
            String resourceID = ConfigService.getTile(tile.getTileID()).resourceID;

            if (resourceID.isEmpty()) { // desert
                tile.setIsBlocked(true);
                tile.setNumber(0); // desert has no number
            } else {
                tile.setIsBlocked(false);
                tile.setNumber(numberSequence[numberIndex]);
                numberIndex++;
            }
        }

        // Set adjacency info
        return setAdjVerticesForEachTile(tiles);
    }

    private int[] generateTileNumberSequence() {
        Map<Integer, Integer> numberTokens = ConfigService.getNumberTokens();
        int NUMBER_OF_TOKENS = numberTokens.values().stream().mapToInt(Integer::intValue).sum();
        int[] sequence = new int[NUMBER_OF_TOKENS];

        int sizeOfSequence = 0;
        for (int key : numberTokens.keySet()) {
            int occurences = numberTokens.get(key);
            for (int i = 0; i < occurences; i++) {
                sequence[sizeOfSequence] = key;
                sizeOfSequence++;
            }
        }

        return generateValidLayout(sequence);
    }

    private static final int MAX_ATTEMPTS = 30000000;

    private static int[] generateValidLayout(int[] tokens) {
        Random rand = new Random();

        // Convert int[] → mutable List<Integer>
        List<Integer> layout = new ArrayList<>();
        for (int token : tokens) {
            layout.add(token);
        }

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            Collections.shuffle(layout, rand);

            if (isValidLayout(layout)) {
                layout.remove(Integer.valueOf(-1)); // remove desert placeholder
                return layout.stream().mapToInt(Integer::intValue).toArray();
            }
        }

        throw new RuntimeException(
                "Couldn't find a valid layout after " + MAX_ATTEMPTS + " attempts");
    }

    private static boolean isValidLayout(List<Integer> layout) {
        for (int i = 0; i < layout.size(); i++) {
            if (layout.get(i) == 6 || layout.get(i) == 8) {
                for (int adj : ADJACENCY[i]) {
                    if (layout.get(adj) == 6 || layout.get(adj) == 8) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Tile[] setAdjVerticesForEachTile(Tile[] tiles) {
        for (int i = 0; i < VERTICES.length; i++) {
            tiles[i].setAdjVertices(VERTICES[i]);
        }

        return tiles;
    }

    public static int[][] getTileVertices() {
        return VERTICES;
    }

    // { Top, URight, DRight, Down, DLeft, ULeft }
    // This is Joshua's changed version to work with UI Code, old one in numerical
    // order is found below
    private static final int[][] VERTICES = {
            { 1, 2, 10, 9, 8, 0 }, // tile 0
            { 3, 4, 12, 11, 10, 2 }, // tile 1
            { 5, 6, 14, 13, 12, 4 }, // tile 2
            { 8, 9, 19, 18, 17, 7 }, // tile 3
            { 10, 11, 21, 20, 19, 9 }, // tile 4
            { 12, 13, 23, 22, 21, 11 }, // tile 5
            { 14, 15, 25, 24, 23, 13 }, // tile 6
            { 17, 18, 29, 28, 27, 16 }, // tile 7
            { 19, 20, 31, 30, 29, 18 }, // tile 8
            { 21, 22, 33, 32, 31, 20 }, // tile 9
            { 23, 24, 35, 34, 33, 22 }, // tile 10
            { 25, 26, 37, 36, 35, 24 }, // tile 11
            { 29, 30, 40, 39, 38, 28 }, // tile 12
            { 31, 32, 42, 41, 40, 30 }, // tile 13
            { 33, 34, 44, 43, 42, 32 }, // tile 14
            { 35, 36, 46, 45, 44, 34 }, // tile 15
            { 40, 41, 49, 48, 47, 39 }, // tile 16
            { 42, 43, 51, 50, 49, 41 }, // tile 17
            { 44, 45, 53, 52, 51, 43 } // tile 18
    };

    // private static final int[][] VERTICES = {
    // {0, 1, 2, 8, 9, 10}, // tile 0
    // {2, 3, 4, 10, 11, 12}, // tile 1
    // {4, 5, 6, 12, 13, 14}, // tile 2
    // {7, 8, 9, 17, 18, 19}, // tile 3
    // {9, 10, 11, 19, 20, 21}, // tile 4
    // {11, 12, 13, 21, 22, 23}, // tile 5
    // {13, 14, 15, 23, 24, 25}, // tile 6
    // {16, 17, 18, 27, 28, 29}, // tile 7
    // {18, 19, 20, 29, 30, 31}, // tile 8
    // {20, 21, 22, 31, 32, 33}, // tile 9
    // {22, 23, 24, 33, 34, 35}, // tile 10
    // {24, 25, 26, 35, 36, 37}, // tile 11
    // {28, 29, 30, 38, 39, 40}, // tile 12
    // {30, 31, 32, 40, 41, 42}, // tile 13
    // {32, 33, 34, 42, 43, 44}, // tile 14
    // {34, 35, 36, 44, 45, 46}, // tile 15
    // {39, 40, 41, 47, 48, 49}, // tile 16
    // {41, 42, 43, 49, 50, 51}, // tile 17
    // {43, 44, 45, 51, 52, 53} // tile 18 };

    private static final int[][] ADJACENCY = {
            { 1, 3, 4 }, // 0
            { 0, 2, 4, 5 }, // 1
            { 1, 5, 6 }, // 2
            { 0, 4, 7, 8 }, // 3
            { 0, 1, 3, 5, 8, 9 }, // 4
            { 1, 2, 4, 6, 9, 10 }, // 5
            { 2, 5, 10, 11 }, // 6
            { 3, 8, 12 }, // 7
            { 3, 4, 7, 9, 12, 13 }, // 8
            { 4, 5, 8, 10, 13, 14 }, // 9
            { 5, 6, 9, 11, 14, 15 }, // 10
            { 6, 10, 15 }, // 11
            { 7, 8, 13, 16 }, // 12
            { 8, 9, 12, 14, 16, 17 }, // 13
            { 9, 10, 13, 15, 17, 18 }, // 14
            { 10, 11, 14, 18 }, // 15
            { 12, 13, 17 }, // 16
            { 13, 14, 16, 18 }, // 17
            { 14, 15, 17 } // 18
    };

}
