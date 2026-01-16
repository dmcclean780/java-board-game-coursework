package com.example.model;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Map;
import java.util.Random;

import com.example.model.config.service.ConfigService;

public class Tiles {
    
    private Tile[] tiles;

    //Board size, change if a bigger/smaller board is desired
    //19 is a normal 3 hex-per-side board
    private final int NUMBER_OF_HEXES = 19;

    public Tiles(){
        this.tiles = setUpTiles();
    }

    //getter
    public Tile[] getTiles(){
        return this.tiles;
    }

    //setter
    public void setTiles(Tile[] _tiles){
        this.tiles = _tiles;
    }

    //set up all the tiles in the gameboard
    private Tile[] setUpTiles(){

        //getting tile config data for setup
        ArrayList<String> tileIDs = ConfigService.getAllTileIDs();

        //generally 19 tiles
        Tile[] tiles = new Tile[NUMBER_OF_HEXES];

        //instantiate tiles
        for (int i = 0; i < NUMBER_OF_HEXES; i++){ tiles[i] = new Tile(); }

        //set tile resources
        //19 tiles: 4 wood, 4 sheep, 4 wheat, 3 brick, 3 rock, 1 desert
        //max 4 of each:
        ArrayList<Integer> tileQuantities = new ArrayList<Integer>();
        for (String tileID : tileIDs){
            tileQuantities.add(ConfigService.getTile(tileID).maxQuantity);
        }

        Random random = new Random();
        for (int i = 0; i < NUMBER_OF_HEXES; i++){

            //repeat until valid
            boolean valid = false;
            while (!valid){
                //generate random number 0-number of resource types (exclusive)
                int num = random.nextInt(tileQuantities.size());
                
                int tileQuantity = tileQuantities.get(num);
                if (tileQuantity != 0){
                    tiles[i].setTileID(tileIDs.get(num));
                    tileQuantities.set(num, tileQuantity);
                    valid = true;
                }

            }
        }

        //tile resources now all set
        //assign numbers
        //also sets non-desert tiles to: not be desolate, not be blocked
        int[] numberSequence = generateTileNumberSequence();
        for (int i = 0; i < NUMBER_OF_HEXES; i++){
            //if tile is a desert, don't assign a number
            boolean passedDesert = false;
            if (ConfigService.getTile(tiles[i].getTileID()).resourceID == ""){
                passedDesert = true;
                tiles[i].setIsBlocked(true);
            }
            else{
                //assign number
                if (!passedDesert){ tiles[i].setNumber(numberSequence[i]); }
                else { tiles[i].setNumber(numberSequence[i - 1]); }
                tiles[i].setIsBlocked(false);
            }
        }

        //set adjacent vertices (so structures can be searched off of tile number)
        tiles = setAdjVerticesForEachTile(tiles);

        return tiles;
    }

    private int[] generateTileNumberSequence(){
        Map<Integer, Integer> numberTokens = ConfigService.getNumberTokens();
        int NUMBER_OF_TOKENS = numberTokens.values().stream().mapToInt(Integer::intValue).sum();
        int[] sequence = new int[NUMBER_OF_TOKENS];

        int sizeOfSequence = 0;
        for (int key : numberTokens.keySet()){
            int occurences = numberTokens.get(key);
            for (int i = 0; i < occurences; i++){
                sequence[sizeOfSequence] = key;
                sizeOfSequence++;
            }
        }

        return generateValidLayout(sequence);
    }

    private static final int MAX_ATTEMPTS = 100000;
    private static int[] generateValidLayout(int[] tokens) {
        Random rand = new Random();
        int[] layout = Arrays.copyOf(tokens, tokens.length);

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            shuffleArray(layout, rand);
            if (isValidLayout(layout)) {
                return layout;
            }
        }

        throw new RuntimeException("Couldn't find a valid layout after " + MAX_ATTEMPTS + " attempts");
    }

    private static boolean isValidLayout(int[] layout) {
        for (int i = 0; i < layout.length; i++) {
            if (layout[i] == 6 || layout[i] == 8) {
                for (int adj : ADJACENCY[i]) {
                    if (layout[adj] == 6 || layout[adj] == 8) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void shuffleArray(int[] array, Random rand) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    private Tile[] setAdjVerticesForEachTile(Tile[] tiles){
        for (int i = 0; i < VERTICES.length; i++){
            tiles[i].setAdjVertices(VERTICES[i]);
        }
        
        return tiles;
    }

    public static int[][] getTileVertices()
    {
        return VERTICES;
    }

    private static final int[][] VERTICES = {
        {0, 1, 2, 8, 9, 10},      // tile 0
        {2, 3, 4, 10, 11, 12},    // tile 1
        {4, 5, 6, 12, 13, 14},    // tile 2
        {7, 8, 9, 17, 18, 19},    // tile 3
        {9, 10, 11, 19, 20, 21},  // tile 4
        {11, 12, 13, 21, 22, 23}, // tile 5
        {13, 14, 15, 23, 24, 25}, // tile 6
        {16, 17, 18, 27, 28, 29}, // tile 7
        {18, 19, 20, 29, 30, 31}, // tile 8
        {20, 21, 22, 31, 32, 33}, // tile 9
        {22, 23, 24, 33, 34, 35}, // tile 10
        {24, 25, 26, 35, 36, 37}, // tile 11
        {28, 29, 30, 38, 39, 40}, // tile 12
        {30, 31, 32, 40, 41, 42}, // tile 13
        {32, 33, 34, 42, 43, 44}, // tile 14
        {34, 35, 36, 44, 45, 46}, // tile 15
        {39, 40, 41, 47, 48, 49}, // tile 16
        {41, 42, 43, 49, 50, 51}, // tile 17
        {43, 44, 45, 51, 52, 53}  // tile 18
    };

    private static final int[][] ADJACENCY = {
        {1, 3, 4},           // 0
        {0, 2, 4, 5},        // 1
        {1, 5, 6},           // 2
        {0, 4, 7, 8},        // 3
        {0, 1, 3, 5, 8, 9},  // 4
        {1, 2, 4, 6, 9, 10}, // 5
        {2, 5, 10, 11},      // 6
        {3, 8, 12},          // 7
        {3, 4, 7, 9, 12, 13},// 8
        {4, 5, 8, 10, 13, 14},// 9
        {5, 6, 9, 11, 14, 15},// 10
        {6, 10, 15},         // 11
        {7, 8, 13, 16},      // 12
        {8, 9, 12, 14, 16, 17},// 13
        {9, 10, 13, 15, 17, 18},// 14
        {10, 11, 14, 18},    // 15
        {12, 13, 17},        // 16
        {13, 14, 16, 18},    // 17
        {14, 15, 17}         // 18
    };
    
}
